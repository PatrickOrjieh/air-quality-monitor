from threading import Thread
from flask import current_app
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from pubnub.callbacks import SubscribeCallback
from pubnub.enums import PNStatusCategory
import os
import mysql.connector
from dotenv import load_dotenv
import json
import requests

load_dotenv()

# Define thresholds for all parameters
TEMP_THRESHOLD = {"Good": 18, "Moderate": 25, "Bad": 30}
HUMIDITY_THRESHOLD = {"Good": 30, "Moderate": 50, "Bad": 70}
GAS_RESISTANCE_THRESHOLD = {"Good": 4000, "Moderate": 6000, "Bad": 10000}
PM_2_5_THRESHOLD = {"Good": 12, "Moderate": 35.4, "Bad": 55.4}
PM_10_THRESHOLD = {"Good": 54, "Moderate": 154, "Bad": 254}

def send_fcm_notification(token, title, body):
    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + os.getenv('FCM_SERVER_KEY')
    }
    payload = {
        'notification': {
            'title': title,
            'body': body
        },
        'to': token
    }
    response = requests.post('https://fcm.googleapis.com/fcm/send', headers=headers, json=payload)
    return response.json()

def calculate_air_quality_score(data):
    #calculate air quality score
    temp_score = score_parameter(data['temperature'], TEMP_THRESHOLD)
    humidity_score = score_parameter(data['humidity'], HUMIDITY_THRESHOLD)
    gas_score = score_parameter(data['gas_resistance'], GAS_RESISTANCE_THRESHOLD, reverse=True)
    pm2_5_score = score_parameter(data['pm2_5'], PM_2_5_THRESHOLD)
    pm10_score = score_parameter(data['pm10'], PM_10_THRESHOLD)

    # Calculate overall air quality score
    total_score = temp_score + humidity_score + gas_score + pm2_5_score + pm10_score
    air_quality_percentage = total_score / 5

    return air_quality_percentage

def score_parameter(value, thresholds, reverse=False):
    if reverse:
        # Higher values are better
        if value >= thresholds["Good"]:
            return 100
        elif value >= thresholds["Moderate"]:
            return 60
        else:
            return 40
    else:
        # Lower values are better
        if value <= thresholds["Good"]:
            return 100
        elif value <= thresholds["Moderate"]:
            return 60
        else:
            return 40

class MySubscribeCallback(SubscribeCallback):
    def message(self, pubnub, message):
        # Decrypt and process message here
        decrypted_message = message.message
        # print(f"Received message: {decrypted_message}")
        store_data(decrypted_message)

    def status(self, pubnub, status):
        if status.category == PNStatusCategory.PNConnectedCategory:
            print("Connected to PubNub")

def store_data(data):
    # Connect to the database
    try:
        connection = mysql.connector.connect(
            host=os.getenv('MYSQL_HOST'),
            user=os.getenv('MYSQL_USER'),
            password=os.getenv('MYSQL_PASSWORD'),
            database=os.getenv('MYSQL_DB_NAME')
        )
        cursor = connection.cursor(buffered=True)  # Use buffered cursor
        print("Connected to database")
    except Exception as e:
        print(f"Error connecting to database: {e}")
        return

    try:
        # Using the modelNumber from the data, get the hubID
        cursor.execute('SELECT hubID FROM Hub WHERE modelNumber = %s', (data['modelNumber'],))
        result = cursor.fetchone()
        if result:
            hubID = result[0]
            # Insert data into the database
            query = """
                INSERT INTO AirQualityMeasurement (
                    hubID, PM1, PM2_5, PM10, VOC, temperature, humidity, gas_resistance, pollenCount
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            values = (
                hubID, data['pm1'], data['pm2_5'], data['pm10'], data['voc'], data['temperature'],
                data['humidity'], data['gas_resistance'], 2
            )
            cursor.execute(query, values)
            connection.commit()

            # Calculate air quality score and determine category
            air_quality_percentage = calculate_air_quality_score(data)
            air_quality_category = determine_air_quality_category(air_quality_percentage)

            #Use the hubID to get the userID in the Hub table and use to insert into Notification table
            cursor.execute('SELECT userID FROM Hub WHERE hubID = %s', (hubID,))
            result = cursor.fetchone()
            if result:
                userID = result[0]
                # Generate notification
                heading, message = generate_notification(data, air_quality_category)
                if heading and message:
                    query = """
                        INSERT INTO Notification (
                            userID, heading, message
                        ) VALUES (%s, %s, %s)
                    """
                    values = (
                        userID, heading, message
                    )
                    cursor.execute(query, values)
                    connection.commit()
                    print("Notification generated")
                    # Send FCM notification
                    cursor.execute('SELECT fcmToken FROM User WHERE userID = %s', (userID,))
                    user_result = cursor.fetchone()
                    if user_result:
                        fcm_token = user_result[0]
                        # Send FCM notification
                        send_fcm_notification(fcm_token, heading, message)
                        print("FCM Notification sent")
                else:
                    print("No notification generated")
            else:
                print("User not found")
        else:
            print("Hub not found")
        # Insert location data if present
        if 'latitude' in data and 'longitude' in data:
            insert_location_data(hubID, data['latitude'], data['longitude'])
            
    except mysql.connector.Error as err:
        print(f"Error: {err}")
    finally:
        cursor.close()
        connection.close()

def insert_location_data(hubID, latitude, longitude):
    try:
        connection = mysql.connector.connect(
            host=os.getenv('MYSQL_HOST'),
            user=os.getenv('MYSQL_USER'),
            password=os.getenv('MYSQL_PASSWORD'),
            database=os.getenv('MYSQL_DB_NAME')
        )
        cursor = connection.cursor()
        query = """
            INSERT INTO Location (hubID, latitude, longitude)
            VALUES (%s, %s, %s)
        """
        cursor.execute(query, (hubID, latitude, longitude))
        connection.commit()
    except mysql.connector.Error as err:
        print(f"Error inserting location data: {err}")
    finally:
        cursor.close()
        connection.close()

def determine_air_quality_category(percentage):
    if percentage >= 85:
        return "Good"
    elif percentage >= 60:
        return "Moderate"
    elif percentage >= 40:
        return "Bad"
    else:
        return "Very Poor"
    
def generate_notification(data, category):
    if category in ["Bad", "Very Poor"]:
        # Check which parameter is causing poor air quality
        if data['temperature'] > TEMP_THRESHOLD["Bad"]:
            heading = f"{category} Air Temperature"
            message = "High temperatures can aggravate asthma symptoms. It's advised to stay in cool, air-conditioned environments and stay hydrated. Avoid outdoor activities during peak heat hours, and always carry your inhaler. Stay safe!"
        elif data['humidity'] > HUMIDITY_THRESHOLD["Bad"]:
            heading = f"{category} Humidity"
            message = "High humidity levels can worsen asthma symptoms. Try to stay indoors in air-conditioned spaces, keep well-hydrated, and avoid strenuous outdoor activities. Always have your asthma medication handy for any emergencies. Take care!"
        elif data['pm2_5'] > PM_2_5_THRESHOLD["Bad"]:
            heading = f"{category} PM2.5 Particles"
            message = "Stay indoors as much as possible, use an air purifier if available, and avoid outdoor exercise. Always keep your asthma medication within reach. Stay safe and take care of your respiratory health"
        elif data['pm10'] > PM_10_THRESHOLD["Bad"]:
            heading = f"{category} PM10 Particles"
            message = "It's best to stay indoors, especially during times of peak air pollution. Use air purifiers to maintain indoor air quality and avoid physical exertion outdoors. Ensure your asthma medication is easily accessible for immediate use. Take care and protect your lungs."
        else:
            heading = f"{category} Air Quality"
            message = "The Air Quality is bad. It's best to stay indoors, especially during times of peak air pollution. Use air purifiers to maintain indoor air quality and avoid physical exertion outdoors. Ensure your asthma medication is easily accessible for immediate use. Take care and protect your lungs."

        return heading, message
    return None, None

def start_pubnub_listener(app):
    with app.app_context():
        print("Initializing PubNub Listener")
        # Initialize PubNub inside the context
        pnconfig = PNConfiguration()
        pnconfig.subscribe_key = os.getenv('PUBNUB_SUBSCRIBE_KEY')
        pnconfig.publish_key = os.getenv('PUBNUB_PUBLISH_KEY')
        pnconfig.user_id = os.getenv('PUBNUB_USER_ID')
        pnconfig.cipher_key = os.getenv('PUBNUB_CIPHER_KEY')
        pubnub = PubNub(pnconfig)
        
        try:
            pubnub.add_listener(MySubscribeCallback())
            pubnub.subscribe().channels('aerosense_channel').execute()
            # print("PubNub listener started")
        except Exception as e:
            print(f"Error in PubNub listener: {e}")

def start_background_task(app):
    # print("Starting background task")
    thread = Thread(target=start_pubnub_listener, args=(app,))
    thread.daemon = True
    thread.start()

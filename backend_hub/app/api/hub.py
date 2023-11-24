from flask import jsonify, request
from app import app, mysql
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from dotenv import load_dotenv
import os

# Load environment variables form .env file
load_dotenv()
basedir = os.path.abspath(os.path.dirname(__file__))
load_dotenv(os.path.join(basedir, 'backend_hub\.env'))


# Configure PubNub
pnconfig = PNConfiguration()
pnconfig.subscribe_key = os.getenv('PUBNUB_SUBSCRIBE_KEY')
pnconfig.publish_key = os.getenv('PUBNUB_PUBLISH_KEY')
pnconfig.user_id = os.getenv('PUBNUB_USER_ID')
pnconfig.cipher_key = os.getenv('PUBNUB_CIPHER_KEY')
pubnub = PubNub(pnconfig)

def publish_message(channel, message):
    pubnub.publish().channel(channel).message(message).sync()

@app.route('/api/settings/<int:user_id>', methods=['GET'])
def get_user_settings(user_id):
    cursor = mysql.connection.cursor()
    cursor.execute('SELECT * FROM UserSetting WHERE userID = %s', (user_id,))
    settings = cursor.fetchone()
    cursor.close()

    if settings:
        return jsonify({
            "settingID": settings[0],
            "userID": settings[1],
            "notificationFrequency": settings[2],
            "vibration": settings[3],
            "sound": settings[4],
        }), 200
    else:
        return jsonify({"error": "Settings not found"}), 404

@app.route('/api/settings/<int:user_id>', methods=['POST'])
def update_user_settings(user_id):
    data = request.get_json()
    notification_frequency = data.get('notificationFrequency')
    vibration = data.get('vibration')
    sound = data.get('sound')

    cursor = mysql.connection.cursor()
    cursor.execute('''
        UPDATE UserSetting 
        SET notificationFrequency = %s, vibration = %s, sound = %s 
        WHERE userID = %s
        ''', (notification_frequency, vibration, sound, user_id))
    mysql.connection.commit()

    # After updating settings, publish to PubNub
    message = {
        'userID': user_id,
        'notificationFrequency': notification_frequency,
        'vibration': vibration,
        'sound': sound
    }
    publish_message('user_settings_channel', message)

    cursor.close()
    return jsonify({"message": "Settings updated successfully"}), 200

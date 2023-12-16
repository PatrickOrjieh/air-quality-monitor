from flask import jsonify, request
from app import app, mysql
import firebase_admin
from firebase_admin import auth
from datetime import datetime

@app.route('/api/locations', methods=['GET'])
def get_hub_locations():
    # Retrieve the Firebase token from the request headers
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        # Verify the Firebase ID token and get the user's information
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        # Use the Firebase UID to find the associated user ID in the database
        cursor = mysql.connection.cursor()
        cursor.execute('SELECT userID FROM User WHERE firebaseUID = %s', (firebase_user_id,))
        user_result = cursor.fetchone()

        if not user_result:
            return jsonify({'error': 'User not found in the database'}), 404

        user_id = user_result[0]

        # Fetch the hub ID associated with the user ID
        cursor.execute('SELECT hubID FROM Hub WHERE userID = %s', (user_id,))
        hub_result = cursor.fetchone()

        if not hub_result:
            return jsonify({'error': 'No hub associated with this user in the database'}), 404

        hub_id = hub_result[0]

        # Fetch the location data for the user's hub for the current day
        today_date = datetime.now().strftime('%Y-%m-%d')
        cursor.execute('''SELECT latitude, longitude FROM Location 
                          WHERE hubID = %s AND DATE(createdAt) = %s''', 
                          (hub_id, today_date))
        locations = cursor.fetchall()
        cursor.close()

        response_data = [{
            'latitude': lat,
            'longitude': lon
        } for lat, lon in locations]

        return jsonify(response_data), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500

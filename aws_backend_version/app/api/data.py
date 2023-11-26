from flask import jsonify, request
from app import app, db
from app.models import User, Hub, AirQualityMeasurement
import firebase_admin
from firebase_admin import auth
from sqlalchemy import desc

@app.route('/api/home', methods=['GET'])
def get_air_quality_data():
    # Firebase token verification
    token = request.headers.get('Authorization')
    if not token:
        return jsonify({'error': 'Authorization token is missing'}), 401

    try:
        # Verify the Firebase ID token and get the user's information
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        # Fetch the user associated with the Firebase UID
        user = User.query.filter_by(firebaseUID=firebase_user_id).first()

        if not user:
            return jsonify({'error': 'User not found'}), 404

        # Fetch the hub associated with the user
        hub = Hub.query.filter_by(userID=user.userID).first()

        if not hub:
            return jsonify({'error': 'No hub associated with this user'}), 404

        # Fetch the latest air quality measurement for the user's hub
        result = AirQualityMeasurement.query.filter_by(hubID=hub.hubID).order_by(desc(AirQualityMeasurement.timestamp)).first()

        if result:
            response_data = {
                'pm25': result.PM2_5,
                'pm10': result.PM10,
                'voc_level': result.VOC,
                'temperature': result.temperature,
                'humidity': result.humidity,
                'last_updated': result.timestamp.strftime('%I:%M%p')
            }
            return jsonify(response_data), 200
        else:
            return jsonify({'message': 'No air quality data available for this hub.'}), 404

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token'}), 401
    except Exception as e:
        return jsonify({'error': str(e)}), 500

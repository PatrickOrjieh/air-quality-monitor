from flask import jsonify, request
from app import app, db
from app.models import User, Hub, Location
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

        # Fetch the user from the database using the Firebase UID
        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if not user:
            return jsonify({'error': 'User not found in the database'}), 404

        # Fetch the user's associated hub from the database
        hub = Hub.query.filter_by(userID=user.userID).first()
        if not hub:
            return jsonify({'error': 'No hub associated with this user in the database'}), 404

        # Fetch location data for the user's hub for the current day
        today_date = datetime.now().date()
        locations = Location.query.filter_by(hubID=hub.hubID).filter(Location.createdAt >= today_date).all()

        response_data = [{'latitude': loc.latitude, 'longitude': loc.longitude} for loc in locations]
        return jsonify(response_data), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500

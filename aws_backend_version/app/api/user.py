from flask import request, jsonify
from app import app, db
from app.models import User, Hub
from werkzeug.security import generate_password_hash
import firebase_admin
from firebase_admin import auth, credentials
import re

# Initialize Firebase
cred = credentials.Certificate("/var/www/Aerosense/Aerosense/aerosense.json")
firebase_admin.initialize_app(cred)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/api/register', methods=['POST'])
def register_user():
    data = request.get_json()
    name = data.get('name', '').strip()
    password = data.get('password', '').strip()
    confirm_password = data.get('confirmPassword', '').strip()
    email = data.get('email', '').strip()
    model_number = data.get('modelNumber', '').strip()

    # Input validation
    if not name or not email or not model_number or not password or not confirm_password:
        return jsonify({"error": "Missing required fields"}), 400
    if not re.match(r"[^@]+@[^@]+\.[^@]+", email):
        return jsonify({"error": "Invalid email format"}), 400
    
    if password != confirm_password:
        return jsonify({"error": "Passwords do not match"}), 400

    hashed_password = generate_password_hash(password)

    try:
        user_record = auth.create_user(email=email)
        firebase_user_id = user_record.uid

        new_user = User(name=name, email=email, password=hashed_password, firebaseUID=firebase_user_id)
        db.session.add(new_user)
        db.session.commit()

        new_hub = Hub(modelNumber=model_number, userID=new_user.userID, batteryLevel=100)
        db.session.add(new_hub)
        db.session.commit()

        return jsonify({"message": "User registered successfully", "firebaseUID": firebase_user_id, "userID": new_user.userID}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/api/login2', methods=['POST'])
def login_user2():
    data = request.get_json()
    email = data.get('email', '').strip()

    if not email:
        return jsonify({"error": "Email is required"}), 400
    if not re.match(r"[^@]+@[^@]+\.[^@]+", email):
        return jsonify({"error": "Invalid email format"}), 400

    try:
        user_record = auth.get_user_by_email(email)
        firebase_user_id = user_record.uid

        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if user is None:
            return jsonify({'error': 'User does not exist'}), 400

        return jsonify({"message": "User logged in successfully", "firebaseUID": firebase_user_id}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 400
    
@app.route('/api/login', methods=['POST'])
def login_user():
    data = request.get_json()
    id_token = data.get('idToken')

    if not id_token:
        return jsonify({'error': 'ID token is required'}), 400

    try:
        # Verify the Firebase ID token
        decoded_token = auth.verify_id_token(id_token)
        firebase_user_id = decoded_token['uid']

        # Retrieve user data from database
        user = User.query.filter_by(firebaseUID=firebase_user_id).first()

        if user is None:
            return jsonify({'error': 'User does not exist'}), 400

        return jsonify({"message": "User logged in successfully", "firebaseUID": firebase_user_id}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 400

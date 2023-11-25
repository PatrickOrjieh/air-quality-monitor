from flask import request, jsonify
from app import app, mysql
import firebase_admin
from firebase_admin import auth, credentials
from werkzeug.security import generate_password_hash
import re

cred = credentials.Certificate("./aerosense.json")
firebase_admin.initialize_app(cred)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/api/register', methods=['POST'])
def register_user():
    data = request.get_json()
    name = data.get('name', '').strip()
    email = data.get('email', '').strip()
    password = data.get('password', '')
    confirm_password = data.get('confirmPassword', '')
    model_number = data.get('modelNumber', '').strip()

    # Input validation
    if not name or not email or not model_number:
        return jsonify({"error": "Missing required fields"}), 400
    if password != confirm_password:
        return jsonify({"error": "Passwords do not match"}), 400
    if not re.match(r"[^@]+@[^@]+\.[^@]+", email):
        return jsonify({"error": "Invalid email format"}), 400

    hashed_password = generate_password_hash(password)

    try:
        user_record = auth.create_user(email=email, password=password)
        firebase_user_id = user_record.uid

        cursor = mysql.connection.cursor()
        cursor.execute('INSERT INTO User (name, email, password, firebaseUID) VALUES (%s, %s, %s, %s)', (name, email, hashed_password, firebase_user_id))
        mysql.connection.commit()

        user_id = cursor.lastrowid
        cursor.execute('INSERT INTO Hub (modelNumber, userID, batteryLevel) VALUES (%s, %s, %s)', (model_number, user_id, 100))
        mysql.connection.commit()
        cursor.close()

        return jsonify({"message": "User registered successfully", "firebaseUID": firebase_user_id, "userID": user_id}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 400


#there is no need for this route because the firebase user id is already stored in the database
#and firebase handles the login but I will leave it here 
@app.route('/api/login', methods=['POST'])
def login_user():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    # Firebase user login
    try:
        user = auth.get_user_by_email(email)
        firebase_user_id = user.uid
    except Exception as e:
        return jsonify({'error': str(e)}), 400

    # Check if user exists in MySQL database
    cursor = mysql.connection.cursor()
    cursor.execute('SELECT * FROM User WHERE firebaseUID = %s', (firebase_user_id,))
    user = cursor.fetchone()
    cursor.close()

    if user is None:
        return jsonify({'error': 'User does not exist'}), 400

    return jsonify({"message": "User logged in successfully", "firebaseUID": firebase_user_id}), 200

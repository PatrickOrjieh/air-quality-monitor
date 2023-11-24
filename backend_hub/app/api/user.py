from flask import request, jsonify
from app import app, mysql
import firebase_admin
from firebase_admin import auth, credentials
from werkzeug.security import generate_password_hash

cred = credentials.Certificate("./aerosense.json")
firebase_admin.initialize_app(cred)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/api/register', methods=['POST'])
def register_user():
    data = request.get_json()
    name = data.get('name')
    email = data.get('email')
    password = data.get('password')
    confirm_password = data.get('confirmPassword')
    model_number = data.get('modelNumber')

    if password != confirm_password:
        return jsonify({"error": "Passwords do not match"}), 400

    hashed_password = generate_password_hash(password)

    try:
        user_record = auth.create_user(email=email, password=password)
        firebase_user_id = user_record.uid
    except Exception as e:
        return jsonify({'error': str(e)}), 400

    cursor = mysql.connection.cursor()
    cursor.execute('INSERT INTO User (name, email, password, firebaseUID) VALUES (%s, %s, %s, %s)', (name, email, hashed_password, firebase_user_id))
    mysql.connection.commit()
    cursor.close()

    # Insert the hub into MySQL database, with the User's ID, model number, battery level
    #first get the user id
    cursor = mysql.connection.cursor()
    cursor.execute('SELECT userID FROM User WHERE firebaseUID = %s', (firebase_user_id,))
    user_id = cursor.fetchone()[0]
    cursor.close()

    #insert the hub
    cursor = mysql.connection.cursor()
    cursor.execute('INSERT INTO Hub (modelNumber, userID, batteryLevel) VALUES (%s, %s, %s)', (model_number, user_id, 100))
    mysql.connection.commit()
    cursor.close()

    return jsonify({"message": "User registered successfully", "firebaseUID": firebase_user_id}), 201

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

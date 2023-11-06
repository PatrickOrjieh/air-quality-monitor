from flask import Flask, request, jsonify
import os
from flask_mysqldb import MySQL
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)

app.config['MYSQL_HOST'] = os.getenv('MYSQL_HOST')
app.config['MYSQL_USER'] = os.getenv('MYSQL_USER')
app.config['MYSQL_PASSWORD'] = os.getenv('MYSQL_PASSWORD')
app.config['MYSQL_DB'] = os.getenv('MYSQL_DB_NAME')

mysql = MySQL(app)

@app.route('/')
def index():
    return 'Hello World!'

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()

    #basic input validation
    if not all(key in data for key in ('name', 'email', 'password','confirmPassword', 'model')):
        return jsonify({'error': 'Missing parameters'}), 400
    
    if data['password'] != data['confirmPassword']:
        return jsonify({'error': 'Passwords do not match'}), 400
    
    #hash the password
    hashed_password = generate_password_hash(data['password'], method='sha256')

    # Look for the wristband based on provided model
    cursor = mysql.connection.cursor()
    cursor.execute("SELECT id FROM wristband WHERE model = %s", (data['model'],))
    wristband = cursor.fetchone()
    if wristband is None:
        cursor.close()
        return jsonify({'error': 'Wristband not found'}), 400
    
    try:
        sql = "INSERT INTO user (name, email, password, wristband_id) VALUES (%s, %s, %s, %s)"
        cursor.execute(sql, (data['name'], data['email'], hashed_password, wristband['id']))
        mysql.connection.commit()
    except Exception as e:
        cursor.close()
        return jsonify({'error': 'An error occured' + str(e)}), 400
    
    cursor.close()
    return jsonify({'message': 'User created successfully'}), 201

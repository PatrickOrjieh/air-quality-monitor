#Starter code from sprts+registrants example
#Github CoPilot was used for parts while writing the below code

import re
from flask import Flask, render_template, request, redirect
import os
from flask_mysqldb import MySQL

app = Flask(__name__)

app.config['MYSQL_HOST'] = os.getenv('MYSQL_HOST')
app.config['MYSQL_USER'] = os.getenv('MYSQL_USER')
app.config['MYSQL_PASSWORD'] = os.getenv('MYSQL_PASSWORD')
app.config['MYSQL_DB'] = os.getenv('MYSQL_DB')

mysql = MySQL(app)

@app.route('/')
def index():
    return render_template('index.html')


@app.route('/register', methods=['POST'])
def register():
    #Server side validation
    name = request.form.get('name')
    email = request.form.get('email')
    phone = request.form.get('phone')
    password = request.form.get('password')

    if not name:
        return render_template('error.html', message="Missing name")
    if not email:
        return render_template('error.html', message="Missing email")
    if not phone:
        return render_template('error.html', message="Missing phone")
    if not password:
        return render_template('error.html', message="Missing password")
    
    # Email validation
    if not re.match(r"[^@]+@[^@]+\.com$", email):
        return render_template('error.html', message="Invalid email format")
    
    # Phone validation
    if not phone.isdigit():
        return render_template('error.html', message="Phone number must contain only digits")
    
    # Password validation
    if len(password) < 8:
        return render_template('error.html', message="Password must be at least 8 characters long")
    if not re.search("[A-Z]", password):
        return render_template('error.html', message="Password must contain at least one capital letter")
    if not re.search("[@#$%^&+=]", password):
        return render_template('error.html', message="Password must contain at least one special character")
    
    cursor = mysql.connection.cursor()
    cursor.execute("Select UserID from users where Email = %s", 
                   (email,))
    result = cursor.fetchall()
    if len(result) != 0:
        cursor.close()
        return render_template('error.html', message="You have already registered")  
    cursor.execute("INSERT INTO users (Name, Email, Phone, Password) VALUES (%s, %s, %s, %s)", 
                   (name, email, phone, password)) 
    mysql.connection.commit()
    cursor.close() 
    return redirect('/registrants')


@app.route('/registrants')
def registrants():
    cursor = mysql.connection.cursor()
    cursor.execute("SELECT * FROM users")
    users = cursor.fetchall()
    cursor.close()
    return render_template('registrants.html', users=users)


@app.route('/deregister', methods=['POST'])
def deregister():
    id = request.form.get('id')
    if id:
        cursor = mysql.connection.cursor()
        cursor.execute("DELETE FROM users WHERE id = %s", (id))
        mysql.connection.commit()
        cursor.close()
    return redirect('/registrants')
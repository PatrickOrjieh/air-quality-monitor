from app import app, db
from flask import request, redirect, url_for, render_template, session, flash, jsonify
from werkzeug.security import check_password_hash, generate_password_hash
from app.models import User, Hub
from .. import pb
import time

app.secret_key = b'\xdc*\x0e\xff\xa8\x0c5\x15\xd4\x93>\x8ap\xe0R8\x9d\x05\xef\xf0\x84\xd7Z\x0e'

# Hardcoded credentials (hashed for added security)
ADMIN_USERNAME = 'D00251785'
ADMIN_PASSWORD_HASH = generate_password_hash('8GZsOo&mF@@kf#Kh')

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/admin_login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']

        print(username, password)
        
        if username == ADMIN_USERNAME and check_password_hash(ADMIN_PASSWORD_HASH, password):
            session['admin_logged_in'] = True
            return redirect(url_for('admin_dashboard'))
        else:
            flash('Invalid credentials', 'danger') 
    return render_template("admin_login.html")

@app.route('/admin_dashboard')
def admin_dashboard():
    if not session.get('admin_logged_in'):
        flash('You need to login first', 'danger')
        return redirect(url_for('login'))
    
    users = User.query.all()
    hubs = Hub.query.all()
    return render_template('admin_dashboard.html', users=users, hubs=hubs)

@app.route('/api/request_token', methods=['POST'])
def request_token():
    data = request.get_json()
    model_number = data.get('model_number')

    #get the write token from the hub based on the model number
    hub = Hub.query.filter_by(modelNumber=model_number).first()
    if not hub:
        return jsonify({"error": "Hub not found"}), 400
    
    write_token = hub.write_token

    print(write_token)

    hub_id = hub.hubID

    writeToken = get_or_refresh_token(hub_id, write_token)
    return jsonify({"token": writeToken}), 200

@app.route('/update_access_user', methods=['POST'])
def update_access_user():
    if not session.get('admin_logged_in'):
        flash('You need to login first', 'danger')
        return redirect(url_for('login'))
    
    user_id = request.form.get('user_id')
    read_access = request.form.get('read') == '1'
    write_access = request.form.get('write') == '2'

    # Calculate the new access level
    new_access_level = 0
    if read_access:
        new_access_level = 1
    if write_access:
        new_access_level = 2 

    # Fetch the user from the database
    user = User.query.get(user_id)
    if user:
        # Update the user's access level
        user.access_level = new_access_level
        db.session.commit()
        flash('Access level updated successfully.', 'success')
    else:
        flash('User not found.', 'danger')

    return redirect(url_for('admin_dashboard'))

def get_or_refresh_token(user_id,token):
    timestamp, ttl, uuid, read, write = pb.parse_token(token, "aerosense_channel")
    current_time = time.time()
    if (timestamp + (ttl*60)) - current_time > 0:
        print("token still valid for " + str((timestamp + (ttl*60)) - current_time) + " seconds")
        return token
    else:
        message, new_token = grant_access(user_id, read, write)
        print("token expired, new token: " + new_token)
        return new_token

def grant_access(hub_id, read, write):
    hub = Hub.query.get(hub_id)
    if not hub:
        return "Hub not found", None
    
    model_number = hub.modelNumber

    if write:
        new_token = pb.grant_read_write_access(model_number, "aerosense_channel")
        hub.write_token = new_token
        hub.read_token = new_token
    elif read:
        new_token = pb.grant_read_access(model_number, "aerosense_channel")
        hub.read_token = new_token
        hub.write_token = None
    elif not read and not write:
        new_token = None
        hub.write_token = None
        hub.read_token = None
    else:
        # if hub.write_token:
        #     pb.revoke_access(hub.write_token)
        hub.write_token = None
        hub.read_token = None
        return "Access revoked", None

    db.session.commit()
    return "Access updated", new_token

@app.route('/update_access_hub', methods=['POST'])
def update_access_hub():
    if not session.get('admin_logged_in'):
        flash('You need to login first', 'danger')
        return redirect(url_for('login'))
    
    hub_id = request.form.get('hub_id')
    read_access = request.form.get('read') == '1'
    write_access = request.form.get('write') == '2'

    # Calculate the new access level
    new_access_level = 0
    if read_access:
        new_access_level = 1
    if write_access:
        new_access_level = 2 

    # Fetch the hub from the database
    hub = Hub.query.get(hub_id)
    if hub:
        # Update the hub's access level
        hub.access_level = new_access_level
        db.session.commit()

        message, token = grant_access(hub_id, read_access, write_access)
        flash('Access level updated successfully.', 'success')
    else:
        flash('Hub not found.', 'danger')

    return redirect(url_for('admin_dashboard'))

@app.route('/admin_logout', methods=['POST'])
def admin_logout():
    session.pop('admin_logged_in', None)
    flash('You have been logged out', 'success')
    return redirect(url_for('login'))
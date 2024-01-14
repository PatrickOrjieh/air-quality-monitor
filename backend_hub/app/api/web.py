from app import app
from flask import request, redirect, url_for, render_template, session, flash
from werkzeug.security import check_password_hash, generate_password_hash

app.secret_key = b'\xdc*\x0e\xff\xa8\x0c5\x15\xd4\x93>\x8ap\xe0R8\x9d\x05\xef\xf0\x84\xd7Z\x0e'

# Hardcoded credentials (hashed for added security)
ADMIN_USERNAME = 'D00251785'
ADMIN_PASSWORD_HASH = generate_password_hash('8GZsOo&mF@@kf#Kh')

@app.route('/')
def index():
    return "Hello World!"

@app.route('/admin_login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        
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
    
    # Render the admin dashboard template
    return render_template('admin_dashboard.html')

@app.route('/logout')
def logout():
    session.pop('admin_logged_in', None)
    flash('You have been logged out', 'success')
    return redirect(url_for('login'))
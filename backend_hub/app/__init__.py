from flask import Flask
from flask_mysqldb import MySQL
from dotenv import load_dotenv
import os

load_dotenv()
#set the location of the .env file
basedir = os.path.abspath(os.path.dirname(__file__))
load_dotenv(os.path.join(basedir, 'backend_hub\.env'))


print(os.getenv('MYSQL_HOST'))  # Should print the MySQL host if loaded correctly


app = Flask(__name__)

# Database Configuration from .env file
app.config['MYSQL_HOST'] = os.getenv('MYSQL_HOST')
app.config['MYSQL_USER'] = os.getenv('MYSQL_USER')
app.config['MYSQL_PASSWORD'] = os.getenv('MYSQL_PASSWORD')
app.config['MYSQL_DB'] = os.getenv('MYSQL_DB_NAME')

mysql = MySQL(app)

from app.api import user

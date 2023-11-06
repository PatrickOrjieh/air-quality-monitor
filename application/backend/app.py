# a simple flask app to just display hello world
# run with: python app.py

from flask import Flask
app = Flask(__name__)

@app.route('/')
def index():
    return "Hello World!"


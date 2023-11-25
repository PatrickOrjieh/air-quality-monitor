from flask import jsonify
from app import app, mysql

@app.route('/api/home', methods=['GET'])
def get_air_quality_data():
    try:
        cursor = mysql.connection.cursor()
        cursor.execute('''SELECT * FROM AirQualityMeasurement 
                          ORDER BY timestamp DESC 
                          LIMIT 1''')
        row_headers = [x[0] for x in cursor.description]
        result = cursor.fetchone()
        cursor.close()

        if result:
            data = dict(zip(row_headers, result))
            response_data = {
                'pm25': data['PM2_5'],
                'pm10': data['PM10'],
                'voc_level': data['VOC'],
                'temperature': data['temperature'],
                'humidity': data['humidity'],
                'last_updated': data['timestamp'].strftime('%I:%M%p')
            }
            return jsonify(response_data), 200
        else:
            return jsonify({'message': 'No air quality data available.'}), 404

    except Exception as e:
        return jsonify({'error': str(e)}), 500

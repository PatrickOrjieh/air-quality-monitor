from flask import jsonify
from app import app, db
from app.models import AirQualityMeasurement
from sqlalchemy import desc

@app.route('/api/home', methods=['GET'])
def get_air_quality_data():
    try:
        result = AirQualityMeasurement.query.order_by(desc(AirQualityMeasurement.timestamp)).first()

        if result:
            response_data = {
                'pm25': result.PM2_5,
                'pm10': result.PM10,
                'voc_level': result.VOC,
                'temperature': result.temperature,
                'humidity': result.humidity,
                'last_updated': result.timestamp.strftime('%I:%M%p')
            }
            return jsonify(response_data), 200
        else:
            return jsonify({'message': 'No air quality data available.'}), 404

    except Exception as e:
        return jsonify({'error': str(e)}), 500

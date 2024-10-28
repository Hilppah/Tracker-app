from flask import Flask, flash, request, jsonify
from flask_mysqldb import MySQL

app = Flask(__name__)
app.secret_key = "super secret key"

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = ''
app.config['MYSQL_DB'] = 'kotlin_sovellus'

mysql = MySQL (app)

@app.route('/register', methods = [ 'POST'])
def register():
    if request.method == 'POST':
        email = request.get_json().get('email')
        password = request.get_json().get('password')
        if not email or not password:
            return jsonify({'error': 'Email and password are required.'}), 400

        cursor = mysql.connection.cursor()
        register_query = "INSERT INTO users ( password, email) VALUES (%s, %s)"

        try:
            cursor.execute(register_query, ( password, email))
            mysql.connection.commit()
            flash('Registration successful! Please log in.', 'success')
            return jsonify({ 'message': 'User successfully registered'}), 201   
        except Exception as e:
            mysql.connection.rollback()
            return jsonify({'error': str(e)}), 500
        finally:
            cursor.close()

    return jsonify({'error': 'Invalid request method'}), 405

@app.route('/login', methods=['POST'])
def login():
    if request.method == 'POST':
        email = request.get_json().get('email')
        password = request.get_json().get('password')
        if not email or not password:
            return jsonify({'error': 'Email and password are required.'}), 400

        cursor = mysql.connection.cursor()
        login_query = "SELECT * FROM users WHERE email = %s"

        try:
            cursor.execute(login_query, (email,))
            user = cursor.fetchone()
            print(user)
            if user:
                stored_password = user[2]
                if password == stored_password:
                    flash('Login successful!', 'success')
                    return jsonify({'message': 'Login successful!'}), 200
                else:
                    return jsonify({'error': 'Invalid password'}), 401
            else:
                return jsonify({'error': 'User not found'}), 404
        except Exception as e:
            return jsonify({'error': str(e)}), 500
        finally:
            cursor.close()

            

    return jsonify({'error': 'Invalid request method'}), 405

@app.route('/addData', methods=['POST'])
def add_data():
    if request.method == 'POST':
        data = request.get_json()
        date = data.get('date')
        emotion = data.get('emotion')
        pain = data.get('pain')
        hoursSlept = data.get('hoursSlept')


        if not all([date, emotion, pain, hoursSlept]):
            return jsonify({'error': 'Fill all of the fields'}), 400

        return jsonify({'message': 'Data saved successfully!'}), 200


#temporary
@app.route('/getData', methods=['GET'])
def get_data():
    fake_data = [
        {
            "date": "24/12/2024",
            "emotion": "happy",
            "pain": "no pain",
            "hoursSlept": 7
        },
        {
            "date": "17/09/2024",
            "emotion": "sad",
            "pain": "headache",
            "hoursSlept": 5
        },
        {
            "date": "15/11/2024",
            "emotion": "angry",
            "pain": "no pain",
            "hoursSlept": 6
        }
    ]

    return jsonify(fake_data), 200

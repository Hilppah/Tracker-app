from flask import Flask, flash, request, jsonify
from flask_mysqldb import MySQL
from datetime import datetime

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
                userID = user[0]
                if password == stored_password:
                    flash('Login successful!', 'success')
                    return jsonify({'message': 'Login successful!', 'userID': userID}), 200
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
        activity_date = data.get('date')
        emotion = data.get('emotion')
        pain = data.get('pain')
        hours_slept = data.get('hoursSlept')
        user_id =data.get('user_id')

        if not all([activity_date, emotion, pain, hours_slept]):
            return jsonify({'error': 'Fill all of the fields'}), 400
        
        cursor = mysql.connection.cursor()
        activity_query = "INSERT INTO daily_activity (user_id, activity_date, emotion, pain, hours_slept) VALUES (%s, %s, %s, %s, %s)"

        try:
            parsed_date = datetime.strptime(activity_date, "%d.%m.%Y")
            formatted_date = parsed_date.strftime("%Y-%m-%d")
            cursor.execute(activity_query, ( user_id, formatted_date, emotion, pain, hours_slept))
            mysql.connection.commit()
            return jsonify({ 'message': 'data successfully added'}), 201   
        except Exception as e:
            mysql.connection.rollback()
            return jsonify({'error': str(e)}), 500
        finally:
            cursor.close()



@app.route('/getData', methods=['GET'])
def get_data():
    user_id = request.args.get('user_id') 
    activity_date = request.args.get('date') 


    if not user_id or not activity_date:
        return jsonify({'error': 'Both user_id and date are required'}), 400
    
    cursor = mysql.connection.cursor()
    get_query ="SELECT user_id, activity_date, emotion, pain, hours_slept FROM daily_activity WHERE user_id = %s AND activity_date = %s"
    
    try: 
        cursor.execute(get_query, (user_id, activity_date))
        result = cursor.fetchone()

        if result is not None: 
            data = {
                'activity_date': result[1],
                'emotion': result[2],
                'pain': result[3],
                'hours_slept': result[4]
            }
            return jsonify(data), 200
        else:
            return jsonify({'error': 'No data found for the given date'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        cursor.close()
    


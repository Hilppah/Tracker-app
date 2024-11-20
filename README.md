# Tracker-app
Basic application for tracking daily activity.

## Description
This is a 4th year school project at OAMK. A mobile app made using Kotlin for the application and Python Flask for the backend. 

## Installation

### Run app
* Clone the project
* Build gradle ./gradlew build
* Start app

### Run backend

* Import project database
  ```
  mysql -u {USER} -p < kotlinDatabaseDump.sql
  ```
* cd to backend
* add credentials in Router.py
* start router
  ```
  flask --app router.py run
  ```
* Run project

## Pictures and demo video
* video
  ```
  https://www.youtube.com/watch?v=Nl9CmuTromU
  ```
* Pictures
  
  ![image](https://github.com/user-attachments/assets/e4f5815a-c655-44a2-9f16-cf010e149463)
  
  Login page

  ![image](https://github.com/user-attachments/assets/847ed68b-1ac4-4801-b914-03913e27d7d8)
  
  Calendar page

  ![image](https://github.com/user-attachments/assets/a96e33c5-96cb-47b2-a7cc-c3198ecbba43)
  
  Add data page

  ![image](https://github.com/user-attachments/assets/4f4cbe2a-32aa-4a42-ba2f-f2e504060e86)
  
  Profile page

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

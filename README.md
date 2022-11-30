## Railway-Reservation-System
Course Project for CS301 - Databases
***
### Setting up the database
Go to "Railway-Reservation-System/SQL" and launch psql
in psql run:
```
\i 'clean.sql'
```
This will set up the database and the required functions and triggers
***
### Checking Correctness
Requires 3 terminals:
* To run interactive server for taking release train requests:
```
javac interactiveServer.java; java ineractiveServer
```
* To run  Sevice Module for taking bookings requests:
```
javac SeviceModule.java; java ServiceModule
```
* To run  Interactive Client for sending requests:
```
javac  interactiveClient.java; java interactiveClient
```
***
### Performance Testing
Requires 3 terminals:
* To run  Sevice Module for taking bookings requests:
```
javac SeviceModule.java; java ServiceModule
```
* To run Client for sending requests:
Argument can be provided for directory containing input files in proper format
```
javac client.java; java client [Filename]
```
Please note that this part requires bookings to be present in the database.
Bookings can be added using the interactive client or can be added from a file with proper formatting using:
```
javac insertTrains.java; java insertTrains <Filename>
```
*Adding trains requires interactiveServer to be running*

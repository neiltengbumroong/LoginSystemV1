# Login System V1 #

A simple console-based program written in Java and built with Maven, designed to create and store users. </br> 
Key features of the program include: <br />
* user login and user creation capabilities
* loading of existing users into program through files in JSON format
* password encryption through SHA-256 hashing pattern
* storage of user information in JSON format in external file, including: 
  * first and last name
  * username
  * password
  * date of birth
  * last login time
* and many more coming in V2!


## How to Run ##
* Clone or fork the repository containing the source files. <br/>
* Use the following command to compile the program:
```
mvn clean package
```
* Use the following command to run the program:
```
java -cp java-project-1.0-SNAPSHOT-jar-with-dependencies.jar main.java.Server
```
* Optional Flags
```
-ls: load users from sample JSON file.
-lu: load from your own JSON file.
```
If loading existing file, JSON file MUST contain the following fields on one line (token) and password must be in SHA-256 format: <br/>
ie. {"birthday":"09-17-1999","firstName":"Neil","lastName":"Tengbumroong","lastLogin":"2020\/04\/07 13:16:34","password":"811eb81b9d11d65a36c53c3ebdb738ee303403cb79d781ccf4b40764e0a9d12a","userName":"neilteng"}

* Follow command line instructions to interact with program.


## How to Use ##
* c - create account. Terminal will prompt for user information, such as creating username and password.
* l - login. Terminal will prompt for user and password, and will verify matching user to password hash.
* d - delete account. Terminal will prompt for user and password, and will delete the account if they match.
* h - help. Displays a usage screen.
* e - exit. Exits the program.

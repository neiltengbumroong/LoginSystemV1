# Login System V1 #

A simple console-based program written in Java and built with Maven, designed to create and store users. </br> 
Key features of the program include: <br />
* user login and user creation capabilities
* loading of existing users into program through files in JSON format
* password recovery system
* password encryption through SHA-256 hashing pattern
* storage of user information in JSON format in external file, including: 
  * first and last name
  * username
  * password
  * date of birth
  * last login time
* and many more coming in V2!

<br/>

## How to Run ##
* Clone or fork the repository containing the source files <br/>
* Use the following command to run the program:
```
java -cp java-project-1.0-SNAPSHOT-jar-with-dependencies.jar main.java.Server
```
* Follow command line instructions to interact with program.


## How to Use ##
* c - create account. Terminal will prompt for user information, such as creating username and password.
* l - login. Terminal will prompt for user and password, and will verify matching user to password hash.
* h - help. Displays a usage screen.
* e - exit. Exits the program.

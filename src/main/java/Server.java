package main.java;

import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

import java.util.regex.*;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Server {
  HashMap<String, User> users; //hashmap listed by username and User object
  boolean write = false;
  static Validator validator;


  public Server() {
    users = new HashMap<String, User>();
    validator = new Validator();
    numUsers = 0;
  }

  public static String computeHash(String pass) throws NoSuchAlgorithmException {
    MessageDigest md = null;
    String hex = "";
    md = MessageDigest.getInstance("SHA-256");
    // Change this to UTF-16 if needed
    md.update(pass.getBytes(StandardCharsets.UTF_8));
    byte[] digest = md.digest();
    hex = String.format("%064x", new BigInteger(1, digest));

    return hex;
  }

  public boolean checkValidCredentials(String user, String pass) {
    // if username is found, compute password and check
    if (this.users.containsKey(user)) {
      String passAttempt = "";
      try {
        passAttempt = computeHash(pass);
      }
      catch (NoSuchAlgorithmException n) {
        n.printStackTrace();
      }

      // check hashed password with that stored in User in hashmap
      if (passAttempt.equals(this.users.get(user).getPasswordHash())) {
        System.out.println("Login successful!");
        return true;
      }
    }
    return false;
  }


  public boolean createUser(String first, String last, String username, String password, String birthday) {
    // check that current username doesn't exist
    if (this.users.containsKey(username)) {
      return false;
    }
    // collect current time for first login
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String currTime = dtf.format(now);

    try {
      password = computeHash(password);
    }
    catch (NoSuchAlgorithmException n) {
      n.printStackTrace();
    }

    // create and add user to hashmap with appropriate elements
    User newUser = new User(first, last, username, password, birthday);
    newUser.setLastLogin(currTime);
    this.users.put(newUser.getUserName(), newUser);
    this.numUsers = this.numUsers + 1;
    return true;
  }

  //public boolean deleteUser(String username) {}

  public void loadUsersJSON(File file) {}

  public boolean writeUsersJSON(String fileToWrite) throws IOException {
    BufferedWriter bw = null;

    // attempt to print userList to file, wrap inside try/catch for exceptions
    try {
      File file = new File(fileToWrite);
      FileWriter fw = new FileWriter(file);
      bw = new BufferedWriter(fw);
      // iterate through users hashmap and add user objects to be printed
      for (Map.Entry userElement : this.users.entrySet()) {
        User currUser = (User)userElement.getValue();
        JSONObject userToWrite = currUser.createJSONObj();
        //userList.add(userToWrite);
        bw.write(userToWrite.toString());
        bw.write("\n");
      }
    }
    // catch and print exceptions
    catch (IOException e) {
      System.out.println("Error writing to file!");
      e.printStackTrace();
      return false;
    }
    // finally close the writer
    finally {
      try {
        bw.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    Server server = new Server();
    server.createUser("Neil", "Tengbumroong", "flackoneil", "Chicken1889", "09171999");


    System.out.println("Welcome! What would you like to do?" + "\n" +
                        "c - create account" + "\n" +
                        "l - login" + "\n" +
                        "e - exit");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.next();

    // keep looping until e or exit is typed
    while (!input.equals("e") && !input.equals("exit")) {
      write = true;
      // handles user creation
      if (input.equals("c")) {
        System.out.print("Please enter your first name: ");
        String first = "";
        while (true) {
          first = scanner.next();
          if (validator.validateName(first)) {
            break;
          }
          System.out.println("Invalid name!");
        }


        System.out.print("Please enter your last name: ");
        String last = "";
        while (true) {
          last = scanner.next();
          if (validator.validateName(last)) {
            break;
          }
          System.out.println("Invalid name!");
        }

        System.out.print("Please choose your username: ");
        String username = "";
        while (true) {
          username = scanner.next();
          if (validator.validateUsername(username)) {
            break;
          }
          System.out.println("Username must be between 4 and 13 characters");
        }

        System.out.print("Please choose a password: ");
        String password = "";
        while (true) {
          password = scanner.next();
          if (validator.validateName(password)) {
            break;
          }
          System.out.println("Password must contain at least one uppercase, one lowercase, one digit, and be between 6 and 20 characters");
        }

        System.out.print("Please enter your birthday (mm-dd-yyyy): ");
        String birthday = "";
        while (true) {
          birthday = scanner.next();
          if (validator.validateName(birthday)) {
            break;
          }
          System.out.println("Invalid birthday! Birthday must be in mm-dd-yyyy format");
        }

        if (server.createUser(first, last, username, password, birthday)) {
          System.out.println("User created succesfully!");
        }

        System.out.println("What else would you like to do?" + "\n" +
                            "c - create account" + "\n" +
                            "l - login" + "\n" +
                            "e - exit");
      }

      // handles user login
      else if (input.equals("l")) {
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();

        server.checkValidCredentials(username, password);
        System.out.println("What else would you like to do?" + "\n" +
                            "c - create account" + "\n" +
                            "l - login" + "\n" +
                            "e - exit");
      }
      input = scanner.next();
    }

    // terminate on early exit
    if (input.equals("e")) {
      return;
    }

    if (write) {}
      try {
        server.writeUsersJSON("./classes/data/Users.json");
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

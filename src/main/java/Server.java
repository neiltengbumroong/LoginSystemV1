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
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
  HashMap<String, User> users; //hashmap listed by username and User object
  HashSet<String> usernames; // hashset containing lowercase users
  boolean write = false;
  static Validator validator;


  // standard constructor for server
  public Server() {
    users = new HashMap<String, User>();
    usernames = new HashSet<String>();
    validator = new Validator();
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

  // method to return the current time (used for loggin last login)
  public static String getTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String currTime = dtf.format(now);
    return currTime;
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
        this.users.get(user).setLastLogin(getTime());
        return true;
      }
    }
    return false;
  }


  public boolean createUser(String first, String last, String username, String password, String birthday) {
    try {
      password = computeHash(password);
    }
    catch (NoSuchAlgorithmException n) {
      n.printStackTrace();
    }

    // create and add user to hashmap with appropriate elements
    User newUser = new User(first, last, username, password, birthday);
    newUser.setLastLogin(getTime());
    this.users.put(newUser.getUserName(), newUser);
    this.usernames.add(username.toLowerCase());
    return true;
  }

  public boolean createUser(String first, String last, String username, String password, String birthday, String lastLogin) {
    // no need to calculate password hash as password is already hashed
    // create and add user to hashmap with appropriate elements
    User newUser = new User(first, last, username, password, birthday, lastLogin);
    this.users.put(newUser.getUserName(), newUser);
    this.usernames.add(username.toLowerCase());
    return true;
  }

  public boolean deleteUser(String username) {
    if (users.containsKey(username)) {
      users.remove(username);
      if (usernames.contains(username.toLowerCase())) {
        usernames.remove(username.toLowerCase());
        this.write = true;
        return true;
      }
    }
    return false;
  }

  public void loadUsersJSON(String fileToLoad) throws IOException {
    File file = new File(fileToLoad);
    Scanner scanner = new Scanner(file);
    String currLine = "";
    JSONParser parser = new JSONParser();

    while (scanner.hasNextLine()) {
      try {
        currLine = scanner.nextLine();
        Object obj = parser.parse(currLine);
        JSONObject jsonObject = (JSONObject) obj;

        String first = (String) jsonObject.get("firstName");

        String last = (String) jsonObject.get("lastName");
        String user = (String) jsonObject.get("userName");
        // early break if username is already existing
        if (users.containsKey(user)) {
          if (usernames.contains(user.toLowerCase())) {
            continue;
          }
        }
        String password = (String) jsonObject.get("password");
        String birthday = (String) jsonObject.get("birthday");
        String lastLogin = (String) jsonObject.get("lastLogin");

        this.createUser(first, last, user, password, birthday, lastLogin);
      }
      catch (ParseException pe) {
        pe.printStackTrace();
      }
    }
  }

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

  public void executeProgram() {
    System.out.println("Welcome! What would you like to do?" + "\n" +
                        "c - create account" + "\n" +
                        "l - login" + "\n" +
                        "d - delete account" + "\n" +
                        "e - exit");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.next();

    // keep looping until e or exit is typed
    while (!input.equals("e") && !input.equals("exit")) {
      this.write = true;
      // handles user creation
      if (input.equals("c")) {

        // handle first name
        System.out.print("Please enter your first name: ");
        String first = "";
        while (true) {
          first = scanner.next();
          if (validator.validateName(first)) {
            break;
          }
          System.out.println("Invalid name!");
          System.out.print("Please enter your first name: ");
        }

        // handle last name
        System.out.print("Please enter your last name: ");
        String last = "";
        while (true) {
          last = scanner.next();
          if (validator.validateName(last)) {
            break;
          }
          System.out.println("Invalid name!");
          System.out.print("Please enter your last name: ");
        }

        // handle username
        System.out.print("Please choose your username: ");
        String username = "";
        while (true) {
          username = scanner.next();
          String tempChecker = username.toLowerCase();
          if (this.usernames.contains(tempChecker) || (this.users.containsKey(username)))  {
            System.out.println("Username already taken! Please choose another");
            System.out.print("Please choose your username: ");
            continue;
          }
          else if (validator.validateUsername(username)) {
            break;
          }
          else {
            System.out.println("Username must be between 4 and 13 characters");
          }
        }

        // handle password
        System.out.print("Please choose a password: ");
        String password = "";
        while (true) {
          password = scanner.next();
          if (validator.validateName(password)) {
            break;
          }
          System.out.println("Password must contain at least one uppercase, one lowercase, one digit, and be between 6 and 20 characters");
          System.out.print("Please choose a password: ");
        }

        // handle birthday
        System.out.print("Please enter your birthday (mm-dd-yyyy): ");
        String birthday = "";
        while (true) {
          birthday = scanner.next();
          if (validator.validateBirthday(birthday)) {
            break;
          }
          System.out.println("Invalid birthday! Birthday must be in mm-dd-yyyy format");
          System.out.print("Please enter your birthday (mm-dd-yyyy): ");
        }

        // create the final user
        if (this.createUser(first, last, username, password, birthday)) {
          System.out.println("User created succesfully!");
        }


        // print prompts again
        System.out.println("What else would you like to do?" + "\n" +
                            "c - create account" + "\n" +
                            "l - login" + "\n" +
                            "d - delete account" + "\n" +
                            "e - exit");
      }

      // handles user login
      else if (input.equals("l")) {
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();

        this.checkValidCredentials(username, password);
        System.out.println("Login successful!");
        System.out.println("What else would you like to do?" + "\n" +
                            "c - create account" + "\n" +
                            "l - login" + "\n" +
                            "d - delete account" + "\n" +
                            "e - exit");
      }

      // handles user login
      else if (input.equals("d")) {
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();

        this.checkValidCredentials(username, password);
        this.deleteUser(username);
        System.out.println("What else would you like to do?" + "\n" +
                            "c - create account" + "\n" +
                            "l - login" + "\n" +
                            "d - delete account" + "\n" +
                            "e - exit");
      }

      // handle help flag
      else if (input.equals("h")) {
        System.out.println("c - create account. Terminal will prompt for user information, such as creating username and password.");
        System.out.println("l - login. Terminal will prompt for user and password, and will verify matching user to password hash.");
        System.out.println("d - delete account. Terminal will prompt for username and password to delete.");
        System.out.println("What else would you like to do?" + "\n" +
                            "c - create account" + "\n" +
                            "l - login" + "\n" +
                            "d - delete account" + "\n" +
                            "e - exit");

      }
      input = scanner.next();
      // terminate on early exit
      if (input.equals("e")) {
        break;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws IOException {
    Server server = new Server();
    // handle command line flags
    int i = 0;
    String arg = "";
    while (i < args.length && args[i].startsWith("-")) {
      arg = args[i++];

      // parse flag for loading own users
      if (arg.equals("-lu")) {
        server.loadUsersJSON("../src/data/MyUsers.json");
        server.write = true;
      }

      // parse flag for loading sample users
      if (arg.equals("-ls")) {
        server.loadUsersJSON("../src/data/SampleUsers.json");
        server.write = true;
      }
    }

    // load the existing users and run program
    server.loadUsersJSON("../src/data/Users.json");
    server.executeProgram();

    // if something needs to be written, write to file
    if (server.write) {
      try {
        server.writeUsersJSON("../src/data/Users.json");
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}

package main.java;

import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import org.json.simple.*;

public class Server {
  HashMap<String, User> users; //hashmap listed by username and User object
  int numUsers;

  public Server() {
    users = new HashMap<String, User>();
    numUsers = 0;
  }

  // computes a 10-digit hash for the password using prime integers
  public static String computeHash(String word) {
    // **** UNFINISHED **** //
    return word;
  }

  public boolean checkValidCredentials(String user, String pass) {
    // if username is found, compute password and check
    if (this.users.containsKey(user)) {
      String passAttempt = computeHash(pass);
      // check hashed password with that stored in User in hashmap
      if (passAttempt.equals(this.users.get(user).getPasswordHash())) {
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

    // create and add user to hashmap with appropriate elements
    User newUser = new User(first, last, username, computeHash(password), birthday);
    newUser.setLastLogin(currTime);
    this.users.put(newUser.getUserName(), newUser);
    this.numUsers = this.numUsers + 1;
    return true;
  }

  //public boolean deleteUser(String username) {}

  public void loadUsersJSON(File file) {}

  public void writeUsersJSON(String file) throws IOException {
    // create JSON array to put users in
    JSONArray userList = new JSONArray();

    // iterate through users hashmap and add user objects to be printed
    for (Map.Entry userElement : this.users.entrySet()) {
      User currUser = (User)userElement.getValue();
      JSONObject userToWrite = currUser.createJSONObj();
      userList.add(userToWrite);
    }

    // attempt to print userList to file, wrap inside try/catch for exceptions
    try {
      FileWriter fileWriter = new FileWriter(file);
      PrintWriter printWriter = new PrintWriter(fileWriter);

      printWriter.write(userList.toJSONString());
      printWriter.close();
    }
    // catch and print exceptions
    catch (IOException e) {
      System.out.println("Error writing to file!");
      e.printStackTrace();
    }

  }


  public static void main(String[] args) {
    Server server = new Server();

    //server.createUser(first, last, user, password, birthday);
    //server.writeUsersJSON("~/Desktop/LoginSystem/UserData.json");
  }
}

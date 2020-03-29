package com.server;

import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import org.json.simple.*;

public class User {

  private String firstname;
  private String lastname;
  //private String userID; //maybe delete later
  private String username;
  private String password;
  private String birthday;

  private String lastlogin;

  private boolean hasAccess;

  public User(String first, String last, String user, String pass, String birthday) {
    this.firstname = first;
    this.lastname = last;
    this.username = user;
    //this.userID =
    this.password = pass;
    this.hasAccess = false;
    this.birthday = birthday;
    this.lastlogin = "";
  }

  /* ---------- Getters --------------- */
  public String getFirstName() {
    return this.firstname;
  }

  public String getLastName() {
    return this.lastname;
  }

  public String getUserName() {
    return this.username;
  }

  public String getPasswordHash() {
    return this.password;
  }

  /* ---------- Setters --------------- */
  public void setFirstName(String name) {
    this.firstname = name;
  }

  public void setLastName(String name) {
    this.lastname = name;
  }

  public void setUserName(String name) {
    this.username = name;
  }

  public void setLastLogin(String time) {
    this.lastlogin = time;
  }

  @Override
  public String toString() {
    String stringToPrint = "username: " + this.username + "\n" +
                           "first name: " + this.firstname + "\n" +
                           "last name: " + this.lastname + "\n" +
                           "date of birth: " + this.birthday + "\n" +
                           "last login: " + this.lastlogin + "\n";
    return stringToPrint;
  }

  public JSONObject createJSONObj() {
    // add all elements to json object
    JSONObject currUser = new JSONObject();
    currUser.put("userName", this.username);
    currUser.put("firstName", this.firstname);
    currUser.put("lastName", this.lastname);
    currUser.put("password", this.password);
    currUser.put("birthday", this.birthday);
    currUser.put("Last login", this.lastlogin);

    // wrap object inside "user" wrapper object for printing
    JSONObject currUserWrapper = new JSONObject();
    currUserWrapper.put("user", currUser);

    return currUserWrapper;
  }



}

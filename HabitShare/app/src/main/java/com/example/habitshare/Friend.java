package com.example.habitshare;

/**
 * A class that contains some useful information of a friend+
 */
public class Friend {
    private String userName;
    private String email;

    public Friend(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    /**
     * @return the user name of a friend
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the email of a friend
     */
    public String getEmail() {
        return email;
    }
}

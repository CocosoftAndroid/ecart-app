package com.cocosoft.ecart.loginmodule;

/**
 * Created by.dmin on 3/17/2017.
 */

public class UserItem {
    private String id="";
    private String userName = "";
    private String password = "";
    private String userType = "";

    public UserItem(String id, String userName, String password, String userType) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
    }

    public UserItem() {
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.aslan.locationcontextprovider;

import java.io.Serializable;

/**
 * Created by Vishnuvathsasarma on 03-Aug-15.
 */
public class RegistrationData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String username;
    private String password;
    private String deviceToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Override
    public String toString() {
        String data = "Name : " + name
                + "\nUsername : " + username
                + "\nPassword : " + password
                + "\nDevice Token : " + deviceToken;
        return data;
    }
}

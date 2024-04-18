package com.orm.app.config;

public class DbConnection {
    private String url;
    private String userName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public DbConnection(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    private String password;
}

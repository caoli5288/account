package com.mengcraft.account.bungee.server.action;

/**
 * Created on 16-4-19.
 */
public class LoginResponse {

    private String session;
    private String error;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}

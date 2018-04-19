/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.model;

/**
 *
 * @author TAND.M
 */
public class MessageModel {

    private String action;
    private String msg;
    private String name;
    private String id;
    private String clientType;
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public String getId() {
        return id;
    }

    
    public String getClientType() {
        return clientType;
    }

    public String getAction() {
        return action;
    }

    public String getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

}

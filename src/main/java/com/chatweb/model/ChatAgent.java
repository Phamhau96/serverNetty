/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.model;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author TAND.M
 */
public class ChatAgent {
    private String agentId;
    private String userName;
    private String fullName;
    private String photo;
    private ChannelHandlerContext ctx;

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoto() {
        return photo;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    
}

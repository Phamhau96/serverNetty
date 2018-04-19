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
public class ChatCustomer {
    private String customerId;
    private String email;
    private ChannelHandlerContext ctx;
    private String agentId;
    private String msgClient;

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setMsgClient(String msgClient) {
        this.msgClient = msgClient;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getMsgClient() {
        return msgClient;
    }
    
    
}

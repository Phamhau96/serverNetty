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
public class ChatSession {

    private String sessionId;
    private String agentId;
    private String agentName;
    private String customerId;
    private String customerName;
    private String queueId;
    private String status;
    private String msgClient;

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMsgClient(String msgClient) {
        this.msgClient = msgClient;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getQueueId() {
        return queueId;
    }

    public String getStatus() {
        return status;
    }

    public String getMsgClient() {
        return msgClient;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getCustomerName() {
        return customerName;
    }
    

}

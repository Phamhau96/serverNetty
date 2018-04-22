/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TAND.M
 */
public class ChatMessage {
    private List<ChatSession> chatSessions = new ArrayList<>();

    public List<ChatSession> getChatSessions() {
        return chatSessions;
    }

    public void setChatSessions(List<ChatSession> chatSessions) {
        this.chatSessions = chatSessions;
    }
}

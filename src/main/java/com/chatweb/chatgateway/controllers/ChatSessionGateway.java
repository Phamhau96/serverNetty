/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.chatgateway.controllers;

import com.chatweb.controller.ChatSessionController;
import com.chatweb.model.ChatSession;
import java.util.Map;

/**
 *
 * @author TAND.M
 */
public class ChatSessionGateway {

    ChatSessionController sc;

    public ChatSessionGateway() {
        sc = new ChatSessionController();
    }

    public Map<String, ChatSession> getmapChatSession() {
        return ChatSessionController.mapChatSession;
    }

    public ChatSession insertSessionChat(ChatSession chatSession) {
        return sc.insertSessionChat(chatSession);
    }

    public void updateSessionChat(ChatSession chatSession) {
        sc.updateSessionChat(chatSession);
    }
}

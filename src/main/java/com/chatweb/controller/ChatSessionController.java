/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.controller;

import com.chatweb.model.ChatSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author TAND.M
 */
public class ChatSessionController {
    public static Map<String, ChatSession> mapChatSession = new HashMap<>();

    public ChatSession insertSessionChat(ChatSession chatSession){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmSS");
        String id = dateFormat.format(new Date());
        chatSession.setSessionId(id);
        mapChatSession.put(id, chatSession);
        return chatSession;
    }
    
    public void updateSessionChat(ChatSession chatSession){
        if(mapChatSession.containsKey(chatSession.getSessionId())){
            mapChatSession.put(chatSession.getSessionId(), chatSession);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servernetty;

import com.chatweb.Util.DBUtility;
import com.chatweb.model.ChatAgent;
import com.chatweb.model.ChatSession;
import com.chatweb.model.MessPoJo;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TAND.M
 */
public class ServerAction {

    public static void sendAllMessageEvent(List<ChannelHandlerContext> agentChannel, Object objEvent, String eventName) {
        for (ChannelHandlerContext ctx : agentChannel) {
            ctx.writeAndFlush(new TextWebSocketFrame(
                    new Gson().toJson(new MessPoJo<Object>(eventName, objEvent))));
            AttributeKey<String> aKey = AttributeKey.valueOf(String.valueOf(objEvent));
            ctx.channel().attr(aKey).setIfAbsent(String.valueOf(objEvent));
        }
    }

    public static void sendMessageEvent(ChannelHandlerContext ctx, Object objEvent, String eventName) {
        ctx.writeAndFlush(new TextWebSocketFrame(
                new Gson().toJson(new MessPoJo<Object>(eventName, objEvent))));
        AttributeKey<String> aKey = AttributeKey.valueOf(String.valueOf(objEvent));
        ctx.channel().attr(aKey).setIfAbsent(String.valueOf(objEvent));
    }

    public static ChatAgent searchAgentChat(Map<String, ChatAgent> mapAgentOnline, String customerId, String queueId, Map<String, List<String>> mapDenyAgent, Queue queue) {
        ChatAgent agent = null;
        if (!mapAgentOnline.isEmpty() && !queue.isEmpty()) {
//            for (String key : mapAgentOnline.keySet()) {
//                agent = mapAgentOnline.get(key);
//                return agent;
//            }
            String key = (String) queue.poll();
            agent = mapAgentOnline.get(key);
            queue.add(key);
            return agent;
        }
        return agent;
    }

    public static boolean insertMessage(ChatSession chatSession) {
        DBUtility bUtility = new DBUtility();
        Connection connection = null;
        try {
            connection = bUtility.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ServerAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean kq = false;
        String sql = "INSERT INTO messages (sessionId, agentId, agentName, customerId, msgClient, clientType)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, chatSession.getSessionId());
            pstmt.setString(2, chatSession.getAgentId());
            pstmt.setString(3, chatSession.getAgentName());
            pstmt.setString(4, chatSession.getCustomerId());
            pstmt.setString(5, chatSession.getMsgClient());
            pstmt.setString(6, chatSession.getClientType());

            if (pstmt.executeUpdate() > 0) {
                kq = true;
            }
            pstmt.close();
            connection.close();
        } catch (SQLException ex) {
        }
        return kq;
    }
}

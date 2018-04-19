/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servernetty;

import com.chatweb.model.ChatAgent;
import com.chatweb.model.MessPoJo;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.Map;

/**
 *
 * @author TAND.M
 */
public class ServerAction {

    public static void sendMessageEvent(ChannelHandlerContext ctx, Object objEvent, String eventName) {
        ctx.writeAndFlush(new TextWebSocketFrame(
                new Gson().toJson(new MessPoJo<Object>(eventName, objEvent))));
        AttributeKey<String> aKey = AttributeKey.valueOf(String.valueOf(objEvent));
        ctx.channel().attr(aKey).setIfAbsent(String.valueOf(objEvent));
    }

    public static ChatAgent searchAgentChat(Map<String, ChatAgent> mapAgentOnline, String customerId, String queueId, Map<String, List<String>> mapDenyAgent) {
        ChatAgent agent = null;
        if (!mapAgentOnline.isEmpty()) {
            for (String key : mapAgentOnline.keySet()) {
                agent = mapAgentOnline.get(key);
                return agent;
            }
        }
        return agent;
    }
}

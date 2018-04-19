/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servernetty;

import com.chatweb.Util.Chat_Constants;
import com.chatweb.chatgateway.controllers.ChatSessionGateway;
import com.chatweb.model.ChatAgent;
import com.chatweb.model.ChatCustomer;
import com.chatweb.model.ChatSession;
import com.chatweb.model.MessageModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author TAND.M
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final String CUSTOMER = "customer";
    private final String AGENT = "agent";
    private static final Map<String, ChatCustomer> mapCusOnline = new HashMap<>();
    private static final Map<String, ChatAgent> mapAgentOnline = new HashMap<>();
    private static Map<String, List<String>> mapDenyAgent = new HashMap<>();
    private static Map<String, ChatSession> mapSessionWait = new HashMap<>();
    private static Map<String, ChatSession> mapSessionAll = new HashMap<>();
    private static ChatSessionGateway sessionGateway = new ChatSessionGateway();

    @Override
    protected void channelRead0(ChannelHandlerContext chc, TextWebSocketFrame txtMsg) throws Exception {
        try {
            String msg = txtMsg.text();
            Gson gson = new Gson();
            MessageModel msgOject = gson.fromJson(msg, MessageModel.class);

            String action = msgOject.getAction();
            String chanSesId = msgOject.getSessionId();

            switch (action) {
                case Chat_Constants.LOGIN:
                    /*ktra customer co trong list chua 
                neu chua tao ra mot phien chat moi va tim kiem Agent
                neu co ktra thoi gian cho chat neu co thi thoi neu ko tim kiem Agent
                     */
                    if (CUSTOMER.equals(msgOject.getClientType())) {
                        if (!mapCusOnline.containsKey(msgOject.getId())) {
                            //them vao lst customer Onl
                            ChatCustomer chatCustomer = new ChatCustomer();
                            chatCustomer.setCustomerId(msgOject.getId());
                            chatCustomer.setMsgClient(msgOject.getMsg());
                            chatCustomer.setCtx(chc);
                            mapCusOnline.put(msgOject.getId(), chatCustomer);

                            //tao ra session chat
                            ChatSession chatSession = new ChatSession();
                            chatSession.setCustomerId(msgOject.getId());
                            chatSession.setMsgClient(msgOject.getMsg());
                            chatSession = sessionGateway.insertSessionChat(chatSession);

                            mapSessionWait.put(chatSession.getSessionId(), chatSession);
                        }

                        ChatSession chatSession = new ChatSession();

                        for (String key : mapSessionWait.keySet()) {
                            if (mapSessionWait.get(key).getCustomerId().equals(msgOject.getId())) {
                                chatSession = mapSessionWait.get(key);
                                break;
                            }
                        }
                        //tim agent chat
                        ChatAgent chatAgent = ServerAction.searchAgentChat(mapAgentOnline, chatSession.getCustomerId(), "", mapDenyAgent);

                        if (chatAgent != null) {
                            //gui yeu cau chat toi agent
                            chatSession.setAgentId(chatAgent.getAgentId());
                            chatSession.setAgentName(chatAgent.getFullName());
                            mapSessionWait.put(chatSession.getSessionId(), chatSession);
                            sessionGateway.updateSessionChat(chatSession);
                            ServerAction.sendMessageEvent(chatAgent.getCtx(), chatSession, Chat_Constants.CHAT_REQUEST);
                            ServerAction.sendMessageEvent(chatAgent.getCtx(), chatSession, Chat_Constants.JOIN_EVENT);
                            ServerAction.sendMessageEvent(chc, chatSession, Chat_Constants.JOIN_EVENT);
                            ServerAction.sendMessageEvent(chatAgent.getCtx(), chatSession, Chat_Constants.GET_MESSAGE);
                            ServerAction.sendMessageEvent(chc, chatSession, Chat_Constants.GET_MESSAGE);

                        }
                    } else {
                        ChatAgent chatAgent = new ChatAgent();
                        chatAgent.setAgentId(msgOject.getId());
                        chatAgent.setCtx(chc);
                        mapAgentOnline.put(msgOject.getId(), chatAgent);
                    }

                    break;
                case Chat_Constants.ACCEPT_CHAT:
                    //
                    for (String key : mapSessionWait.keySet()) {
                        if (mapSessionWait.get(key).getAgentId().equals(msgOject.getId())) {
                            mapSessionWait.remove(key);
                            break;
                        }
                    }
                    break;
                case Chat_Constants.NO_ACCEPT_CHAT:
                    break;
                case Chat_Constants.SEND_CHAT:
                    ChatSession chatSession = new ChatSession();
                    chatSession.setCustomerId(msgOject.getId());
                    chatSession.setMsgClient(msgOject.getMsg());
                    Map<String, ChatSession> mapSessionClone = sessionGateway.getmapChatSession();
                    String customerId = mapSessionClone.get(chanSesId).getCustomerId();
                    String agentId = mapSessionClone.get(chanSesId).getAgentId();
                    ChannelHandlerContext ctx = null;
                    //tim kenh cua khach hang
                    if (AGENT.equals(msgOject.getClientType())) {
                        ctx = mapCusOnline.get(customerId).getCtx();
                    } else {
                        chatSession.setSessionId(chanSesId);
                        chatSession.setCustomerId(customerId);
                        ctx = mapAgentOnline.get(agentId).getCtx();
                    }
                    ServerAction.sendMessageEvent(chc, chatSession, Chat_Constants.GET_MESSAGE);
                    ServerAction.sendMessageEvent(ctx, chatSession, Chat_Constants.GET_MESSAGE);
                    break;
                case Chat_Constants.END_CHAT:
                    //ktra xem session ko neu co thi End
                    // gui yeu cau ket thuc chat
                    Map<String, ChatSession> mapSessionClone1 = sessionGateway.getmapChatSession();
                    String customerId1 = mapSessionClone1.get(chanSesId).getCustomerId();
                    String agentId1 = mapSessionClone1.get(chanSesId).getAgentId();
                    ChannelHandlerContext ctx1 = null;
                    //Tim kenh
                    if (AGENT.equals(msgOject.getClientType())) {
                        ctx1 = mapCusOnline.get(customerId1).getCtx();
                    } else {
                        ctx1 = mapAgentOnline.get(agentId1).getCtx();
                    }
                    ServerAction.sendMessageEvent(ctx1, null, Chat_Constants.END_CHAT);
                    break;
                case Chat_Constants.LOG_OUT:
                    mapAgentOnline.remove(msgOject.getId());
                    break;
            }
        } catch (JsonSyntaxException json) {
            System.err.println("JsonSyntaxException " + json.getMessage());
            chc.write(new CloseWebSocketFrame());
            chc.close();
        } catch (Exception ex) {
            System.err.println(ex.toString());
            chc.write(new CloseWebSocketFrame());
            chc.close();
        }

    }
}

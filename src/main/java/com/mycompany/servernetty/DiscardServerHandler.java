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
import com.chatweb.model.ChatMessage;
import com.chatweb.model.ChatSession;
import com.chatweb.model.MessageModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private static Map<String, ChatMessage> mapMessageChat = new HashMap<>();
    private static ChatSessionGateway sessionGateway = new ChatSessionGateway();
    private static Map<String, HashSet<ChannelHandlerContext>> mapChannel = new HashMap<>();
    //theo dõi các session maf agent đang chat, Key id agent
    private static Map<String, HashSet<String>> mapAgentChatSession = new HashMap<>();

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
                    //<editor-fold defaultstate="collapsed" desc="Login">
                    /*ktra customer co trong list chua 
                neu chua tao ra mot phien chat moi va tim kiem Agent
                neu co ktra thoi gian cho chat neu co thi thoi neu ko tim kiem Agent
                     */

                    if (CUSTOMER.equals(msgOject.getClientType())) {
                        System.out.println("Kênh Khách hàng: " + chc.channel());
                        ChatSession chatSession = new ChatSession();
                        if (!mapCusOnline.containsKey(msgOject.getId())) {
                            //them vao lst customer Onl
                            ChatCustomer chatCustomer = new ChatCustomer();
                            chatCustomer.setCustomerId(msgOject.getId());
                            chatCustomer.setMsgClient(msgOject.getMsg());
                            chatCustomer.setCtx(chc);
                            mapCusOnline.put(msgOject.getId(), chatCustomer);

                            //tao ra session chat
//                            ChatSession chatSession = new ChatSession();
                            chatSession.setCustomerId(msgOject.getId());
                            chatSession.setMsgClient(msgOject.getMsg());
                            chatSession.setClientType(msgOject.getClientType());
                            chatSession = sessionGateway.insertSessionChat(chatSession);

                            mapSessionWait.put(chatSession.getSessionId(), chatSession);
                        }

//                        ChatSession chatSession = new ChatSession();
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

                            //get list Channel
                            List<ChannelHandlerContext> agentChannel = new ArrayList<>(mapChannel.get(chatAgent.getAgentId()));

                            ServerAction.sendAllMessageEvent(agentChannel, chatSession, Chat_Constants.CHAT_REQUEST);
                            ServerAction.sendAllMessageEvent(agentChannel, chatSession, Chat_Constants.JOIN_EVENT);
                            ServerAction.sendMessageEvent(chc, chatSession, Chat_Constants.JOIN_EVENT);
                            ServerAction.sendAllMessageEvent(agentChannel, chatSession, Chat_Constants.SEND_CHAT);
                            ServerAction.sendMessageEvent(chc, chatSession, Chat_Constants.SEND_CHAT);

                            MessageModel message = new MessageModel();
                            message.setMsg(chatSession.getMsgClient());

                            if (mapMessageChat.containsKey(chatSession.getSessionId())) {
                                mapMessageChat.get(chatSession.getSessionId()).getChatSessions().add(chatSession);
                            } else {
                                mapMessageChat.put(chatSession.getSessionId(), new ChatMessage());
                                mapMessageChat.get(chatSession.getSessionId()).getChatSessions().add(chatSession);
                            }

                            //Thêm session vào map
                            if (!mapAgentChatSession.containsKey(chatAgent.getAgentId())) {
                                mapAgentChatSession.put(chatAgent.getAgentId(), new HashSet<>());
                            }
                            mapAgentChatSession.get(chatAgent.getAgentId()).add(chatSession.getSessionId());
                        }
                    } else {
                        if (!mapAgentOnline.containsKey(msgOject.getId())) {
                            ChatAgent chatAgent = new ChatAgent();
                            chatAgent.setAgentId(msgOject.getId());
                            chatAgent.setCtx(chc);
                            mapAgentOnline.put(msgOject.getId(), chatAgent);
                            mapChannel.put(msgOject.getId(), new HashSet<>());
                            mapChannel.get(msgOject.getId()).add(chc);
                            System.out.println("Kênh Agent: " + chc.channel());
                            break;
                        }
                        mapChannel.get(msgOject.getId()).add(chc);
                    }
                    //</editor-fold>
                    break;
                case Chat_Constants.ACCEPT_CHAT:
                    //<editor-fold defaultstate="collapsed" desc="Accept Chat">
                    for (String key : mapSessionWait.keySet()) {
                        if (mapSessionWait.get(key).getAgentId().equals(msgOject.getId())) {
                            mapSessionWait.remove(key);
                            break;
                        }
                    }
                    //</editor-fold>
                    break;
                case Chat_Constants.NO_ACCEPT_CHAT:
                    break;
                case Chat_Constants.SEND_CHAT:
                    //<editor-fold defaultstate="collapsed" desc="Send Message">
                    ChatSession chatSession = new ChatSession();
                    chatSession.setSessionId(chanSesId);
                    chatSession.setCustomerId(msgOject.getId());
                    chatSession.setMsgClient(msgOject.getMsg());
                    chatSession.setClientType(msgOject.getClientType());
                    chatSession.setSessionId(chanSesId);

                    Map<String, ChatSession> mapSessionClone = sessionGateway.getmapChatSession();
                    String customerId = mapSessionClone.get(chanSesId).getCustomerId();
                    String agentId = mapSessionClone.get(chanSesId).getAgentId();
                    chatSession.setCustomerId(customerId);

                    //get list Channel Agent
                    List<ChannelHandlerContext> agentChannel = new ArrayList<>(mapChannel.get(agentId));

                    ChannelHandlerContext context = null;
                    //tim kenh cua khach hang
                    if (AGENT.equals(msgOject.getClientType())) {
                        //get list Channel
                        context = mapCusOnline.get(customerId).getCtx();
                    } else {
                        context = chc;
                    }
                    ServerAction.sendMessageEvent(context, chatSession, Chat_Constants.SEND_CHAT);
                    ServerAction.sendAllMessageEvent(agentChannel, chatSession, Chat_Constants.SEND_CHAT);

                    mapMessageChat.get(chanSesId).getChatSessions().add(chatSession);
                    break;
                //</editor-fold>

                case Chat_Constants.GET_MESSAGE:
                    //<editor-fold defaultstate="collapsed" desc="Get Message">
                    ChatMessage chatMessage = mapMessageChat.get(chanSesId);
                    ServerAction.sendMessageEvent(chc, chatMessage, Chat_Constants.GET_MESSAGE);
                    //</editor-fold>
                    break;
                case Chat_Constants.END_CHAT:
                    //<editor-fold defaultstate="collapsed" desc="End Chat">
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
//                    ServerAction.sendMessageEvent(ctx1, null, Chat_Constants.END_CHAT);
                    //</editor-fold>
                    break;
                case Chat_Constants.LOG_OUT:
                    mapAgentOnline.remove(msgOject.getId());
                    break;
                case Chat_Constants.GET_CONVERSATION:
                    //<editor-fold defaultstate="collapsed" desc="get conversation client">
                    mapCusOnline.get(msgOject.getId()).setCtx(chc);
                    ChatMessage msgClient = mapMessageChat.get(chanSesId);
                    ServerAction.sendMessageEvent(chc, msgClient, Chat_Constants.GET_CONVERSATION);
                    //</editor-fold>
                    break;
                case Chat_Constants.GET_CONVERSATION_AGENT:
                    //<editor-fold defaultstate="collapsed" desc="get conversation agent">
                    if (mapAgentChatSession.size() > 0) {
                        List<String> lstSession = new ArrayList<>(mapAgentChatSession.get(msgOject.getId()));
                        List<ChatSession> chatSessions = new ArrayList<>();
                        for (String session : lstSession) {
                            List<ChatSession> chatMessage1 = mapMessageChat.get(session).getChatSessions();
                            ChatSession chatSession1 = chatMessage1.get(chatMessage1.size() - 1);
                            chatSessions.add(chatSession1);
                        }
                        ServerAction.sendMessageEvent(chc, chatSessions, Chat_Constants.GET_CONVERSATION_AGENT);
                    }
                    //</editor-fold>
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

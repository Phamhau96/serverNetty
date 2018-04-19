/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servernetty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 *
 * @author TAND.M
 */
public class ServerHandersInit extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel c) throws Exception {
       ChannelPipeline pipeline = c.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
            pipeline.addLast(new DiscardServerHandler());
    }
    
}

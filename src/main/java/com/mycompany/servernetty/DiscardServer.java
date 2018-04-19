/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servernetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * @author TAND.M
 */
public class DiscardServer {
    
    private int port;
    
    public DiscardServer(int port){
        this.port = port;
    }
    
    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                     .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerHandersInit())
//                    .option(ChannelOption.SO_BACKLOG, 128)
                   .childOption(ChannelOption.AUTO_READ, true);
                  
            ChannelFuture f = b.bind(port).sync();
            
            f.channel().closeFuture().sync();
        }
        finally{
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception{
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }else{
            port = 8080;
        }
        System.out.println(port);
        new DiscardServer(port).run();
    }
}

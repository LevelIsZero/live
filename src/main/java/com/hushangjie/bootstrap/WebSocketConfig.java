package com.hushangjie.bootstrap;

import com.hushangjie.dao.StatDao;
import com.hushangjie.service.HandShkeInceptor;
import com.hushangjie.service.MyChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

/**
 * Created by hushangjie on 2017/6/12.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    //拦截器注入service失败解决办法
    @Bean
    public MyChannelInterceptor myChannelInterceptor(){
        return new MyChannelInterceptor();
    }
    /*
    * 使用registerStompEndpoints方法注册一个websocket终端连接。
    * 这里需要了解两个东西：stomp和sockjs
    * 1、sockjs：是对于websocket的封装，如果单纯使用websocket的话效率会非常低，我们需要的编码量也会增多，而且如果浏览器不支持
    *   websocket，sockjs会自动降级为轮询策略，并模拟websocket，保证客户端和服务端可以通信。
    * 2、stomp是一种简单(流)文本定向消息协议，提供了可互操作的连接格式，允许STOMP客户端与STOMP消息代理（RabbitMQ就是一个消息代理）
    *   进行交互。
    * */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //添加访问域名限制可以防止跨域soket连接
        //setAllowedOrigins("http://localhost:8085")
        registry.addEndpoint("/live").setAllowedOrigins("*").addInterceptors(new HandShkeInceptor()).withSockJS();

    }

    /*
    * 使用configureMessageBroker来配置消息代理（注意：我们将要部署的服务器也应该要有RabbitMQ）。
    * 这里配置了两个代理转播策略，就是说客户端订阅了前缀为"/topic,/queue"频道都会通过消息代理(RabbbitMQ)来转发，与spring没关系，完全解耦。
    * */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*.enableSimpleBroker("/topic","/queue");*/
        //假如需要第三方消息队列，比如rabitMQ,activeMq，在这里配置
        registry.setApplicationDestinationPrefixes("/demo")
                .enableStompBrokerRelay("/topic","/queue")
                .setRelayHost("127.0.0.1")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest")
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelRegistration channelRegistration = registration.setInterceptors(myChannelInterceptor());
        super.configureClientInboundChannel(registration);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        super.configureClientOutboundChannel(registration);
    }

}

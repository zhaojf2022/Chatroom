package chatdemo.web.websocket;


import chatdemo.service.ChatService;
import chatdemo.service.impl.ChatServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import chatdemo.vo.ResponseJson;
import chatdemo.util.Constant;

import javax.annotation.Resource;


@Component
@Sharable  // 标记这是一个可以被多个channel共享的handler
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);
    
//    @Resource
//    private ChatService chatService;
    private final ChatServiceImpl chatService = new ChatServiceImpl();

    /**
     * 描述：读取完连接的消息后，对消息进行处理。
     *      这里主要是处理WebSocket请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        // 关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handshaker = 
                    Constant.webSocketHandshakerMap.get(ctx.channel().id().asLongText());
            if (handshaker == null) {
                log.error("WebSocket关闭请求握手错误：不存在的客户端连接");
                sendErrorMessage(ctx, "不存在的客户端连接！");
            } else {
                log.debug("WebSocket关闭请求握手成功");
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        // ping请求
        if (frame instanceof PingWebSocketFrame) {
            log.debug("收到WebSocket Ping请求");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 只支持文本格式，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            log.debug("收到WebSocket 非文本消息请求");
            sendErrorMessage(ctx, "仅支持文本(Text)格式，不支持二进制消息");
        }

        // 客服端发送过来的消息
        String request = null;
        if (frame instanceof TextWebSocketFrame) {
            log.debug("收到 WebSocket 文本消息请求");
            request = ((TextWebSocketFrame)frame).text();
        }

        log.info("服务端收到新信息：" + request);
        JSONObject param = null;
        try {
            param = JSONObject.parseObject(request);
        } catch (Exception e) {
            sendErrorMessage(ctx, "JSON字符串转换出错！");
            log.error("JSON字符串转换出错" + e.getMessage());
        }
        if (param == null) {
            log.warn("请求消息中的参数为空");
            sendErrorMessage(ctx, "参数为空！");
            return;
        }

        String type = (String) param.get("type");
        switch (type) {
            case "REGISTER":
                chatService.register(param, ctx);
                break;
            case "SINGLE_SENDING":
                chatService.singleSend(param, ctx);
                break;
            case "GROUP_SENDING":
                chatService.groupSend(param, ctx);
                break;
            case "FILE_MSG_SINGLE_SENDING":
                chatService.fileMsgSingleSend(param, ctx);
                break;
            case "FILE_MSG_GROUP_SENDING":
                chatService.fileMsgGroupSend(param, ctx);
                break;
            default:
                chatService.typeError(ctx);
                break;
        }
    }
    
    /**
     * 描述：客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端断开连接，删除上下文");
        chatService.remove(ctx);
    }
   
    /**
     * 异常处理：关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("WebSocket消息处理异常：" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
    
    
    private void sendErrorMessage(ChannelHandlerContext ctx, String errorMsg) {
        String responseJson = new ResponseJson()
                .error(errorMsg)
                .toString();
        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseJson));
    }

}

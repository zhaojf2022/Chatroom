package chatdemo.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import chatdemo.util.Constant;

@Component
@Sharable // 标记这是一个可以被多个channel共享的handler
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    /**
     * 描述：读取完连接的消息后，对消息进行处理。
     * 这里仅处理HTTP请求，WebSocket请求交给下一个处理器（WebSocketServerHandler)处理。
     * @param ctx ChannelHandlerContext 上下文对象，含有管道pipeline，地址信息，数据等
     * @param msg 客户端发送过来的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            log.debug("收到 FullHttpRequest类型的消息：" + msg.toString());
            // 处理 http 请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            //如果接收到的消息是一个WebSocketFrame类型的消息，则将该消息传递给下一个ChannelHandler进行处理
            log.debug("收到 WebSocketFrame类型的消息：" + msg.toString());
            ctx.fireChannelRead(((WebSocketFrame) msg).retain());
        } 
    }

    /**
     * 描述：处理Http请求，主要是完成HTTP协议到Websocket协议的升级
     * @param ctx ChannelHandlerContext 上下文对象，含有管道pipeline，地址信息，数据等
     * @param req 客户端发送过来的请求信息
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果HTTP解码失败，返回HTTP异常
        if (!req.decoderResult().isSuccess()) {
             log.warn("对收到的http请求消息解码失败，发送响应消息");
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 根据请求中的上下文，创建WebSocket，与请求对象发起握手
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws:/" + ctx.channel() + "/websocket", null, false);
        log.debug("创建 WebSocket 握手对象：" + "ws:/" + ctx.channel() + "/websocket" );
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        Constant.webSocketHandshakerMap.put(ctx.channel().id().asLongText(), handshaker);

        if (handshaker == null) {
            log.warn("WebSocket握手对象创建失败！ " + ctx.channel());
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            log.debug("WebSocket握手对象创建成功！，和请求对象发起握手");
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 发送 http 响应
     * @param ctx ChannelHandlerContext
     * @param req FullHttpRequest
     * @param res DefaultFullHttpResponse
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 描述：异常处理，关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("http请求消息处理异常");
        cause.printStackTrace();
        ctx.close();
    }
}

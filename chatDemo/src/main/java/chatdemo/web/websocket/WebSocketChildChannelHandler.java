package chatdemo.web.websocket;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

@Component
public class WebSocketChildChannelHandler extends ChannelInitializer<SocketChannel>{

	@Resource
	private WebSocketServerHandler webSocketServerHandler;

	@Resource
	private HttpRequestHandler httpRequestHandler;

	private static Logger log = LoggerFactory.getLogger(WebSocketChildChannelHandler.class);

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		log.info("初始化 Netty渠道");
		if(httpRequestHandler != null) {log.debug("httpRequestHandler已注入");}
		if(webSocketServerHandler != null) {log.debug("webSocketServerHandler");}

		// HTTP编码解码器
		socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());
		// 把HTTP头、HTTP体拼成完整的HTTP请求
		socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));

		// 请求处理管道：先先处理 http 请求，再处理 webSocket 请求；
		// ChunkedWriteHandler用于处理HTTP分块编码，它可以将一个完整的HTTP响应分成多个块进行传输。
		//socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
//		socketChannel.pipeline().addLast("http-handler", httpRequestHandler);
//		socketChannel.pipeline().addLast("websocket-handler",webSocketServerHandler);
		socketChannel.pipeline().addLast(httpRequestHandler);
		socketChannel.pipeline().addLast(webSocketServerHandler);
	}

}

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

	private final WebSocketServerHandler webSocketServerHandler = new WebSocketServerHandler();

	private final HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

	private static final Logger log = LoggerFactory.getLogger(WebSocketChildChannelHandler.class);

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {

		log.info("初始化 Netty Channel");

		// 通过将HttpServerCodec添加到管道中，可以实现对HTTP协议的支持。
		// HttpServerCodec是Netty中的一个编解码器，用于将HTTP请求和响应编解码为字节流。
		// 它包含了HttpRequestDecoder和HttpResponseEncoder两个编解码器，用于处理HTTP请求和响应的编解码工作。
		socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());

		// HttpObjectAggregator是一个用于聚合HTTP消息的处理器，它将多个HTTP消息合并成一个完整的FullHttpRequest或FullHttpResponse对象。
		// HttpObjectAggregator的参数为65536，表示最大聚合的消息长度为65536字节
		socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));

		// ChunkedWriteHandler用于处理HTTP分块编码，它可以将一个完整的HTTP响应分成多个块进行传输。
		socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

		// 先按照http请求处理，再按照webSocket请求处理；
		socketChannel.pipeline().addLast("http-handler", httpRequestHandler);
		socketChannel.pipeline().addLast("websocket-handler",webSocketServerHandler);

	}

}

package chatdemo.web.websocket;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 描述: Netty WebSocket服务器，使用独立的线程启动
 * @author Kanarien 
 * @version 1.0
 * @date 2018年5月18日 上午11:22:51
 */

@Service
public class WebSocketServer implements Runnable{

    private final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    
	@Autowired
	private EventLoopGroup bossGroup;
	@Autowired
	private EventLoopGroup workerGroup;
	@Autowired
	private ServerBootstrap serverBootstrap;
	@Getter
	private static final int PORT = 3333;

	@Getter
	@Autowired
	private WebSocketChildChannelHandler childChannelHandler;

	private ChannelFuture serverChannelFuture;
	
	public WebSocketServer() {}

	@Override
	public void run() {
        build();
	}
	
	/**
	 * 描述：启动Netty Websocket服务器
	 */
	public void build() {
		try {
		    long begin = System.currentTimeMillis();

			//boss辅助客户端的tcp连接请求  worker负责与客户端之前的读写操作
			serverBootstrap.group(bossGroup, workerGroup)
				//配置客户端的channel类型
			   .channel(NioServerSocketChannel.class)
				//配置TCP参数，握手字符串长度设置
			   .option(ChannelOption.SO_BACKLOG, 1024)
				//TCP_NODELAY算法，尽可能发送大块数据，减少充斥的小块数据
				.childOption(ChannelOption.TCP_NODELAY, true)
				//开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
			   .childOption(ChannelOption.SO_KEEPALIVE, true)
				//配置固定长度接收缓存区分配器
			   .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))

				//绑定I/O事件的处理类,WebSocketChildChannelHandler中定义
			   .childHandler(childChannelHandler);

			long end = System.currentTimeMillis();
	        log.info("Netty Websocket服务器启动完成，耗时 " + (end - begin) + " ms，已绑定端口 " + PORT + " 阻塞式等候客户端连接");

			// 通过将服务器引导类绑定到指定端口，来创建服务器通道，并同步阻塞等待通道绑定完成
			serverChannelFuture = serverBootstrap.bind(PORT).sync();

			// 关闭通道并等待通道关闭完成（才能继续执行其后的代码）
			//serverChannelFuture.channel().closeFuture().sync();

		} catch (Exception e) {
		    log.info("创建Netty进程异常：" + Arrays.toString(e.getStackTrace()));
			bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
		}

	}
	
	/**
	 * 描述：关闭Netty Websocket服务器，主要是释放连接
	 *     连接包括：服务器连接serverChannel，
	 *     客户端TCP处理连接bossGroup，
	 *     客户端I/O操作连接workerGroup
	 *     
	 *     若只使用
	 *         bossGroupFuture = bossGroup.shutdownGracefully();
	 *         workerGroupFuture = workerGroup.shutdownGracefully();
	 *     会造成内存泄漏。
	 */
	public void close(){
	    serverChannelFuture.channel().close();
		Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
        
        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
        } catch (InterruptedException exception) {
			log.error("关闭 Netty进程异常：" + Arrays.toString(exception.getStackTrace()));
        }
	}

	public void setChildChannelHandler(WebSocketChildChannelHandler childChannelHandler) {
        this.childChannelHandler = childChannelHandler;
    }

}

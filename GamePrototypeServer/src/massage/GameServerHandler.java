package massage;

/**
 * @author DZQ
 * Handles a server-side channel.
 */

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//import java.net.InetAddress;
//import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Sharable
public class GameServerHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger logger = Logger
			.getLogger(GameServerHandler.class.getName());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Send greeting for a new connection. (do not send any massage here, or it will cause unexpected exception.)

		// ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName()
		// + "!\r\n");
		// ctx.write("It is " + new Date() + " now.\r\n");
		// ctx.write("Please input your id like id:20001 , and then input your command like 20001:start . \r\n");
		// ctx.flush();

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String request)
			throws Exception {

		// Generate and write a response.

//		System.out.println(request);
		RequestSorter rs = new RequestSorter();
		String req = rs.checkCommand(request);

		String response = rs.setResponse(req);

		// We do not need to write a ChannelBuffer here.
		// We know the encoder inserted at TelnetPipelineFactory will do the
		// conversion.
		
		ctx.write(response);

//		System.out.println(response);
		

		// Close the connection after sending 'Have a good day!'
		// if the client has sent 'quit'.
		if ("quit".equals(req)) {
			 ChannelFuture future = ctx.write("quit\r\n");
			 future.addListener(ChannelFutureListener.CLOSE);
		}
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		if(ctx.channel().isActive()){
			ctx.flush();
		}else{
			ctx.close();
		}
	}
	

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.log(Level.WARNING, "Unexpected exception from downstream.",
				cause);
		ctx.close();
	}

}

package massage;

/**
 * @author DZQ
 * Simplistic telnet server.
 */

import de.ovgu.tools.ConfigurationReader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.h2.jdbcx.JdbcConnectionPool;

import h2.DataBackup;
import h2.H2Accessor;
import h2.InitH2;


public class GameServer {

	private final int port;
	

	public GameServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		//generate a connection pool in H2
		JdbcConnectionPool cp = null;
		
		try{// Initialize the in-memory database H2			
			cp = new InitH2().memoryMode();
			cp.setMaxConnections(30); // default is 10
			H2Accessor ha = new H2Accessor(cp);

			long beginTime = System.currentTimeMillis();
			
			ha.createTable();
						
			long endTime = System.currentTimeMillis();
			System.out.println(endTime-beginTime + "ms was used to initialize the database.");
			
			
			// get all data from H2
			DataBackup db = new DataBackup(ha);
			db.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {// Initialize the service					
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new GameServerInitializer());

			b.bind(port).sync().channel().closeFuture().sync();
			
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			cp.dispose();//close the connection with H2
		}
	}

	public static void main(String[] args) throws Exception {

		int port;

		ConfigurationReader creader = ConfigurationReader.getInstance();
		port = creader.getGameServerPort();

		new GameServer(port).run();
	}
}

package massage;

/**
 * @author DZQ
 * Simplistic Game client.
 */



import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Timer;
import java.util.TimerTask;



public class GameClient  extends Thread{

    private static String host;
    private static int port;
	public int clientId;
	
	private static int threadNum = 35; // maximum is 100, when there are multiprocess
	
	public  static int activeThreadNum = 0;
	
	private static int lastClientId = 0;
	
	public synchronized static void increaseActiveThreadNum(){
		++activeThreadNum;
	}
	
	public synchronized static void reduceActiveThreadNum(){
		--activeThreadNum;
	}

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
        	GameClientInitializer gci = new GameClientInitializer();
        	gci.ClientId = clientId;
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(gci);

            // Start the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();

            // Read commands from the screen.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // Sends the received line to the server.
                lastWriteFuture = ch.writeAndFlush(line + "\r\n");

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("quit".equals(line.toLowerCase())) {
                	ch.closeFuture().sync();
                	activeThreadNum--;
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 3) {
            System.err.println(
                    "Usage: " + GameClient.class.getSimpleName() +
                    " <host> <port> <client id>");
            return;
        }

        // Parse options.
        host = args[0];
        port = Integer.parseInt(args[1]);
       
        int id = Integer.parseInt(args[2]);
        
        for(int i = 0; i< threadNum; i++){
        	GameClient gc = new GameClient();
        	gc.clientId = id + i;
        	gc.start();
        	lastClientId = gc.clientId;
        }
        
        
        
    	// stop this program when all threads get 'quite'
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				System.out.println(lastClientId + ": " + activeThreadNum);
				if(activeThreadNum <= 0){
					System.exit(0);
				}
			}
		};

		timer.schedule(task, 5000, 5000); // after 0s send massages for
										// each 2s
        
    }
}

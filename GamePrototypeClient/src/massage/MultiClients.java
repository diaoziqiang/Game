package massage;

//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


public class MultiClients extends Thread {


	
	private static int ClientNum = 80;	// maximum is 90 --number of concurrent processes
	private static int ClientId = 10000; // It could be 10000, 20000, 30000... and 90000.
	
	private static int threadNumber = 1;
	
	public MultiClients() {
		System.out.println("Connecting " + threadNumber++);
	}
	
	
	public void run() {
		String path=System.getProperty("user.dir") + File.separator + "bin";
		//if running from command line ,remove the following line
		//path=path+File.separator+"bin";
//		System.out.println(path);
		try {
		ProcessBuilder pb = new ProcessBuilder("java","massage.GameClient","localhost","8080", String.valueOf(ClientId));
		pb.directory(new File(path));
		Process p = pb.start();
		ClientId += 100;// so the maximum multithread is 100
		
		
		// get output from single process (do not delete!!!!!!) 
		BufferedInputStream in = new BufferedInputStream(p.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String s;
        while ((s = br.readLine()) != null)
        	System.out.println(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static int count = 0;
	
	public static void main(String[] args) {
//		for (int i = 0; i < ClientNum; i++)
//			new MultiClients().start();
		
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				if(count++ < ClientNum){
					new MultiClients().start();
				}else{
					timer.cancel();
				}
				
			}
		};

		timer.schedule(task, 0, 1000); // after 0s send massages for
										// each 1s
		
		System.out.println("All Threads Started");
//		return;
		
	}
	
}
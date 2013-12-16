package massage;


import h2.H2Accessor;

/**
 * @author DZQ Handle a valid request from client.
 */

public class ValidRequestHandler {

	public String ClientId; // Sating login and ClientId here will cause
	// errors in the case of concurrent
	// connections.

	
	private H2Accessor ha = null;// should NOT be static
	

	public boolean run(String req) throws Exception {
		
		ha = new h2.H2Accessor();
		
		switch (req) {
		case "start":
			return startGame();
		case "get":
			return getItem();
		case "sell":
			return sellItem();
		case "quit":
			return quitGame();
		default:
			return false;
		}
	}

	private boolean startGame() throws Exception {
		System.out.println(ClientId + ": OK! Let's go!\r\n");
		ha.addSpecifiedRow(ClientId);
		return true;
	}

	private boolean getItem() throws Exception {
		System.out.println(ClientId + ": Here is your item: \r\n");
		ha.selectOneColumn(ClientId, "NAME", true);
		return true;
	}

	private boolean sellItem() throws Exception {
		System.out.println(ClientId + ": OK! I will do it!\r\n");
		ha.updateColumn(ClientId, "NAME", "AK");
		return true;
	}

	private boolean quitGame() throws Exception {
		System.out.println(ClientId + ": Have a good day!\r\n");
//		ha.selectOneRow(ClientId,true);
		return true;
	}

}
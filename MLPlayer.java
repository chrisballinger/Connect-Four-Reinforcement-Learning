import java.io.*;
import java.net.*;

public class MLPlayer {
    Player thePlayer;

    public MLPlayer(Player p) {
	thePlayer=p;
    }

    public void play() throws IOException,ClassNotFoundException {
	//open socket and in/out streams
	Socket sock = new Socket("localhost", Player.getSocketNumber(thePlayer));
	ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
	ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
	BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
	//get the game rules
	Rules gameRules = (Rules) in.readObject();
	System.out.printf("Num Rows: %d, Num Cols: %d, Num Connect: %d\n", gameRules.numRows, gameRules.numCols, gameRules.numConnect);
	//start playing the game
	System.out.println("Waiting...");
	GameMessage mess = (GameMessage) in.readObject(); //wait for initial message
	int move;
	while(mess.win == Player.EMPTY) {
	    if(mess.move != -1) {
		System.out.printf("Player %s moves %d\n", Player.otherPlayer(thePlayer),mess.move);
	    }
	    System.out.println("Your move?");
	    move = Integer.parseInt(sysin.readLine());
	    mess.move=move;
	    out.writeObject(mess);
	    mess = (GameMessage) in.readObject();
	}
	System.out.printf("Player %s wins.\n", mess.win);
	sock.close();

    }
    public static void main(String[] args) {
	if(args.length != 1) {
	    System.out.println("Usage:\n java MPLayer [1|2]");
	    System.exit(-1);
	}
	int which_player = Integer.parseInt(args[0]);
	Player p=null;
	if(which_player == 1) {
	    p = Player.ONE;
	}  
	else if ( which_player == 2) {
	    p = Player.TWO;
	}
	else {
	    System.out.println("Usage:\n java MPLayer [1|2]");
	    System.exit(-1);
	}
	MLPlayer me = new MLPlayer(p);
	try {
	    me.play();
	} catch(IOException ioe) {
	    ioe.printStackTrace();
	} catch(ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	}

    }

}

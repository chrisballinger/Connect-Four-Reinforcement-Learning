import java.io.*;
import java.net.*;

public class MLPlayerAlpha
{
	// An internal board representation.
	private int ppInternalBoard[][] = null;

	// The player.
    Player thePlayer;

	// The player id of this player.
	int iID = 0;

	// Constructor.
    public MLPlayerAlpha(Player p)
	{
		thePlayer=p;
    }

	// The play function, which connects to the socket and plays.
    public void play(int iMyID) throws IOException,ClassNotFoundException
	{
		// Open socket and in/out streams.
		Socket sock = new Socket("localhost", Player.getSocketNumber(thePlayer));
		ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

		// Set the id.
		iID = iMyID;

		// Get the game rules.
		Rules gameRules = (Rules)in.readObject();
		System.out.printf("Num Rows: %d, Num Cols: %d, Num Connect: %d\n", gameRules.numRows, gameRules.numCols, gameRules.numConnect);

		// Create the internal board.
		ppInternalBoard = new int[gameRules.numRows][gameRules.numCols];
		for (int r = 0; r < gameRules.numRows; r++)
			for (int c = 0; c < gameRules.numCols; c++)
				ppInternalBoard[r][c] = 0;

		// Start playing the game, first by waiting fo the initial message.
		System.out.println("Waiting...");
		GameMessage mess = (GameMessage)in.readObject();

		// The main game loop.
		int move = 0;
		while(mess.win == Player.EMPTY)
		{
			// Output the game board.
			for (int r = 0; r < gameRules.numRows; r++)
			{
				for (int c = 0; c < gameRules.numCols; c++)
				{
					System.out.print(ppInternalBoard[r][c] + " ");
				}
				System.out.println();
			}

			// If the first message is not the begin message (-1), then record what the other player did.
			if(mess.move != -1)
			{
				int r = 0;
				for (r = 1; r < gameRules.numRows; r++)
				{
					if (ppInternalBoard[r][mess.move] != 0)
					{
						ppInternalBoard[r - 1][mess.move] = iMyID;
						break;
					}
				}
				if (r == gameRules.numRows) ppInternalBoard[r - 1][mess.move] = iMyID;

				//System.out.printf("Player %s moves %d\n", Player.otherPlayer(thePlayer), mess.move);
			}

			// Decide on which column to place the token.
			int iSelectedColumn = 0;
			do
			{
				iSelectedColumn = (int)((float)gameRules.numCols * Math.random());
			}
			while (ppInternalBoard[gameRules.numRows - 1][iSelectedColumn] != 0);

			// Write the game message and read the next one.
			mess.move = iSelectedColumn;
			out.writeObject(mess);
			mess = (GameMessage)in.readObject();

			/*
			System.out.println("Your move?");
			move = Integer.parseInt(sysin.readLine());
			mess.move=move;
			out.writeObject(mess);
			mess = (GameMessage) in.readObject();
			*/
		}

		// Output the winner.
		System.out.printf("Player %s wins.\n", mess.win);

		// Close the socket.
		sock.close();
    }

	// The main function.
    public static void main(String[] args)
	{
		// If no argument is specified, throw an error.
		if(args.length != 1)
		{
	    	System.out.println("Usage:\n java MPLayer [1|2]");
	    	System.exit(-1);
		}

		// Get the player.
		int iMyID = Integer.parseInt(args[0]);

		// Set the player object.
		Player p = null;
		if(iMyID == 1) p = Player.ONE;
		else if (iMyID == 2) p = Player.TWO;
		else
		{
			System.out.println("Usage:\n java MPLayer [1|2]");
			System.exit(-1);
		}

		// Create the MLPlayer object, and begin play.
		MLPlayerAlpha me = new MLPlayerAlpha(p);
		try
		{
			me.play(iMyID);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe)
		{
			cnfe.printStackTrace();
		}
    }
}


import java.io.*;
import java.net.*;

public class MLPlayerAlphaOne
{
	// If debug should be printed or not. 
	private boolean PRINT_DEBUG = false;

	// The constants that define the reinforcement learning algorithm.
	double epsilon = 0.2f;
	double eta = 0.25f;
	double gamma = 0.99f;

	// An internal board representation.
	private int internal_board[][] = null;

	// The player.
	Player thePlayer;

	// The player id of this player.
	int player_id = 0;

	// Constructor.
	public MLPlayerAlphaOne(Player p)
	{
		thePlayer = p;
	}

	// The play function, which connects to the socket and plays.
	public void play(int my_id) throws IOException,ClassNotFoundException
	{
		// Open socket and in/out streams.
		Socket sock = new Socket("localhost", Player.getSocketNumber(thePlayer));
		ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

		Weights weights = new Weights("alpha_weights.txt");

		// Create the weights file if it doesn't exist and initialize to default values.
		File weights_file = new File("alpha_weights.txt");
		if (!weights_file.exists())
		{
			System.out.println("Weights file does not exist. Creating one with default weights");
			weights_file.createNewFile();
			double[] initialWeights = new double[FeatureExplorer.getNumFeatures()];
			for(int x = 0; x < FeatureExplorer.getNumFeatures(); x++)
			{
				double k = 1.0f / Math.sqrt((double)FeatureExplorer.getNumFeatures());
				initialWeights[x] = 2.0f * Math.random() * k - k;
			}
			weights.setWeights(initialWeights);
			weights.saveWeights();
		}

		// Set the id.
		player_id = my_id;

		// Get the game rules.
		Rules gameRules = (Rules)in.readObject();
		if (PRINT_DEBUG) System.out.printf("Num Rows: %d, Num Cols: %d, Num Connect: %d\n", gameRules.numRows, gameRules.numCols, gameRules.numConnect);

		// Create the internal board.
		internal_board = new int[gameRules.numRows][gameRules.numCols];
		for (int r = 0; r < gameRules.numRows; r++)
			for (int c = 0; c < gameRules.numCols; c++)
				internal_board[r][c] = 0;

		// Start playing the game, first by waiting fo the initial message.
		if (PRINT_DEBUG) System.out.println("Waiting...");
		GameMessage mess = (GameMessage)in.readObject();

		// The main game loop.
		double approx_qsa = -1.0f;
		double y_i = 0.0f; // For the current board (state i+1).
		double[] x_i = new double[FeatureExplorer.getNumFeatures()]; // For the previous board after you took an action AND opponent took action (state i).
		double[] x_ip1 = new double[FeatureExplorer.getNumFeatures()]; // For the current board after you took an action AND oppoent took action (state i+1).
		boolean beginning = true;
		int move = 0;
		int selected_column = 0;
		while(mess.win == Player.EMPTY)
		{
			if (PRINT_DEBUG) weights.printWeights();
			// If the first message is not the begin message (-1), then record what the other player did.
			if(mess.move != -1)
			{
				int r = 0;
				for (r = 0; r < gameRules.numRows; r++)
				{
					if (internal_board[r][mess.move] != 0)
					{
						if (r == 0) break;
						internal_board[r - 1][mess.move] = player_id % 2 + 1;
						break;
					}
				}
				if (r > 0 && r == gameRules.numRows) internal_board[r - 1][mess.move] = player_id % 2 + 1;
				if (r == 0) System.out.println("Alpha has detected an improper move made by the other player.");
			}
			else
			{
				// Randomly move the first time if you are first.
				mess.move = (int)((float)gameRules.numCols * Math.random());

				// Create the features for the blank board.
				FeatureExplorer ff = new FeatureExplorer();
				ff.initialize(internal_board, gameRules.numRows, gameRules.numCols, -1, player_id);
				x_ip1 = ff.getFeatures(); // Previous board given that we chose this action.

				// Update internal representaiton and tell server about the move (e.g. take action).
				internal_board[gameRules.numRows - 1][mess.move] = player_id;
				out.writeObject(mess); // Tell the server.
				mess = (GameMessage)in.readObject();

				// Continue to the next state.
				continue;
			}

			// (Determine an action) Create features based on the current board layout.
			FeatureExplorer[] ff = new FeatureExplorer[gameRules.numCols];
			boolean[] ff_use = new boolean[gameRules.numCols];
			int numFeatures = FeatureExplorer.getNumFeatures();
			double[][] features = new double[gameRules.numCols][numFeatures];
			double[] wx = new double[gameRules.numCols];
			double[] sig = new double[gameRules.numCols];
			double[] w = weights.getWeights();
			double max = 0;
			int action = 0;
			double temp;
			for(int x = 0; x < gameRules.numCols; x++)
			{
				ff[x] = new FeatureExplorer();
				ff_use[x] = ff[x].initialize(internal_board, gameRules.numRows, gameRules.numCols, x, player_id);
				if(ff_use[x]) features[x] = ff[x].getFeatures();

				if (PRINT_DEBUG) System.out.println("Action Column " + x + " Features:");
				if (PRINT_DEBUG) printD(features[x]);

				for(int y = 0; y < numFeatures; y++)
				{
					//System.out.println("ff_use: " + ff_use[x]);
					if(ff_use[x])
					{
						//if(beginning)
						//{
						//	if(mess.move == x)
						//		wx[x] += ((double)features[x][y])*w[y];
						//	else
						//		wx[x] = 0;
						//}
						//else
						wx[x] += ((double)features[x][y]) * w[y];
					}
					else
						wx[x] = 0;
				}
				if(ff_use[x])
					sig[x] = sigmoid(wx[x]);
				else
					sig[x] = 0;

				if (PRINT_DEBUG) System.out.printf("... and the weights: wx[%d]: %f approx_qsa[%d]: %f\n", x, wx[x], x, sig[x]);
				if(x == 0)
				{
					max = sig[0];
					action = 0;
				}
				else if(sig[x] > max)
				{
					max = sig[x];
					action = x;
				}

//System.out.println("\t" + sig[x]);
			}

			// The Q(s, a) value is the max of all of these. (Not used, just for a note).
			approx_qsa = max;
			if (PRINT_DEBUG) System.out.println("----------------------->>> Decided to take action " + action);

			// (Exploration function) Epsilon-greedy.
			if(Math.random() > epsilon)
				selected_column = action;
			else
			{
				selected_column = (int)(Math.random() * gameRules.numCols);
				for (int i = 0; i < 1000 && internal_board[0][selected_column] != 0; i++) selected_column = (int)(Math.random() * gameRules.numCols);
				System.out.println("I'm exploring a bit! I choose action: " + selected_column);
			}

			// (Compute previous board layout) Previous is equal to the old current.
			for (int j = 0; j < FeatureExplorer.getNumFeatures(); j++) x_i[j] = x_ip1[j];

			// (Compute Current board layout) Since this is after you have gone and your opponent has gone, calculate the ***current*** board layout.
			FeatureExplorer ff_cur = new FeatureExplorer();
			ff_cur.initialize(internal_board, gameRules.numRows, gameRules.numCols, -1, player_id);
			x_ip1 = ff_cur.getFeatures();

			// (Update weights) Do the update of the weight vector for the previous iteration.
			if(beginning == false)
			{
				if (PRINT_DEBUG) System.out.println("Approximated Q(s, a) (Intermediate): " + approx_qsa + "\tplayer_id: " + player_id);
				sarsa(0, weights, x_i, x_ip1);
			}

			// (Take action) Update the internal representation for where this player put his token. Then, send the action to the server.
			int r = 0;
			for (r = 0; r < gameRules.numRows; r++)
			{
				if (internal_board[r][selected_column] != 0)
				{
					internal_board[r - 1][selected_column] = player_id;
					break;
				}
			}
			if (r == gameRules.numRows) internal_board[r - 1][selected_column] = player_id;
			mess.move = selected_column;
			out.writeObject(mess);
			mess = (GameMessage)in.readObject();

			// The beginning has happened now.
			beginning = false;
		}

		// Print out the final approximated Q(s, a) value and the player id.
		System.out.println("Approximated Q(s, a): " + approx_qsa + "\tplayer_id: " + player_id);

		// (Compute previous board layout) Previous is equal to the old current.
		for (int j = 0; j < FeatureExplorer.getNumFeatures(); j++) x_i[j] = x_ip1[j];

		// (Compute Current board layout) Since this is after you have gone and your opponent has gone, calculate the ***current*** board layout.
		FeatureExplorer ff_cur = new FeatureExplorer();
		ff_cur.initialize(internal_board, gameRules.numRows, gameRules.numCols, -1, player_id);
		x_ip1 = ff_cur.getFeatures();

		// (Final Weight Update) Do a final update based on win/loss.
		if(mess.win == thePlayer)
		{
			sarsa(1, weights, x_i, x_ip1);	//reward 1 for win
			System.out.println("MLPlayerAlphaOne has won.");
		}
		else
		{
			sarsa(0, weights, x_i, x_ip1);	//reward 0 for loss
			System.out.println("MLPlayerAlphaOne has lost.");
		}

		// Save the weights.
		weights.saveWeights();

		// Close the socket.
		sock.close();
	}

	private double sigmoid(double t)
	{
		return 1.0f/(1.0f+(double)Math.exp(-t));
	}

	private void sarsa(int reward, Weights weights, double x_i[], double[] x_ip1)
	{
		// Set some local variables.
		double[] w = weights.getWeights();
		double wx = 0.0f;
		double sig = 0.0f;

		// Compute y_i.
		double wxp1 = 0.0f;
		for (int j = 0; j < FeatureExplorer.getNumFeatures(); j++) wxp1 += w[j] * x_ip1[j];
		double sigp1 = sigmoid(wxp1);
		double y_i = reward + gamma * sigp1;

		// Print out some debug if needed.
		if (PRINT_DEBUG)
		{
			System.out.println("SARSA Weights:");
			weights.printWeights();
		}

		// For each feature, compute the dot product of w and x.
		for (int j = 0; j < FeatureExplorer.getNumFeatures(); j++) wx += w[j] * x_i[j];
		if (PRINT_DEBUG)
			System.out.println("\twx: " + wx);

		// Compute the sigmoid of this.
		sig = sigmoid(wx);
		if (PRINT_DEBUG)
			System.out.println("\tsigmoid(wx): " + sig);

		// Weight vector update.
		for (int j = 0; j < FeatureExplorer.getNumFeatures(); j++)
			w[j] = w[j] + eta * ((y_i - sig) * (sig) * (1 - sig) * x_i[j]);

		// Set the new weights.
		weights.setWeights(w);
		if (PRINT_DEBUG)
		{
			System.out.println("SARSA New Weights:");
			weights.printWeights();
		}
	}

	private void printD(double[] array)
	{
		for(int i = 0; i < array.length; i++)
			System.out.println("\t" + array[i]);
	}

	// The main function.
	public static void main(String[] args)
	{
		// If no argument is specified, throw an error.
		if(args.length != 1)
		{
	    		System.out.println("Usage:\n java MPlayerAlphaOne [1|2]");
	    		System.exit(-1);
		}

		// Get the player.
		int my_id = Integer.parseInt(args[0]);

		// Set the player object.
		Player p = null;
		if (my_id == 1) p = Player.ONE;
		else if (my_id == 2) p = Player.TWO;
		else
		{
			System.out.println("Usage:\n java MPlayerAlphaOne [1|2]");
			System.exit(-1);
		}

		// Create the MLPlayer object, and begin play.
		MLPlayerAlphaOne me = new MLPlayerAlphaOne(p);
		try
		{
			me.play(my_id);
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


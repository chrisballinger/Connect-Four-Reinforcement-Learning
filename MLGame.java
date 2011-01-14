import java.util.*;
import java.net.*;
import java.io.*;

public class MLGame {
    private Rules gameRules;
    private Vector<Vector<Player> > board; //indexed by column number then row, 
                                          //we number rows from the bottom
                                          //we number columns from the left

    public static void main(String[] args) {
	if(args.length != 3) {
	    System.out.println("Usage:\njava MLGame [num rows] [num cols] [num connect]\n");
	    System.exit(-1);
	}
	int num_rows = Integer.parseInt(args[0]);
	int num_cols = Integer.parseInt(args[1]);
	int num_connect = Integer.parseInt(args[2]);
	Rules r= new Rules(num_rows, num_cols, num_connect);
	MLGame mygame = new MLGame(r);
	try {
	    mygame.play();
	} catch(IOException ioe) {
	    ioe.printStackTrace();
	} catch(ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	}
	
    }

    public void play() throws IOException,ClassNotFoundException {
	int num_moves=0;
	ServerSocket ss1;
	ServerSocket ss2;
	Socket psocket1;
	Socket psocket2;
	ObjectInputStream p1in; //stream for reading from player 1
	ObjectInputStream p2in; //stream for reading from player 2
	ObjectOutputStream p1out; //stream for writing to player 1
	ObjectOutputStream p2out; //stream for writing to player 2
	//create server sockets
	ss1=new ServerSocket(Player.getSocketNumber(Player.ONE));
	ss2=new ServerSocket(Player.getSocketNumber(Player.TWO));
	psocket1 = ss1.accept();
	psocket2 = ss2.accept();
	//open strams
	p1in=new ObjectInputStream(psocket1.getInputStream());
	p2in=new ObjectInputStream(psocket2.getInputStream());
	p1out = new ObjectOutputStream(psocket1.getOutputStream());
	p2out = new ObjectOutputStream(psocket2.getOutputStream());
	//tell rules to the players
	p1out.writeObject(gameRules);
	p2out.writeObject(gameRules);
	//play game
	GameMessage moves = new GameMessage();
	Player winner=Player.EMPTY; //is Player.EMPTY until someone wins
	int col;
	while(true) {
	    //player 1
	    moves.win=Player.EMPTY;
	    p1out.writeObject(moves);
	    moves = (GameMessage) p1in.readObject();
	    col = moves.move;
	    winner = add(col, Player.ONE);
	    num_moves++;
	    if(winner != Player.EMPTY) {
		alertWin(winner, p1out, p2out, moves);
		break;
	    }
	    //player 2
	    moves.win=Player.EMPTY;
	    p2out.writeObject(moves);
	    moves = (GameMessage) p2in.readObject();
	    col=moves.move;
	    winner=add(col, Player.TWO);
	    num_moves++;
	    if(winner != Player.EMPTY) {
		alertWin(winner, p2out, p1out, moves);
		break;
	    }
	}
	System.out.printf("Game over in %d moves\n",num_moves);
        //close sockets
        psocket1.close();
	psocket2.close();
	ss1.close();
	ss2.close();
    }
    private void alertWin(Player winning_player, ObjectOutputStream p_lm_os, ObjectOutputStream p_other_os, GameMessage gm) throws IOException { //notify both players that someone has won
	GameMessage my_message = new GameMessage(gm); //my_message now has the last move
	my_message.win = winning_player; //set the winning player
	p_other_os.writeObject(my_message); //send last move and winning player to player who did not move last
	my_message.move = -1; //prepare to send a message to the player who moved last
	p_lm_os.writeObject(my_message); //tell player who moved last who the winner is

    }
    public MLGame(Rules r) {
	gameRules=new Rules(r);
	setupBoard();
    }
    private void setupBoard(){

	//create the board
	board=new Vector<Vector<Player> >(gameRules.numCols);
	for(int c=0; c<gameRules.numCols; c++) {
	    Vector<Player> col_vec=new Vector<Player>(gameRules.numRows);
	    for(int r=0; r<gameRules.numRows; r++) {
		col_vec.add(Player.EMPTY);
	    }
	    board.add(col_vec);
	}
    }
    private void showBoard() {
	Player p;
	StringBuffer sb=new StringBuffer(gameRules.numRows*(gameRules.numCols+1) + 1);
	for(int r=gameRules.numRows-1; r>=0; r--) {
	    for(int c=0; c<gameRules.numCols; c++) {
		p=board.get(c).get(r);
		switch(p) {
		    case ONE: sb.append(1);
		        break;
		    case TWO: sb.append("2");
		        break;
		    case EMPTY: sb.append("-");
		        break;
		    default: sb.append("-");
		        break;
		}
	    }
	    sb.append("\n");
	}
	sb.append("\n");
	System.out.print(sb);
    }
    //returns Player.EMPTY if game is not over, otherwise it returns the winning Player
    private Player add(int col, Player p) {
	if(col < 0 || col >= gameRules.numCols) {
	    System.out.printf("Illegal move by Player %s\n",p);
	    return Player.otherPlayer(p);
	}
	int row = getRowFromColumn(col);
	boolean win=false;
	//check for illegal move
	System.out.printf("Player %s moves to column %d:\n",p,col);
	if(row >= gameRules.numRows) {
	    System.out.printf("Illegal move by Player %s\n",p);
	    return Player.otherPlayer(p);
	}
	board.get(col).set(row, p);
	showBoard();
	win = checkWin(col, row, p);
	if(win) {
	    System.out.printf("Player %s wins\n",p);
	    return p;
	}
	return Player.EMPTY;
    }
    /*
    //reset everything to allow a new game to start
    private void endGame() {
	//clear board
	for(int c=0; c<gameRules.numCols; c++) {
	    for(int r=0; r<gameRules.numRows; r++) {
		board.get(c).set(r,Player.EMPTY)
	    }
	}
	//disconnect players

    }
    */
    private int getRowFromColumn(int col) {
	Vector<Player> col_vec=board.get(col);
	int index=gameRules.numRows;
	for(int r=0;r<gameRules.numRows;r++) {
	    if(col_vec.get(r) == Player.EMPTY) {
		index=r;
		break;
	    }
	}
	return index;
    }

    private boolean checkWin(int col, int row, Player p) {
	int num_left=0;
	int num_right=0;
	int num_below=0;
        int num_rightup=0;
        int num_leftup=0;
        int num_rightdown=0;
        int num_leftdown=0;

        //number of slots on the left
	for(int c = col-1; c >= 0; c--) {
	    if (board.get(c).get(row) == p) {
		num_left++;
	    }
	    else {
		break;
	    }
	}
        //number of slots on the right
	for(int c = col+1; c < gameRules.numCols; c++) {
	    if (board.get(c).get(row) == p) {
		num_right++;
	    }
	    else {
		break;
	    }
	}
        //number of slots below
	for(int r=row-1; r>=0; r--) {
	    if(board.get(col).get(r) == p) {
		num_below++;
	    }
	    else {
		break;
	    }
	}
        //there are 0 slots above
        //number of slots diagonally left and up
        for(int r=row+1, c=col-1; r<gameRules.numRows && c >= 0; r++, c--) {
	    if(board.get(c).get(r) == p) {
		num_leftup++;
	    }
	    else {
		break;
	    }
	}
        //number of slots diagonally left and down
        for(int r=row-1, c=col-1; r >= 0 && c >= 0; r--, c--) {
	    if(board.get(c).get(r) == p) {
		num_leftdown++;
	    }
	    else {
		break;
	    }
	}
        //number of slots diagonally right and up
        for(int r=row+1, c=col+1; r<gameRules.numRows && c < gameRules.numCols; r++, c++) {
	    if(board.get(c).get(r) == p) {
		num_rightup++;
	    }
	    else {
		break;
	    }
	}
        //number of slots diagonnaly right and down
        for(int r=row-1, c=col+1; r>=0 && c < gameRules.numCols; r--, c++) {
	    if(board.get(c).get(r) == p) {
		num_rightdown++;
	    }
	    else {
		break;
	    }
	}

	//test for gameRules.numConnect in a row
	if((1+num_left+num_right >= gameRules.numConnect) 
           || (1+num_below >= gameRules.numConnect)
           || (1+num_leftup + num_rightdown >= gameRules.numConnect)
           || (1+num_leftdown + num_rightup >= gameRules.numConnect)) {
	    return true;
	}
	else {
	    return false;
	}
    }
}

import java.io.*;

public class GameMessage implements Cloneable, Serializable {
    public int move; //this is the move to be played. A value of -1 means start the game.
    public Player win; // this is different from Player.EMPTY

    public GameMessage() {
	move = -1;
	win = Player.EMPTY;
    }
    public GameMessage(GameMessage gm) {
	move = gm.move;
	win = gm.win;
    }
}

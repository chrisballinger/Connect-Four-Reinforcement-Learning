

public enum Player {
    EMPTY, ONE, TWO;
    
    public static int getSocketNumber(Player p) {
	switch(p) {
	    case ONE: return 4441;
	    case TWO: return 4442;
  	    default: throw new RuntimeException("No player specified");
	}
    }
    public static Player otherPlayer(Player p) {
	if(p == ONE) {
	    return TWO;
	}
	if(p == TWO) {
	    return ONE;
	}
	return EMPTY;
    }
}

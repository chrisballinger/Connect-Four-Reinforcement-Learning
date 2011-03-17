import java.util.*;
import java.io.*;


// Used exclusively for debugging.
public class FeatureTester
{
	public static void main(String args[])
	{
		System.out.println("Loading random board...");

		BoardLoader bl = new BoardLoader();
		bl.printBoard();

		FeatureExplorer fe = new FeatureExplorer();
		fe.initialize(bl.getBoard(), 6, 7, -1, 1);
		fe.printFeatures();

		System.out.println("Done.");
	}
}


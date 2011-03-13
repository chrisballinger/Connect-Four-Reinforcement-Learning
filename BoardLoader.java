import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

public class BoardLoader
{
	private int[][] board;
	
	public BoardLoader()
	{
		this("boards");
	}
	
	public BoardLoader(String boardDirectory)
	{
		File dir = new File(boardDirectory);	
        File[] files = dir.listFiles();
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
		StringTokenizer st;
		int randomFile = (int)(files.length * Math.random());
		int row = 0;
		int col = 0;
		board = new int[6][7];

        try {
            reader = new BufferedReader(new FileReader(files[randomFile]));
            String text = null;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) 
			{
				st = new StringTokenizer(text, ", ");
				while (st.hasMoreTokens()) 
				{
					 board[row][col] = Integer.parseInt(st.nextToken());
					 col++;
				}
				col = 0;
                row++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }		
	}
	
	public int[][] getBoard()
	{
		return board;
	}
	
	public void printBoard()
	{
		for (int r = 0; r < board.length; r++)
		{
			for (int c = 0; c < board[0].length; c++)
			{
				System.out.print(board[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	/*public static void main(String args[])
	{
		BoardLoader test = new BoardLoader();
		test.printBoard();
	}*/
}
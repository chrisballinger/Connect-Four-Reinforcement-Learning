import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

// This class finds "features" in a given grid board.
public class FeatureExplorer
{
	// This is the grid where features will be found.
	private int[][] grid = null;
	private int num_rows = 0;
	private int num_cols = 0;

	// The constructor.
	public FeatureExplorer()
	{	}
	
	// Returns false if there is an error. True if good.
	public boolean initialize(int[][] new_grid, int new_num_rows, int new_num_cols, int action_column, int player_id)
	{
		// Setup some stuff.
		num_rows = new_num_rows;
		num_cols = new_num_cols;
		grid = new int[num_rows][num_cols];
		for (int r = 0; r < num_rows; r++)
		{
			for (int c = 0; c < num_cols; c++)
			{
				if (new_grid[num_rows - r - 1][c] == 0) grid[r][c] = 0;
				else if (new_grid[num_rows - r - 1][c] == player_id) grid[r][c] = 1;
				else grid[r][c] = 2;
			}
		}

		// Check.
		if (action_column!= -1 && grid[num_rows - 1][action_column] != 0) return false;

		// Action Column!
		if (action_column != -1)
		{
			int r = num_rows - 1;
			for (r = num_rows - 1; r >= 0; r--)
			{
				if (grid[r][action_column] != 0)
				{
					grid[r + 1][action_column] = 1; // You (1) placed a token here.
					break;
				}
			}
		}
		return true;
	}

	// Get the total number of features.
	public static int getNumFeatures()
	{
		// (num_players * num_combinations) + counting_directional_pieces + num_empty_red_blue_cells + avg_dist_from_left_right_top_bottom + players_dist_to_win
		return (2 * 8) + 4 + 3 + 4;// + 2;
	}

	// Get all of the features.
	public double[] getFeatures()
	{
		double[] features = new double[getNumFeatures()];
		double[] temp = getBaseFeatures();
		for (int i = 0; i < (2 * 8) + 4; i++) features[i] = temp[i];
		temp = getNumEmptyRedBlueCells();
		for (int i = 0; i < 3; i++) features[i + (2 * 8) + 4] = temp[i];
		features[(2 * 8) + 4 + 3 + 0] = getAverageLocationOf(1, true);	// Player 1 rows
		features[(2 * 8) + 4 + 3 + 1] = getAverageLocationOf(1, false);	// Player 1 columns
		features[(2 * 8) + 4 + 3 + 2] = getAverageLocationOf(2, true);	// Player 2 rows
		features[(2 * 8) + 4 + 3 + 3] = getAverageLocationOf(2, false);	// Player 2 columns
		return features;
	}

	// Find the number of open cells, cells with red, and cells with blue.
	public double[] getNumEmptyRedBlueCells()
	{
		double[] result = new double[3];
		for (int r = 0; r < num_rows; r++)
		{
			for (int c = 0; c < num_cols; c++)
			{
				if (grid[r][c] == 0) result[0] += 1.0f;
				else if (grid[r][c] == 1) result[1] += 1.0f;
				else if (grid[r][c] == 2) result[2] += 1.0f;
			}
		}
		return result;
	}

	// Find the average location of all red and all blue.
	public double getAverageLocationOf(int player, boolean check_rows)
	{
		double num_found = 0;
		double count = 0;
		for (int r = 0; r < num_rows; r++)
		{
			for (int c = 0; c < num_cols; c++)
			{
				if (grid[r][c] == player)
				{
					if (check_rows) count += (double)r;
					else count += (double)c;
					num_found += 1.0f;
				}
			}
		}
		return count / num_found;
	}

	// Find the number of base features that count the number of "in-a-rows" there are.
	public double[] getBaseFeatures()
	{
		// The result.
		double[] result = new double[2 * 8 + 4];
		// Note: These are what it is counting: The Number Of...
		//   X
		//   XX
		//   XXX
		//   XXXX
		//   X_X
		//   X__X
		//   XX_X
		//   X_XX
		//   ... row pieces
		//   ... column pieces
		//   ... diagonal up pieces
		//   ... diagonal down pieces

		// Begin by making vectors out of all the rows, columns, left diagonals, and right diagonals.
		ArrayList[][] vectors = new ArrayList[4][];
		vectors[0] = new ArrayList[num_rows];					// Rows.
		vectors[1] = new ArrayList[num_cols];					// Columns.
		vectors[2] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Up.
		vectors[3] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Down.
		for (int r = 0; r < num_rows; r++) // Rows.
		{
			vectors[0][r] = new ArrayList();
			for (int c = 0; c < num_cols; c++) vectors[0][r].add(new Integer(grid[r][c]));
		}
		for (int c = 0; c < num_cols; c++) // Columns.
		{
			vectors[1][c] = new ArrayList();
			for (int r = 0; r < num_rows; r++) vectors[1][c].add(new Integer(grid[r][c]));
		}
		for (int r = 0; r < num_rows; r++) // Diagonal Up - Part 1.
		{
			vectors[2][r] = new ArrayList();
			for (int c = 0; c < num_cols; c++)
			{
				if (r + c >= num_rows) break;
				vectors[2][r].add(new Integer(grid[r + c][c]));
			}
		}
		for (int c = 1; c < num_cols; c++) // Diagonal Up - Part 2. (Note the 1)
		{
			vectors[2][num_rows - 1 + c] = new ArrayList();
			for (int r = 0; r < num_rows; r++)
			{
				if (c + r >= num_cols) break;
				vectors[2][num_rows - 1 + c].add(new Integer(grid[r][c + r]));
			}
		}
		for (int r = 0; r < num_rows; r++) // Diagonal Down - Part 1.
		{
			vectors[3][r] = new ArrayList();
			for (int c = 0; c < num_cols; c++)
			{
				if (r - c < 0) break;
				vectors[3][r].add(new Integer(grid[r - c][c]));
			}
		}
		for (int c = 1; c < num_cols; c++) // Diagonal Down - Part 2. (Note the 1)
		{
			vectors[3][num_rows - 1 + c] = new ArrayList();
			for (int r = 0; r < num_rows; r++)
			{
				if (c - r < 0) break;
				vectors[3][num_rows - 1 + c].add(new Integer(grid[r][c - r]));
			}
		}

		// Now that all of these are created, search for all the types to get a count.
		int x1 = 0;		int x2 = 0;
		int xx1 = 0;	int xx2 = 0;
		int x_x1 = 0;	int x_x2 = 0;
		int x__x1 = 0;	int x__x2 = 0;
		int xxx1 = 0;	int xxx2 = 0;
		int xx_x1 = 0;	int xx_x2 = 0;
		int x_xx1 = 0;	int x_xx2 = 0;
		int xxxx1 = 0;	int xxxx2 = 0;
		int num_row_pieces = 0;
		int num_col_pieces = 0;
		int num_diag_up_pieces = 0;
		int num_diag_down_pieces = 0;
		for (int i = 0; i < 4; i++)
		{
			// Loop through all of the rows, cols, or rows + cols.
			int num_pieces_found = 0;
			int max_value = 0;
			if (i == 0) max_value = num_rows;
			else if (i == 1) max_value = num_cols;
			else if (i == 2 || i == 3) max_value = num_rows + num_cols - 1;
			for (int j = 0; j < max_value; j++)
			{
				// If the size is too small, pad with 0's to make the vector large enough for comparison.
				if (vectors[i][j].size() == 1) for (int k = 0; k < 3; k++) vectors[i][j].add(new Integer(0));
				else if (vectors[i][j].size() == 2) for (int k = 0; k < 2; k++) vectors[i][j].add(new Integer(0));
				else if (vectors[i][j].size() == 3) vectors[i][j].add(new Integer(0));

				// This vector here now corresponds to something we need to look for patterns in. Each of the patterns is a "piece" as described above.
				for (int k = 0; k < vectors[i][j].size() - 3; k++)
				{
					// Look for the various patterns.
					int[] v = new int[4];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					if ((v[0] == 1 && v[1] == 0 && v[2] == 0 && v[3] == 0) || (v[0] == 0 && v[1] == 1 && v[2] == 0 && v[3] == 0) ||
						(v[0] == 0 && v[1] == 0 && v[2] == 1 && v[3] == 0) || (v[0] == 0 && v[1] == 0 && v[2] == 0 && v[3] == 1))
					{ x1++;		num_pieces_found++; }
					else if ((v[0] == 1 && v[1] == 1 && v[2] == 0 && v[3] == 0) || (v[0] == 0 && v[1] == 1 && v[2] == 1 && v[3] == 0) ||
							 (v[0] == 0 && v[1] == 0 && v[2] == 1 && v[3] == 1))
					{ xx1++;	num_pieces_found++; }
					else if ((v[0] == 1 && v[1] == 0 && v[2] == 1 && v[3] == 0) || (v[0] == 0 && v[1] == 1 && v[2] == 0 && v[3] == 1))
					{ x_x1++;	num_pieces_found++; }
					else if (v[0] == 1 && v[1] == 0 && v[2] == 0 && v[3] == 1)
					{ x__x1++;	num_pieces_found++; }
					else if ((v[0] == 1 && v[1] == 1 && v[2] == 1 && v[3] == 0) || (v[0] == 0 && v[1] == 1 && v[2] == 1 && v[3] == 1))
					{ xxx1++;	num_pieces_found++; }
					else if (v[0] == 1 && v[1] == 1 && v[2] == 0 && v[3] == 1)
					{ xx_x1++;	num_pieces_found++; }
					else if (v[0] == 1 && v[1] == 0 && v[2] == 1 && v[3] == 1)
					{ x_xx1++;	num_pieces_found++; }
					else if (v[0] == 1 && v[1] == 1 && v[2] == 1 && v[3] == 1)
					{ xxxx1++;	num_pieces_found++; }
					else if ((v[0] == 2 && v[1] == 0 && v[2] == 0 && v[3] == 0) || (v[0] == 0 && v[1] == 2 && v[2] == 0 && v[3] == 0) ||
							 (v[0] == 0 && v[1] == 0 && v[2] == 2 && v[3] == 0) || (v[0] == 0 && v[1] == 0 && v[2] == 0 && v[3] == 2))
					{ x2++;		num_pieces_found++; }
					else if ((v[0] == 2 && v[1] == 2 && v[2] == 0 && v[3] == 0) || (v[0] == 0 && v[1] == 2 && v[2] == 2 && v[3] == 0) ||
							 (v[0] == 0 && v[1] == 0 && v[2] == 2 && v[3] == 2))
					{ xx2++;	num_pieces_found++; }
					else if ((v[0] == 2 && v[1] == 0 && v[2] == 2 && v[3] == 0) || (v[0] == 0 && v[1] == 2 && v[2] == 0 && v[3] == 2))
					{ x_x2++;	num_pieces_found++; }
					else if (v[0] == 2 && v[1] == 0 && v[2] == 0 && v[3] == 2)
					{ x__x2++;	num_pieces_found++; }
					else if ((v[0] == 2 && v[1] == 2 && v[2] == 2 && v[3] == 0) || (v[0] == 0 && v[1] == 2 && v[2] == 2 && v[3] == 2))
					{ xxx2++;	num_pieces_found++; }
					else if (v[0] == 2 && v[1] == 2 && v[2] == 0 && v[3] == 2)
					{ xx_x2++;	num_pieces_found++; }
					else if (v[0] == 2 && v[1] == 0 && v[2] == 2 && v[3] == 2)
					{ x_xx2++;	num_pieces_found++; }
					else if (v[0] == 2 && v[1] == 2 && v[2] == 2 && v[3] == 2)
					{ xxxx2++;	num_pieces_found++; }
				}
			}

			// Based on the type of group it is, add to a different pool of pieces count.
			if (i == 0) num_row_pieces = num_pieces_found;
			else if (i == 1) num_col_pieces = num_pieces_found;
			else if (i == 2) num_diag_up_pieces = num_pieces_found;
			else if (i == 3) num_diag_down_pieces = num_pieces_found;
		}

		// Set all the results.
		result[0] = (double)x1;		result[1] = (double)xx1;		result[2] = (double)x_x1;		result[3] = (double)x__x1;
		result[4] = (double)xxx1;	result[5] = (double)xx_x1;		result[6] = (double)x_xx1;		result[7] = (double)xxxx1;
		result[8] = (double)x2;		result[9] = (double)xx2;		result[10] = (double)x_x2;		result[11] = (double)x__x2;
		result[12] = (double)xxx2;	result[13] = (double)xx_x2;		result[14] = (double)x_xx2;		result[15] = (double)xxxx2;
		result[16] = (double)num_row_pieces;						result[17] = (double)num_col_pieces;
		result[18] = (double)num_diag_up_pieces;					result[19] = (double)num_diag_down_pieces;

		// Return the result.
		return result;
	}
}


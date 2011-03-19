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
			if(r==-1)
				grid[0][action_column] = 1;
		}

		/*System.out.println("player_id:" + player_id);
		for (int r = 0; r < num_rows; r++)
		{
			for (int c = 0; c < num_cols; c++)
			{
				System.out.print(grid[r][c] + " ");
			}
			System.out.println();
		}*/
		return true;
	}

	// Get the total number of features.
	public static int getNumFeatures()
	{
		return (2 * 1) + (2 * 7) + 4 + 4 + 1 + 4;
	}

	// Get all of the features.
	public double[] getFeatures()
	{
		double[] features = new double[getNumFeatures()];
		double[] temp = getSinglePieces();
		features[0] = temp[0];
		features[1] = temp[1];
		temp = getBaseFeatures();
		for (int i = 0; i < (2 * 7) + 4 + 4; i++) features[(2 * 1) + i] = temp[i];
		temp = getNumEmptyRedBlueCells();
		features[(2 * 1) + (2 * 7) + 4 + 4 + 0] = temp[0];
		features[(2 * 1) + (2 * 7) + 4 + 4 + 1 + 0] = getAverageLocationOf(1, true);	// Player 1 rows
		features[(2 * 1) + (2 * 7) + 4 + 4 + 1 + 1] = getAverageLocationOf(1, false);	// Player 1 columns
		features[(2 * 1) + (2 * 7) + 4 + 4 + 1 + 2] = getAverageLocationOf(2, true);	// Player 2 rows
		features[(2 * 1) + (2 * 7) + 4 + 4 + 1 + 3] = getAverageLocationOf(2, false);	// Player 2 columns
		return features;
	}

	// Print the features out.
	public void printFeatures()
	{
		System.out.println("Features:");
		double[] features = getFeatures();
		System.out.println("\tNum Isolated 1 = " + features[0]);
		System.out.println("\tNum Isolated 2 = " + features[1]);
		System.out.println("\t1 xx =   " + features[2]);
		System.out.println("\t1 x_x =  " + features[3]);
		System.out.println("\t1 x__x = " + features[4]);
		System.out.println("\t1 xxx =  " + features[5]);
		System.out.println("\t1 xx_x = " + features[6]);
		System.out.println("\t1 x_xx = " + features[7]);
		System.out.println("\t1 xxxx = " + features[8]);
		System.out.println("\t2 xx =   " + features[9]);
		System.out.println("\t2 x_x =  " + features[10]);
		System.out.println("\t2 x__x = " + features[11]);
		System.out.println("\t2 xxx =  " + features[12]);
		System.out.println("\t2 xx_x = " + features[13]);
		System.out.println("\t2 x_xx = " + features[14]);
		System.out.println("\t2 xxxx = " + features[15]);
		System.out.println("\tNum Row Pieces =           " + features[16]);
		System.out.println("\tNum Column Pieces =        " + features[17]);
		System.out.println("\tNum Diagonal Up Pieces =   " + features[18]);
		System.out.println("\tNum Diagonal Down Pieces = " + features[19]);
		System.out.println("\tNum Threat Points of 1 =   " + features[20]);
		System.out.println("\tNum Threat Points of 2 =   " + features[21]);
		System.out.println("\tThreat Level Bravo (1) =   " + features[22]);
		System.out.println("\tThreat Level Tango (2) =   " + features[23]);
		//System.out.println("\tThreat Level Charlie (1) = " + features[24]);
		//System.out.println("\tThreat Level Zulu (2) =    " + features[25]);
		System.out.println("\tNum Empty Cells / ((num_rows + num_cols) / 2) (i.e. Game Counter) = " + features[24]);
		//System.out.println("\tNum 1 Cells =  " + features[21]);
		//System.out.println("\tNum 2 Cells =   " + features[22]);
		System.out.println("\tNum Average Row Distance to Center of 1 =    " + features[25]);
		System.out.println("\tNum Average Column Distance to Center of 1 = " + features[26]);
		System.out.println("\tNum Average Row Distance to Center of 2 =    " + features[27]);
		System.out.println("\tNum Average Column Distance to Center of 2 = " + features[28]);
	}


	// Get the number of isolated points.
	public double[] getSinglePieces()
	{
		double[] singles = new double[2];
		singles[0] = 0.0f;
		singles[1] = 0.0f;
		for(int r = 0; r < num_rows; r++)
		{
			for(int c = 0; c < num_cols; c++)
			{
				int counter = 0;
				for(int i = -1; i <= 1; i++)
					for(int j = -1; j <= 1; j++)
						if((r + i) >= 0 && (r + i) < num_rows && (c + j) >= 0 && (c + j) < num_cols)
							if(grid[r + i][c + j] == grid[r][c])
								counter++;
				if(counter == 1) singles[grid[r][c] - 1] += 1.0f;
			}
		}
		return singles;	
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
		result[0] = result[0] / (((double)num_cols + (double)num_rows) / 2.0f);
		result[1] = result[1] / (((double)num_cols + (double)num_rows) / 2.0f);
		result[2] = result[2] / (((double)num_cols + (double)num_rows) / 2.0f);
		
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
					if (check_rows) count += Math.abs((double)r - (double)num_rows / 2.0f);
					else count += Math.abs((double)c - (double)num_cols / 2.0f);
					num_found += 1.0f;
				}
			}
		}
		if(check_rows) return (count / num_found);
		else return (count / num_found);
	}

	// Find the number of base features that count the number of "in-a-rows" there are.
	public double[] getBaseFeatures()
	{
		// The result.
		double[] result = new double[2 * 7 + 4 + 4];
		// Note: These are what it is counting: The Number Of...
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
		//   ... num threats for 1
		//   ... num threats for 2
		//   ... summed distance to threats for 1, divided by 2
		//   ... summed distance to threats for 2, divided by 2
		//   ... summed distance to places that will make threats for 1, divided by 10
		//   ... summed distance to places that will make threats for 2, divided by 10

		// Begin by making vectors out of all the rows, columns, left diagonals, and right diagonals.
		ArrayList[][] vectors = new ArrayList[4][];
		ArrayList[][] vectors_row = new ArrayList[4][];
		ArrayList[][] vectors_col = new ArrayList[4][];

		vectors[0] = new ArrayList[num_rows];					// Rows.
		vectors[1] = new ArrayList[num_cols];					// Columns.
		vectors[2] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Up.
		vectors[3] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Down.

		vectors_row[0] = new ArrayList[num_rows];					// Rows.
		vectors_row[1] = new ArrayList[num_cols];					// Columns.
		vectors_row[2] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Up.
		vectors_row[3] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Down.

		vectors_col[0] = new ArrayList[num_rows];					// Rows.
		vectors_col[1] = new ArrayList[num_cols];					// Columns.
		vectors_col[2] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Up.
		vectors_col[3] = new ArrayList[num_rows + num_cols - 1];	// Diagonal Down.

		for (int r = 0; r < num_rows; r++) // Rows.
		{
			vectors[0][r] = new ArrayList();
			vectors[0][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++) vectors[0][r].add(new Integer(grid[r][c]));

			vectors_row[0][r] = new ArrayList();
			vectors_row[0][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++) vectors_row[0][r].add(new Integer(r));

			vectors_col[0][r] = new ArrayList();
			vectors_col[0][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++) vectors_col[0][r].add(new Integer(c));
		}

		for (int c = 0; c < num_cols; c++) // Columns.
		{
			vectors[1][c] = new ArrayList();
			vectors[1][c].add(new Integer(-1));
			for (int r = 0; r < num_rows; r++) vectors[1][c].add(new Integer(grid[r][c]));

			vectors_row[1][c] = new ArrayList();
			vectors_row[1][c].add(new Integer(-1));
			for (int r = 0; r < num_rows; r++) vectors_row[1][c].add(new Integer(r));

			vectors_col[1][c] = new ArrayList();
			vectors_col[1][c].add(new Integer(-1));
			for (int r = 0; r < num_rows; r++) vectors_col[1][c].add(new Integer(c));
		}
		for (int r = 0; r < num_rows; r++) // Diagonal Up - Part 1.
		{
			vectors[2][r] = new ArrayList();
			vectors[2][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++)
			{
				if (r + c >= num_rows) break;
				vectors[2][r].add(new Integer(grid[r + c][c]));
			}

			vectors_row[2][r] = new ArrayList();
			vectors_row[2][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++)
			{
				if (r + c >= num_rows) break;
				vectors_row[2][r].add(new Integer(r + c));
			}

			vectors_col[2][r] = new ArrayList();
			vectors_col[2][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++)
			{
				if (r + c >= num_rows) break;
				vectors_col[2][r].add(new Integer(c));
			}
		}
		for (int c = 1; c < num_cols; c++) // Diagonal Up - Part 2. (Note the 1)
		{
			vectors[2][num_rows - 1 + c] = new ArrayList();
			vectors[2][num_rows - 1 + c].add(new Integer(-1));
			for (int r = 0; r < num_rows; r++)
			{
				if (c + r >= num_cols) break;
				vectors[2][num_rows - 1 + c].add(new Integer(grid[r][c + r]));
			}

			vectors_row[2][num_rows - 1 + c] = new ArrayList();
			vectors_row[2][num_rows - 1 + c].add(new Integer(-1));
			for (int r = 0; r < num_rows; r++)
			{
				if (c + r >= num_cols) break;
				vectors_row[2][num_rows - 1 + c].add(new Integer(r));
			}

			vectors_col[2][num_rows - 1 + c] = new ArrayList();
			vectors_col[2][num_rows - 1 + c].add(new Integer(-1));
			for (int r = 0; r < num_rows; r++)
			{
				if (c + r >= num_cols) break;
				vectors_col[2][num_rows - 1 + c].add(new Integer(c + r));
			}
		}
		for (int r = 0; r < num_rows; r++) // Diagonal Down - Part 1.
		{
			vectors[3][r] = new ArrayList();
			vectors[3][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++)
			{
				if (r - c < 0) break;
				vectors[3][r].add(new Integer(grid[r - c][c]));
			}

			vectors_row[3][r] = new ArrayList();
			vectors_row[3][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++)
			{
				if (r - c < 0) break;
				vectors_row[3][r].add(new Integer(r - c));
			}

			vectors_col[3][r] = new ArrayList();
			vectors_col[3][r].add(new Integer(-1));
			for (int c = 0; c < num_cols; c++)
			{
				if (r - c < 0) break;
				vectors_col[3][r].add(new Integer(c));
			}
		}
		for (int c = 1; c < num_cols; c++) // Diagonal Down - Part 2. (Note the 1)
		{
			vectors[3][num_rows - 1 + c] = new ArrayList();
			vectors[3][num_rows - 1 + c].add(new Integer(-1));
			for (int r = num_rows - 1; r >= 0; r--)
			{
				if (c + (num_rows - 1) - r >= num_cols) break;
				vectors[3][num_rows - 1 + c].add(new Integer(grid[r][c + (num_rows - 1) - r]));
			}

			vectors_row[3][num_rows - 1 + c] = new ArrayList();
			vectors_row[3][num_rows - 1 + c].add(new Integer(-1));
			for (int r = num_rows - 1; r >= 0; r--)
			{
				if (c + (num_rows - 1) - r >= num_cols) break;
				vectors_row[3][num_rows - 1 + c].add(new Integer(r));
			}

			vectors_col[3][num_rows - 1 + c] = new ArrayList();
			vectors_col[3][num_rows - 1 + c].add(new Integer(-1));
			for (int r = num_rows - 1; r >= 0; r--)
			{
				if (c + (num_rows - 1) - r >= num_cols) break;
				vectors_col[3][num_rows - 1 + c].add(new Integer(c + (num_rows - 1) - r));
			}
		}

		// Now that all of these are created, search for all the types to get a count.
		/*int x1 = 0;		int x2 = 0;*/
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
		int num_threat_points1 = 0;
		int num_threat_points2 = 0;
		double threat_level_bravo = 0.0f;
		double threat_level_tango = 0.0f;
		double threat_level_charlie = 0.0f;
		double threat_level_zulu = 0.0f;
		for (int i = 0; i < 4; i++)
		{
			// Loop through all of the rows, cols, or rows + cols.
			int num_pieces_found = 0;
			int max_value = 0;
			if (i == 0) max_value = num_rows;
			else if (i == 1) max_value = num_cols;
			else if (i == 2 || i == 3) max_value = num_rows + num_cols - 1;

//System.out.println("TYPE OF FEATURE i=" + i + ":");

			for (int j = 0; j < max_value; j++)
			{
				// If the size is too small, pad with 0's to make the vector large enough for comparison.
				if (vectors[i][j].size() == 1)		for (int k = 0; k < 3; k++) vectors[i][j].add(new Integer(-1));
				else if (vectors[i][j].size() == 2)	for (int k = 0; k < 2; k++) vectors[i][j].add(new Integer(-1));
				else if (vectors[i][j].size() == 3)	for (int k = 0; k < 1; k++) vectors[i][j].add(new Integer(-1));
				vectors[i][j].add(new Integer(-1)); // Add extra -1 at end. Important for checking patterns below!!!

				if (vectors_row[i][j].size() == 1)		for (int k = 0; k < 3; k++) vectors_row[i][j].add(new Integer(-1));
				else if (vectors_row[i][j].size() == 2)	for (int k = 0; k < 2; k++) vectors_row[i][j].add(new Integer(-1));
				else if (vectors_row[i][j].size() == 3)	for (int k = 0; k < 1; k++) vectors_row[i][j].add(new Integer(-1));
				vectors_row[i][j].add(new Integer(-1)); // Add extra -1 at end. Important for checking patterns below!!!

				if (vectors_col[i][j].size() == 1)		for (int k = 0; k < 3; k++) vectors_col[i][j].add(new Integer(-1));
				else if (vectors_col[i][j].size() == 2)	for (int k = 0; k < 2; k++) vectors_col[i][j].add(new Integer(-1));
				else if (vectors_col[i][j].size() == 3)	for (int k = 0; k < 1; k++) vectors_col[i][j].add(new Integer(-1));
				vectors_col[i][j].add(new Integer(-1)); // Add extra -1 at end. Important for checking patterns below!!!

//System.out.println("\tvector[" + i + "][" + j + "] = " + vectors[i][j]);

				// For this player.
				for (int k = 0; k < vectors[i][j].size() - 3; k++)
				{
					int[] v = new int[4];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 1 && v[3] != 1)
					{
						xx1++; num_pieces_found++;

						if (v[0] == 0)
						{
							threat_level_charlie += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 0)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 0)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_charlie -= 0.1f;
						}
						if (v[3] == 0)
						{
							threat_level_charlie += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 3)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 3)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_charlie -= 0.1f;
						}

						k += 2;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 4; k++)
				{
					int[] v = new int[5];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 1 && v[3] == 1 && v[4] != 1)
					{
						xxx1++; num_pieces_found++;

						if (v[0] == 0)
						{
							num_threat_points1++;

							threat_level_bravo += (float)num_rows / 2.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 0)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 0)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_bravo -= 0.5f;
						}
						if (v[4] == 0)
						{
							num_threat_points1++;

							threat_level_bravo += (float)num_rows / 2.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 4)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 4)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_bravo -= 0.5f;
						}

						k += 3;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 4; k++)
				{
					int[] v = new int[5];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 0 && v[3] == 1 && v[4] != 1)
					{
						x_x1++; num_pieces_found++;

						if (v[2] == 0)
						{
							threat_level_charlie += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 2)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 2)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_charlie -= 0.1f;
						}

						k += 1;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 0 && v[3] == 0 && v[4] == 1 && v[5] != 1)
					{
						x__x1++; num_pieces_found++;

						if (v[2] == 0)
						{
							threat_level_charlie += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 2)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 2)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_charlie -= 0.1f;
						}
						if (v[3] == 0)
						{
							threat_level_charlie += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 3)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 3)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_charlie -= 0.1f;
						}

						k += 1;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 1 && v[3] == 0 && v[4] == 1 && v[5] != 1)
					{
						xx_x1++; num_pieces_found++; num_threat_points1++;

						threat_level_bravo += (float)num_rows / 2.0f;
						int u = ((Integer)vectors_col[i][j].get(k + 3)).intValue();
						for (int z = ((Integer)vectors_row[i][j].get(k + 3)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_bravo -= 0.5f;

						k += 4;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 0 && v[3] == 1 && v[4] == 1 && v[5] != 1)
					{
						x_xx1++; num_pieces_found++; num_threat_points1++;

						threat_level_bravo += (float)num_rows / 2.0f;
						int u = ((Integer)vectors_col[i][j].get(k + 2)).intValue();
						for (int z = ((Integer)vectors_row[i][j].get(k + 2)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_bravo -= 0.5f;

						k += 4;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 1 && v[1] == 1 && v[2] == 1 && v[3] == 1 && v[4] == 1 && v[5] != 1)
					{ xxxx1++; num_pieces_found++; k += 4; num_threat_points1++; threat_level_bravo += (double)num_rows; }
				}

				// For the other player now.
				for (int k = 0; k < vectors[i][j].size() - 3; k++)
				{
					int[] v = new int[4];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 2 && v[3] != 2)
					{
						xx2++; num_pieces_found++;

						if (v[0] == 0)
						{
							threat_level_zulu += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 0)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 0)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_zulu -= 0.1f;
						}
						if (v[3] == 0)
						{
							threat_level_zulu += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 3)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 3)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_zulu -= 0.1f;
						}

						k += 2;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 4; k++)
				{
					int[] v = new int[5];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 2 && v[3] == 2 && v[4] != 2)
					{
						xxx2++; num_pieces_found++;

						if (v[0] == 0)
						{
							num_threat_points2++;

							threat_level_tango += (float)num_rows / 2.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 0)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 0)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_tango -= 0.5f;
						}
						if (v[4] == 0)
						{
							num_threat_points2++;

							threat_level_tango += (float)num_rows / 2.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 4)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 4)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_tango -= 0.5f;
						}

						k += 3;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 4; k++)
				{
					int[] v = new int[5];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 0 && v[3] == 2 && v[4] != 2)
					{
						x_x2++; num_pieces_found++;

						if (v[2] == 0)
						{
							threat_level_zulu += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 2)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 2)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_zulu -= 0.1f;
						}

						k += 1;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 0 && v[3] == 0 && v[4] == 2 && v[5] != 2)
					{
						x__x2++; num_pieces_found++;

						if (v[2] == 0)
						{
							threat_level_zulu += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 2)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 2)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_zulu -= 0.1f;
						}
						if (v[3] == 0)
						{
							threat_level_zulu += (float)num_rows / 10.0f;
							int u = ((Integer)vectors_col[i][j].get(k + 3)).intValue();
							for (int z = ((Integer)vectors_row[i][j].get(k + 3)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_zulu -= 0.1f;
						}

						k += 1;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 2 && v[3] == 0 && v[4] == 2 && v[5] != 2)
					{
						xx_x2++; num_pieces_found++; num_threat_points2++;

						threat_level_tango += (float)num_rows / 2.0f;
						int u = ((Integer)vectors_col[i][j].get(k + 3)).intValue();
						for (int z = ((Integer)vectors_row[i][j].get(k + 3)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_tango -= 0.5f;

						k += 4;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 0 && v[3] == 2 && v[4] == 2 && v[5] != 2)
					{
					 	x_xx2++; num_pieces_found++; num_threat_points2++;

						threat_level_tango += (float)num_rows / 2.0f;
						int u = ((Integer)vectors_col[i][j].get(k + 2)).intValue();
						for (int z = ((Integer)vectors_row[i][j].get(k + 2)).intValue(); z >= 0; z--) if (grid[z][u] == 0) threat_level_tango -= 0.5f;

						k += 4;
					}
				}
				for (int k = 0; k < vectors[i][j].size() - 5; k++)
				{
					int[] v = new int[6];
					v[0] = ((Integer)vectors[i][j].get(k)).intValue(); v[1] = ((Integer)vectors[i][j].get(k + 1)).intValue();
					v[2] = ((Integer)vectors[i][j].get(k + 2)).intValue(); v[3] = ((Integer)vectors[i][j].get(k + 3)).intValue();
					v[4] = ((Integer)vectors[i][j].get(k + 4)).intValue(); v[5] = ((Integer)vectors[i][j].get(k + 5)).intValue();
					if (v[0] != 2 && v[1] == 2 && v[2] == 2 && v[3] == 2 && v[4] == 2 && v[5] != 2)
					{ xxxx2++; num_pieces_found++; k += 4; num_threat_points2++; threat_level_tango += (double)num_rows; }
				}
			}

			// Based on the type of group it is, add to a different pool of pieces count.
			if (i == 0) num_row_pieces = num_pieces_found;
			else if (i == 1) num_col_pieces = num_pieces_found;
			else if (i == 2) num_diag_up_pieces = num_pieces_found;
			else if (i == 3) num_diag_down_pieces = num_pieces_found;
		}

		// Set all the results.
		result[0] = (double)xx1;		result[1] = (double)x_x1;		result[2] = (double)x__x1;
		result[3] = (double)xxx1;		result[4] = (double)xx_x1;		result[5] = (double)x_xx1;
		result[6] = (double)xxxx1;

		result[7] = (double)xx2;		result[8] = (double)x_x2;		result[9] = (double)x__x2;
		result[10] = (double)xxx2;		result[11] = (double)xx_x2;		result[12] = (double)x_xx2;
		result[13] = (double)xxxx2;

		result[14] = (double)num_row_pieces;							result[15] = (double)num_col_pieces;
		result[16] = (double)num_diag_up_pieces;						result[17] = (double)num_diag_down_pieces;

		result[18] = (double)num_threat_points1;						result[19] = (double)num_threat_points2;
		result[20] = threat_level_bravo;								result[21] = threat_level_tango;
		//result[22] = threat_level_charlie;								result[23] = threat_level_zulu;

		// Return the result.
		return result;
	}
}


import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

// This class finds "features" (e.g. groups) by performing a modified (and somewhat simplified) version of connected-components.
public class FeatureFinder
{
	// This is the grid where features will be found.
	private int[][] grid = null;
	private int num_rows = 0;
	private int num_cols = 0;

	// The features found.
	private ArrayList<Feature> features = null;

	// The constructor.
	public FeatureFinder(int[][] new_grid, int new_num_rows, int new_num_cols)
	{
		features = new ArrayList<Feature>();
		num_rows = new_num_rows;
		num_cols = new_num_cols;
		grid = new int[num_rows][num_cols];
		for (int r = 0; r < num_rows; r++) for (int c = 0; c < num_cols; c++) grid[r][c] = new_grid[r][c];
	}

	// Find the features in this grid.
	public ArrayList<Feature> FindFeatures()
	{
		// Clear the current array list.
		features.clear();

		// For each point on the grid, begin creating features.
		for (int r = 0; r < num_rows; r++)
		{
			for (int c = 0; c < num_cols; c++)
			{
				// If the grid location doesn't contain a player, skip.
				if (grid[r][c] == 0) continue;

				// Loop over each theta value for this cell.
				for (int theta = 0; theta < 360; theta += 45)
				{
					// Create a potential feature.
					Feature potential_feature = new Feature(r, c, theta, 0, grid[r][c]);
					Point pf_point_end = potential_feature.getEnd();
					Point pf_point_prev = potential_feature.getPrevious();
					Point pf_point_next = potential_feature.getNext();

					// Ensure it does not go out of bounds.
					if (pf_point_prev.r < 0) pf_point_prev.r = 0;
					if (pf_point_prev.c < 0) pf_point_prev.c = 0;
					if (pf_point_prev.r >= num_rows) pf_point_prev.r = num_rows - 1;
					if (pf_point_prev.c >= num_cols) pf_point_prev.c = num_cols - 1;
					if (pf_point_next.r < 0) pf_point_next.r = 0;
					if (pf_point_next.c < 0) pf_point_next.c = 0;
					if (pf_point_next.r >= num_rows) pf_point_next.r = num_rows - 1;
					if (pf_point_next.c >= num_cols) pf_point_next.c = num_cols - 1;

					// If this feature is not even on a boundry, don't even bother.
					//if (grid[r][c] == grid[pf_point_prev.r][pf_point_prev.c]) continue;

					// Increase the length of the feature until it reaches a cell not of the same type.
					for (int length = 0; grid[r][c] == grid[pf_point_next.r][pf_point_next.c]; length++)
					{
						// Increment the length and get the new end and next points.
						potential_feature.length = length;
						pf_point_end = potential_feature.getEnd();
						pf_point_next = potential_feature.getNext();

						// Ensure it does not go out of bounds. If it does, break out.
						if (pf_point_end.r < 0 || pf_point_end.r >= num_rows || pf_point_end.c < 0 || pf_point_end.c >= num_cols)
						{
							potential_feature.length--;
							break;
						}

						// It is alright however if the next spot is out of bounds.
						if (pf_point_next.r < 0) pf_point_next.r = 0;
						if (pf_point_next.c < 0) pf_point_next.c = 0;
						if (pf_point_next.r >= num_rows) pf_point_next.r = num_rows - 1;
						if (pf_point_next.c >= num_cols) pf_point_next.c = num_cols - 1;
					}

					// Add this feature to the list.
					features.add(potential_feature);
				}
			}
		}

/*
System.out.println("Starting cleanup cycles! Initial size is " + features.size() + ".");
		// Find the features whereby no immediate action can be taken to strengthen it.
		for (int i = 0; i < features.size(); i++)
		{
			// Some local variables.
			Point prev = features.get(i).getPrevious();
			Point next = features.get(i).getNext();

			// If both the previous and next cells do not have a token beneath them, remove it from the list.
			boolean prev_not_important = false;
			boolean next_not_important = false;
			if (prev.r < 0 || prev.c < 0 || prev.r >= num_rows || prev.c >= num_cols) prev_not_important = true;
			if (!prev_not_important && prev.c + 1 < num_cols && grid[prev.r][prev.c + 1] == 0) prev_not_important = true;
			if (next.r < 0 || next.c < 0 || next.r >= num_rows || next.c >= num_cols) next_not_important = true;
			if (!next_not_important && next.c + 1 < num_cols && grid[next.r][next.c + 1] == 0) next_not_important = true;

			// Remove if both are true.
			if (prev_not_important && next_not_important) features.remove(i);
		}
System.out.println("Done! Now size is " + features.size() + ". Next step, remove duplicates!");

		// Find the features that are duplicates.
		for (int i = 0; i < features.size(); i++)
		{
			for (int j = 0; j < features.size(); j++)
			{
				// Skip the same comparison.
				if (i == j) continue;

				// If the start and end are the same, remove this jth one.
				if (features.get(i).start.isEqualTo(features.get(j).start) && features.get(i).getEnd().isEqualTo(features.get(j).getEnd()))
				{
					features.remove(j);
					j--;
				}
			}
		}
System.out.println("Complete! Now size is " + features.size() + ".");
*/

		// Now that the array list is created, sort it.
		Collections.sort(features);

		// Return the array list.
		return features;
	}
}


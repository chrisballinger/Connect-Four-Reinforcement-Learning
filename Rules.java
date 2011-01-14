import java.io.*;

public class Rules implements Serializable {
    public int numRows;
    public int numCols;
    public int numConnect;

    public Rules(int num_rows, int num_cols, int num_con) {
	numRows=num_rows;
	numCols=num_cols;
	numConnect=num_con;
    }
    public Rules(Rules r) {
	numRows=r.numRows;
	numCols=r.numCols;
	numConnect=r.numConnect;
    }
}

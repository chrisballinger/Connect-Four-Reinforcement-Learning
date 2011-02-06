import java.lang.Comparable;
import java.lang.NullPointerException;

// A class to contain a feature.
public class Feature implements Comparable
{
	// The variables associated with a feature.
	public Point start;	// The starting point.
	public int theta;	// Theta value (in degrees).
	public int length;	// The length (in cells).
	public int type;	// The type of feature (player 1 or player 2).

	// The constructor.
	public Feature()
	{
		start = new Point();
		theta = -1;
		length = -1;
		type = -1;
	}

	// A constructor that allows specification of all these.
	public Feature(int r, int c, int theta, int length, int type)
	{
		start = new Point(r, c);
		this.theta = theta;
		this.length = length;
		this.type = type;
	}

	// Get the starting point.
	public Point getStart()
	{
		return start;
	}

	// Get the ending point.
	public Point getEnd()
	{
		// Declare local variables.
		Point end = new Point();
		double hypo = 1.0f;

		// Find the hypotenuse of the "triangle."
		if ((theta / 45) % 2 == 1) hypo = Math.sqrt(2);

		// Compute end point.
		end.r = start.r - Math.max(0, (length - 1)) * (int)Math.round(hypo * Math.sin((theta) * Math.PI / 180));
		end.c = start.c + Math.max(0, (length - 1)) * (int)Math.round(hypo * Math.cos((theta) * Math.PI / 180));

		// Return.
		return end; 
	}

	// Get the previous point (i.e. following the length to -1).
	public Point getPrevious()
	{
		// Declare local variables.
		Point prev = new Point();
		double hypo = 1.0f;

		// Find the hypotenuse of the "triangle."
		if ((theta / 45) % 2 == 1) hypo = Math.sqrt(2);

		// Compute previous point.
		prev.r = start.r - (int)Math.round(hypo * Math.sin((theta + 180) * Math.PI / 180));
		prev.c = start.c + (int)Math.round(hypo * Math.cos((theta + 180) * Math.PI / 180));

		// Return.
		return prev;
	}

	// Get the next point (i.e. following the length to len+1).
	public Point getNext()
	{
		// Declare local variables.
		Point next = new Point();
		double hypo = 1.0f;

		// Find the hypotenuse of the "triangle."
		if ((theta / 45) % 2 == 1) hypo = Math.sqrt(2);

		// Compute next point.
		next.r = start.r - length * (int)Math.round(hypo * Math.sin((theta) * Math.PI / 180));
		next.c = start.c + length * (int)Math.round(hypo * Math.cos((theta) * Math.PI / 180));

		// Return.
		return next;
	}

	// Check equality with another object.
	public boolean equals(Object o)
	{
		//if (type == ((Feature)o).type && length == ((Feature)o).length) return true;
		if (length == ((Feature)o).length) return true;
		else return false;
	}

	// Compare this to another object.
	public int compareTo(Object o)
	{
		//if (o == null) throw java.lang.NullPointerException;
		if (length == ((Feature)o).length) return 0;
		else if (length > ((Feature)o).length) return -1;
		else if (length < ((Feature)o).length) return 1;
		//if (type == ((Feature)o).type && length == ((Feature)o).length) return 0;
		//else if (type > ((Feature)o).type || (type == ((Feature)o).type && length > ((Feature)o).length)) return -1;
		//else if (type < ((Feature)o).type || (type == ((Feature)o).type && length < ((Feature)o).length)) return 1;
		return 1;
	}
}


public class Group
{
	public Point start;
	public int theta;	// in degrees
	public int length;
	
	public Group()
	{
		start = new Point();
		theta = -1;
		length = -1;
	}
	
	public Group(int r, int c, int theta, int length)
	{
		start = new Point(r, c);
		this.theta = theta;
		this.length = length;
	}
	
	public Point getStart()
	{
		return start;
	}
	
	public Point getEnd()
	{
		Point prev = new Point();
		double hypo = 1.0f;
		if ((theta / 45) % 2 == 1) hypo = Math.sqrt(2);
		prev.r = start.r - (length-1)*(int)Math.round(hypo*Math.sin((theta)*Math.PI/180));
		prev.c = start.c + (length-1)*(int)Math.round(hypo*Math.cos((theta)*Math.PI/180));
		return prev; 
	}
	
	public Point getPrevious()
	{
		Point prev = new Point();
		double hypo = 1.0f;
		if ((theta / 45) % 2 == 1) hypo = Math.sqrt(2);
		prev.r = start.r - (int)Math.round(hypo*Math.sin((theta+180)*Math.PI/180));
		prev.c = start.c + (int)Math.round(hypo*Math.cos((theta+180)*Math.PI/180));
		return prev;
	}
	
	public Point getNext()
	{
		Point prev = new Point();
		double hypo = 1.0f;
		if ((theta / 45) % 2 == 1) hypo = Math.sqrt(2);
		prev.r = start.r - length*(int)Math.round(hypo*Math.sin((theta)*Math.PI/180));
		prev.c = start.c + length*(int)Math.round(hypo*Math.cos((theta)*Math.PI/180));
		return prev;
	}

}


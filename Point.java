public class Point
{
	public int r;
	public int c;
	
	public Point()
	{
		r = -1;
		c = -1;
	}
	
	public Point(int r, int c)
	{
		this.r = r;
		this.c = c;
	}
	
	public String toString()
	{
		return "{"+r+","+c+"}";
	}

	public boolean isEqualTo(Point p)
	{
		if (r == p.r && c == p.c) return true;
		else return false;
	}
}

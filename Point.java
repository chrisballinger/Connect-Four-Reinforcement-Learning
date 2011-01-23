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
}
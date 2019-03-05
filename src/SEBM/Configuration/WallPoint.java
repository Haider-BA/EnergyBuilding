package SEBM.Configuration;

public class WallPoint 
{
	private long mX;
	private long mY;
	
	public WallPoint()
	{
		mX = 0;
		mY = 0;
	}

	public WallPoint(long paramX, long paramY)
	{
		mX = paramX;
		mY = paramY;
	}
	
	public void X(long paramX) { mX = paramX; }
	public long  X() { return mX; }
	
	public void Y(long paramY) { mY = paramY; }
	public long  Y() { return mY; }
	
	public String toString()
	{
		String tString = "";
		
		tString += "(" + mX + ", " + mY + ")";
		
		return tString;
	}
}

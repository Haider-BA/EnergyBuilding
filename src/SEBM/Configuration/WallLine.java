package SEBM.Configuration;

public class WallLine 
{
	private WallPoint mBegin;
	private WallPoint mEnd;
	
	public WallLine()
	{
		mBegin = new WallPoint();
		mEnd   = new WallPoint();
	}
	
	public WallLine(WallPoint paramB, WallPoint paramE)
	{
		mBegin = paramB;
		mEnd = paramE;
	}
	
	public WallLine(long paramX1, long paramY1, long paramX2, long paramY2)
	{
		mBegin = new WallPoint(paramX1, paramY1);
		mEnd   = new WallPoint(paramX2, paramY2);
	}
	
	public void Begin(long paramX, long paramY) { mBegin.X(paramX); mBegin.Y(paramY); }
	public WallPoint Begin() { return mBegin; }
	
	public void End(long paramX, long paramY) { mEnd.X(paramX); mEnd.Y(paramY); }
	public WallPoint End() { return mEnd; }
	
	public boolean isWall(long paramX, long paramY)
	{
		if (mBegin.X() == mEnd.X())	// Horizontal Wall
		{
			if ( (paramX == mBegin.X()) & 
				 ( (paramY >= mBegin.Y()) & (paramY <= mEnd.Y()) )
			   )
				 return true;
		}
		else if (mBegin.Y() == mEnd.Y())	// Vertical Wall
		{
			if ( (paramY == mBegin.Y()) & 
					 ( (paramX >= mBegin.X()) & (paramX <= mEnd.X()) )
			   )
					 return true;
		}
		else
		{
			return false;
		}
		
		return false;
	}
	
	public String toJSONString()
	{
		String tString = "";
		
		return tString;
	}
	
	public String toString()
	{
		String tString = "";
		
		tString += "[" + mBegin.toString() + "-" + mEnd.toString() + "]";
		
		return tString;
	}
}

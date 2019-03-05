package SEBM.Configuration;

public class RoomSpace 
{
	private int mX, mY;
	private int mW, mH;
	private int mVolume;
	
	public RoomSpace(int paramX, int paramY, int paramW, int paramH, int paramV)
	{
		mX = paramX;
		mY = paramY;
		mW = paramW;
		mH = paramH;
		mVolume = paramV;
	}
	
	public boolean isInside(int paramX, int paramY)
	{
//		if ( ( (mX + paramX) < (mX + mW) ) && ( (mY + paramY) < (mY + mH) ) )
		if ( (paramX >= mX) && ( paramX < (mX + mW) ) && (paramY >= mY) && ( paramY < (mY + mH) ) )
			return true;
		
		return false;
	}
	
	public int Volume()
	{
		return mVolume;
	}
	
	public String toString()
	{
		String strTemp = "";
		
		strTemp += "Space: < ";
		strTemp += "(" + mX + ", " + mY + "), ";
		strTemp += "(" + mW + ", " + mH + "), ";
		strTemp += "(" + mVolume + ") ";
		strTemp += ">";
		
		return strTemp;
	}
}

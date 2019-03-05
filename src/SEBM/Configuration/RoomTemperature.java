package SEBM.Configuration;

public class RoomTemperature 
{
	private double mInception;
	private double mTarget;
	private double mCurrent;

	public RoomTemperature(double paramI, double paramT)
	{
		mInception = paramI;
		mTarget = paramT;
		mCurrent = paramI;
	}
	
	public void resetTemperature()
	{
		mCurrent = mInception;
	}
	
	public void updateTemperature(double paramUpdate)
	{
		mCurrent += (1 / paramUpdate);
	}
	
	public void updateTemperature(double paramV, double paramP)
	{
		mCurrent += (paramP / paramV);
	}
	
	public double Current()
	{
		return mCurrent;
	}
	
	public double Target()
	{
		return mTarget;
	}
	
	public boolean isHigh()
	{
		if (mCurrent >= mTarget) return true;
		
		return false;
	}
	
	public boolean isLow(int paramDelta)
	{
		if ( (mCurrent + paramDelta) < mTarget) return true;
		
		return false;
	}
	
	public String toString()
	{
		String strTemp = "";
		
		strTemp += "Temperature: <" + mTarget + ", " + mCurrent + ">";
		
		return strTemp;
	}
}

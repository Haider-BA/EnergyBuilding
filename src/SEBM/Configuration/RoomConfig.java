package SEBM.Configuration;

import java.util.ArrayList;

public class RoomConfig 
{
	private int mNumber;
	private int mDelta;
	
	public ArrayList<Long> mOffTimes;
	public double		   mTimeDiff;

	public RoomTemperature mTemperature;
	public RoomSpace       mSpace;
	public RoomHVAC		   mHVAC;
	
	public RoomConfig()
	{
		mNumber = -1;
		mDelta = -1;
		mTemperature = new RoomTemperature(0, 0);
		mSpace       = new RoomSpace(-1, -1, 0, 0, 0);
		mHVAC        = new RoomHVAC(true, -1, -1, -1, -1);
	}
	
	public RoomConfig(int paramNumber, int paramDelta, RoomTemperature paramTemperature, RoomSpace paramSpace, RoomHVAC paramHVAC)
	{
		mOffTimes = new ArrayList<Long>();
		
		mNumber = paramNumber;
		mDelta = paramDelta;
		mTemperature = paramTemperature;
		mSpace = paramSpace;
		mHVAC  = paramHVAC;
	}
	
	public void resetConfig()
	{
		mTemperature.resetTemperature();
		mOffTimes.clear();
		mOffTimes = new ArrayList<Long>();
		mHVAC.Pressure = 0.0;
	}
	
	public void addTime(long paramTime)
	{
		mOffTimes.add(paramTime);
	}
	
	public void calcTimeDiff()
	{
		int  i, nSize;
		long nDiff = 0;
		
		nSize = mOffTimes.size();
		
		if (nSize < 2)
		{
			mTimeDiff = 1;
			return;
		}
		
		for (i = 0; i < nSize - 1; i++)
			nDiff += mOffTimes.get(i + 1).longValue() - mOffTimes.get(i).longValue();
		
		mTimeDiff = (nDiff / (nSize - 1));
	}
	
	public void Number(int paramNumber) { mNumber = paramNumber; }
	public int  Number() { return mNumber; }
		
	public void Delta(int paramDelta) { mDelta = paramDelta; }
	public int  Delta() { return mDelta; }

	public void initPressure(double paramV)
	{
		if (mHVAC.PressureSave != 0)
			mHVAC.Pressure     = mHVAC.PressureSave;
		else
		{
			mHVAC.PressureSave = (double)mSpace.Volume() / paramV;
			mHVAC.Pressure     = mHVAC.PressureSave;
		}
	}
	
	public double getDeltaPressure()
	{
		return (mHVAC.Pressure - mHVAC.PressureSave);
	}
	
	public void distributePressure(double paramV, double paramP)
	{
		mHVAC.Pressure = mHVAC.PressureSave + ((double)mSpace.Volume() / paramV) * paramP;
	}
	
	public void updatePressure(int paramTotalVolume)
	{
		if (mHVAC.isOn())
			mHVAC.Pressure = (double)mSpace.Volume() / (double)paramTotalVolume;
		else
			mHVAC.Pressure = 0.0;
	}
	
	public void updateTemperature()
	{
		if (mHVAC.isOn())
			mTemperature.updateTemperature( 1 * mSpace.Volume(), mHVAC.Pressure );
		else
			mTemperature.updateTemperature( -1 * mSpace.Volume(), 1);
	}

	public boolean isHVAC(int paramX, int paramY)
	{
		if ( (paramX == mHVAC.X) && (paramY == mHVAC.Y)) return true;
		
		return false;
	}
	
	public boolean isHeat()
	{
		return mTemperature.isHigh();
	}
	
	public boolean isCool()
	{
		return mTemperature.isLow(mDelta);
	}
	
	public String toTimeLine()
	{
		int i;
		int nSize = mOffTimes.size();
		String strTemp;
			
		if (nSize == 0)
			strTemp = "{ }";
		else
		{
			strTemp = "{ ";
		
			for (i = 0; i < nSize; i++)
				strTemp += "( " + mOffTimes.get(i).longValue() + " )";
		
			strTemp += " }";
		}
		
		return strTemp;
	}
	
	public String toTimeLineString()
	{
		int i;
		int nSize = mOffTimes.size();
		String strTemp;
			
		if (nSize == 0)
			strTemp = "empty \n";
		else
		{
			strTemp = "Room" + mNumber;
		
			for (i = 0; i < nSize; i++)
				strTemp += ", " + mOffTimes.get(i).longValue();
		
			strTemp += "\n";
		}
		
		return strTemp;
	}
	
	public String toLog()
	{
		String tString = "";
		
		tString += "Room " + mNumber + ": { ";
		tString += mTemperature.toString() + " -- ";
		tString += "Pressure: " + mHVAC.Pressure + " -- ";
		tString += "Volume: " + mSpace.Volume();
		tString += " }";
		
		return tString;
	}
	
	public String toString()
	{
		String tString = "{";
		
		tString += " NUMBER: " + mNumber;
		tString += ", DELTA: " + mDelta + ",";
		tString += mTemperature.toString();
		tString += mSpace.toString();
		tString += mHVAC.toString();
		
		tString += " }";
		
		return tString;
	}
	
}

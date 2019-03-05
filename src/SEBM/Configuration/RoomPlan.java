package SEBM.Configuration;

import java.util.ArrayList;

import SEBM.SEBMMeasureElement;

public class RoomPlan 
{
	private ArrayList<RoomConfig> mRooms;
	public  ArrayList<Double>     mTimeDiffs;
	private int                   mVolume;
	private long				  mTime;
	
	private boolean               mDistribute; // TRUE: redistribute, FALSE: no distribute 
	

	public RoomPlan()
	{
		mRooms = null;
		mTimeDiffs = new ArrayList<Double>();
		mVolume = 0;
		mDistribute = false;	// base algorithm
		mTime = 0;
	}
	
	public void initPlan(int paramSize)
	{
		mRooms = new ArrayList<RoomConfig>(paramSize);
	}
	
	public void Distribute(boolean paramDis)
	{
		mDistribute = paramDis;
	}
	
	public void resetPlan()
	{
		int nSize = mRooms.size();
		
		mTime = 0;
		mTimeDiffs.clear();
				
		for (int i = 0; i < nSize; i++)
		{
			mRooms.get(i).resetConfig();
		}
	}
	
	
	public void calcTimeDiffs()
	{
		int nSize = mRooms.size();
		
		for (int i = 0; i < nSize; i++)
		{
			mRooms.get(i).calcTimeDiff();
			mTimeDiffs.add(mRooms.get(i).mTimeDiff);
		}
	}
	
	public void updateRoomTemperatures()
	{
		int nSize = mRooms.size();
		
		mTime++;
		
		// Update room Temperature & Control room HVAC 
		for (int i = 0; i < nSize; i++)
		{
			mRooms.get(i).updateTemperature();
			
			if (mRooms.get(i).isHeat() && (mRooms.get(i).mHVAC.isOn() == true))
			{
//				System.out.println("HVAC will be OFF");
				mRooms.get(i).mHVAC.Turn(false);
				mRooms.get(i).addTime(mTime);
				updateRoomPressures(i, false);
			}
			
			if (mRooms.get(i).isCool() && (mRooms.get(i).mHVAC.isOn() == false))
			{
//				System.out.println("HVAC will be ON");
				mRooms.get(i).mHVAC.Turn(true);
				updateRoomPressures(i, true);
			}
		}
	}
	
	public void printTimeLines()
	{
		int nSize = mRooms.size();

		for (int i = 0; i < nSize; i++)
			System.out.println("Room " + (i + 1) + " --> " + mRooms.get(i).toTimeLine());
	}
	
	public String toTimeLines()
	{
		int nSize = mRooms.size();
		String strTemp = "";

		for (int i = 0; i < nSize; i++)
			strTemp += mRooms.get(i).toTimeLineString();
		
		return strTemp;
	}
	
	public void initRoomPressures()
	{
		int nSize = mRooms.size();
		
		for (int i = 0; i < nSize; i++)
		{
			mRooms.get(i).initPressure(mVolume);
		}
	}
	
	public void applyPressures(SEBMMeasureElement paramE)
	{
		int nSize = mRooms.size();
		
		for (int i = 0; i < nSize; i++)
		{
			mRooms.get(i).mHVAC.PressureSave = paramE.getWeight(i);
		}
	}
	
	public double getDeltaPressures()
	{
		int nSize = mRooms.size();
		double tDelta = 0;
	
		for (int i = 0; i < nSize; i++)
		{
			if (mRooms.get(i).getDeltaPressure() > 0)
				tDelta += mRooms.get(i).getDeltaPressure();
		}
		
		return tDelta;
	}
	
	public void updateRoomPressures(int paramIdx, boolean paramOn)
	{
		int nSize = mRooms.size();
		int tVolume = 0;
		double tPressure = 0;
//		double tDelta = 0;
		
		if (mDistribute == false)
		{
			if (paramOn == true)	// if HVAC will be on
				mRooms.get(paramIdx).mHVAC.Pressure = mRooms.get(paramIdx).mHVAC.PressureSave;
			else					// HVAC will be off
				mRooms.get(paramIdx).mHVAC.Pressure = 0;
		}
		else
		{
			tVolume = UsedVolume();
		
			// if HVACs of all rooms are off.
			if (tVolume == 0) return;

			
			if (paramOn == true)
			{
				// when HVAC is on, totally redistribute pressure
				// "Used Pressure" means sum of initial pressures which are on
				tPressure = 1 - usedPressure();
			}
			else
			{
				// when HVAC is off, redistribute pressure of HVAC which is turned off
				tPressure = mRooms.get(paramIdx).mHVAC.Pressure;
				mRooms.get(paramIdx).mHVAC.Pressure = 0;
				
			}

			for (int i = 0; i < nSize; i++)
			{
				if (mRooms.get(i).mHVAC.isOn())
					mRooms.get(i).distributePressure(tVolume, tPressure);
			}
		}
	}
	
	public void updateRoomPlan()
	{
		updateRoomTemperatures();
//		updateRoomPressures();
	}
	
	public double calcCurrentAvg()
	{
		int nSize = mRooms.size();
		double nSum = 0.0;
		
		for (int i = 0; i < nSize; i++)
			nSum += mRooms.get(i).mTemperature.Current();
		
		return (nSum / nSize);
	}
	
	public double calcTargetAvg()
	{
		int nSize = mRooms.size();
		double nSum = 0.0;
		
		for (int i = 0; i < nSize; i++)
			nSum += mRooms.get(i).mTemperature.Target();
		
		return (nSum / nSize);
	}
	
	public double usedPressure()
	{
		int nSize = mRooms.size();
		double tPressure = 0;

		for (int i = 0; i < nSize; i++)
		{
			if (mRooms.get(i).mHVAC.isOn())
				tPressure += mRooms.get(i).mHVAC.PressureSave;
		}
		
		return tPressure;
	}
	
	public int Volume() { return mVolume; }
	public int UsedVolume()
	{
		int tVolume = 0;
		int nSize = mRooms.size();
		
		for (int i = 0; i < nSize; i++)
		{
			if (mRooms.get(i).mHVAC.isOn())
				tVolume += mRooms.get(i).mSpace.Volume();
		}
		
		return tVolume;
	}
	
	public int DynamicVolume()
	{
		return 0;
	}
	
	public void addRoom(RoomConfig paramRoom)
	{
		mRooms.add(paramRoom);
		mVolume += paramRoom.mSpace.Volume();
	}
	
	public RoomConfig getRoom(int paramIdx)
	{
		return mRooms.get(paramIdx);
	}
	
	public RoomConfig findRoom(int paramRoomNo)
	{
		int nSize = mRooms.size();
		
		for (int i = 0; i < nSize; i++)
		{
			if (mRooms.get(i).Number() == paramRoomNo) return mRooms.get(i);
		}
		
		return null;
	}
	
	public int Size()
	{
		return mRooms.size();
	}
	
	public String toLog(int paramIdx)
	{
		return mRooms.get(paramIdx).toLog();
	}
	
	public void printPlan()
	{
		int nSize = mRooms.size();
		
		for (int i = 0; i < nSize; i++)
		{
			System.out.println(mRooms.get(i).toString());
		}
	}
}

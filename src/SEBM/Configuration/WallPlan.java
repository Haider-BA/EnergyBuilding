package SEBM.Configuration;

import java.util.ArrayList;

public class WallPlan 
{
	private ArrayList<WallLine> mWalls;

	public WallPlan()
	{
		mWalls = null;
	}
	
	public void initPlan(int paramSize)
	{
		mWalls = new ArrayList<WallLine>(paramSize);		
	}
	
	public void addWall(WallLine paramLine)
	{
		mWalls.add(paramLine);
	}
	
	public void addWall(WallPoint paramStart, WallPoint paramEnd)
	{
		WallLine tWallLine = new WallLine(paramStart, paramEnd);
		mWalls.add(tWallLine);
	}
	
	public WallLine getWall(int paramIdx)
	{
		return mWalls.get(paramIdx);
	}
	
	public int size()
	{
		return mWalls.size();
	}
	
	public void printPlan()
	{
		int nSize = mWalls.size();
		
		for (int i = 0; i < nSize; i++)
		{
			System.out.println(mWalls.get(i).toString());
		}
	}
}

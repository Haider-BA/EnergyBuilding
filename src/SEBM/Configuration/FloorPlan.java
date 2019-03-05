package SEBM.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.simple.parser.JSONParser;

import SEBM.SEBMMeasureElement;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class FloorPlan 
{
	private String  mFloorName;
	private long    mFloorWidth;
	private long    mFloorHeight;
	private long    mCellUnitSize;
	private long	mPadSize;
	public  long    mFrameMax;
	
	private WallPlan mFloorWalls;
	private RoomPlan mFloorRooms;

	public FloorPlan()
	{
		mFloorName = "Unknown";
		mFloorWidth = 0;
		mFloorHeight = 0;
		mCellUnitSize = 0;
		mPadSize = 0;
		
		mFloorWalls = new WallPlan();
		mFloorRooms = new RoomPlan();
	}
	
	public void readFloorPlan()
	{
		mFloorWalls.initPlan(10);
		mFloorRooms.initPlan(10);
		
		mFloorName = "EnergyBuilding";
		mFloorWidth = 64;
		mFloorHeight = 32;
		mCellUnitSize = 4;
		mPadSize = 1;
		
		WallLine tWallLine = new WallLine(0, 0, mFloorWidth + mPadSize, 0);
		mFloorWalls.addWall(tWallLine);

		tWallLine = new WallLine(mFloorWidth + mPadSize, 0, mFloorWidth + mPadSize, mFloorHeight + mPadSize);
		mFloorWalls.addWall(tWallLine);
		
		tWallLine = new WallLine(mFloorWidth + mPadSize, mFloorHeight + mPadSize, 0, mFloorHeight + mPadSize);
		mFloorWalls.addWall(tWallLine);
		
		tWallLine = new WallLine(0, mFloorHeight + mPadSize, 0, 0);
		mFloorWalls.addWall(tWallLine);
	}
	
	public void readFloorPlan(String paramFile)
	{
		int nSize, i;
		long sx, sy, ex, ey;
		long roomNo, roomDelta, hvacX, hvacY, hvacF, hvacD;
		double initTemp, targetTemp;
		long spaceX, spaceY, spaceW, spaceH;
		double roomVolume;
		
		WallLine tWallLine;
		RoomConfig tRoomConfig;
		
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		try
		{
			fileReader = new FileReader(paramFile);
			bufferedReader = new BufferedReader(fileReader);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject)jsonParser.parse(bufferedReader);
			
			mFloorName = (String)jsonObj.get("FLOOR");
			mFloorWidth = (long)jsonObj.get("WIDTH");
			mFloorHeight = (long)jsonObj.get("HEIGHT");
			mCellUnitSize = (long)jsonObj.get("UNIT");
			mPadSize = (long)jsonObj.get("PAD");
			mFrameMax = (long)jsonObj.get("DURATION");
			
			JSONArray jsonArray = (JSONArray)jsonObj.get("WALL");

			nSize = jsonArray.size();
			mFloorWalls.initPlan(nSize);
			
			for (i = 0; i < nSize; i++)
			{
				JSONObject loopObj = (JSONObject)jsonArray.get(i);
				JSONArray  loopArray = (JSONArray)loopObj.get("START");
				sx = (long)loopArray.get(0);
				sy = (long)loopArray.get(1);

				loopArray = (JSONArray)loopObj.get("END");
				ex = (long)loopArray.get(0);
				ey = (long)loopArray.get(1);
				
				tWallLine = new WallLine(sx, sy, ex, ey);
				
				mFloorWalls.addWall(tWallLine);
			}
			
			JSONArray jsonRooms = (JSONArray)jsonObj.get("ROOM");
			nSize = jsonRooms.size();
			mFloorRooms.initPlan(nSize);
			
			for (i = 0; i < nSize; i++)
			{
				JSONObject roomObj = (JSONObject)jsonRooms.get(i);
				JSONArray  hvacArray = (JSONArray)roomObj.get("HVAC");
				JSONArray  spaceArray = (JSONArray)roomObj.get("SPACE");
				
				roomNo = (long)roomObj.get("NUMBER");
				initTemp = ((Number)(roomObj.get("INITIAL"))).doubleValue();
				targetTemp = ((Number)(roomObj.get("TARGET"))).doubleValue();
				roomDelta = (long)roomObj.get("DELTA");
				roomVolume = ((Number)(roomObj.get("VOLUME"))).doubleValue();
				
				spaceX = (long)spaceArray.get(0);
				spaceY = (long)spaceArray.get(1);
				spaceW = (long)spaceArray.get(2);
				spaceH = (long)spaceArray.get(3);
				
				hvacX = (long)hvacArray.get(0);
				hvacY = (long)hvacArray.get(1);
				hvacF = (long)hvacArray.get(2);
				hvacD = (long)hvacArray.get(3);
				
				RoomTemperature tTemp  = new RoomTemperature(initTemp, targetTemp);
				RoomSpace       tSpace = new RoomSpace((int)spaceX, (int)spaceY, (int)spaceW, (int)spaceH, (int)roomVolume);
				RoomHVAC        tHVAC  = new RoomHVAC(true, (int)hvacX, (int)hvacY, (int)hvacF, (int)hvacD);
				tRoomConfig = new RoomConfig((int)roomNo, (int) roomDelta, tTemp, tSpace, tHVAC);
				mFloorRooms.addRoom(tRoomConfig);
			}
			
			mFloorRooms.initRoomPressures();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void resetFloorPlan()
	{
		mFloorRooms.resetPlan();
	}
	
	public void writeFloorPlan()
	{
		printFloor();
	}
	
	public void   Name(String paramName) { mFloorName = paramName; }
	public String Name() { return mFloorName; }
	
	public void   Width(long paramWidth) { mFloorWidth = paramWidth; }
	public long    Width() { return mFloorWidth; }
	
	public void   Height(long paramHeight) { mFloorHeight = paramHeight; }
	public long    Height() { return mFloorHeight; }
	
	public void   Unit(long paramUnit) { mCellUnitSize = paramUnit; }
	public long    Unit() { return mCellUnitSize; }
	
	public void  Pad(long paramPad) { mPadSize = paramPad; }
	public long  Pad() { return mPadSize; }
	
	public boolean isWall(int paramX, int paramY)
	{
		int i;
		
		for (i = 0; i < mFloorWalls.size(); i++)
		{
			if (mFloorWalls.getWall(i).isWall(paramX, paramY) == true)
				return true;
		}
		
		return false;
	}
	
	public int   WallCount()
	{
		return mFloorWalls.size();
	}
	
	public WallLine WallLineAt(int paramIdx)
	{
		return mFloorWalls.getWall(paramIdx);
	}
	
	public int RoomCount()
	{
		return mFloorRooms.Size();
	}
	
	public RoomPlan Rooms() { return mFloorRooms; }
	
	public void setDistribute()
	{
		mFloorRooms.Distribute(true);
	}
	
	public void initRoomPressures()
	{
		mFloorRooms.initRoomPressures();
	}
	
	public void applyNewPressures(SEBMMeasureElement paramE)
	{
		mFloorRooms.applyPressures(paramE);
	}
	
	public void updateRooms()
	{
		mFloorRooms.updateRoomPlan();
	}

	public int isRoom(int paramX, int paramY)
	{
		int i;
		
		for (i = 0; i < mFloorRooms.Size(); i++)
			if (mFloorRooms.getRoom(i).mSpace.isInside(paramX, paramY))
				return mFloorRooms.getRoom(i).Number();
		
		return -1;
	}
	
	public int isHVAC(int paramX, int paramY)
	{
		int i;
		
		for (i = 0; i < mFloorRooms.Size(); i++)
			if (mFloorRooms.getRoom(i).isHVAC(paramX, paramY)) return i;
		
		return -1;
	}
	
	public boolean isOnHVAC(int paramRoomNo)
	{
		RoomConfig tRoom = mFloorRooms.findRoom(paramRoomNo);
		
		if (tRoom == null) 	return false;
		else 				return tRoom.mHVAC.isOn();
	}

	public double temperatureAtRoom(int paramRoomNo)
	{
		RoomConfig tRoom = mFloorRooms.findRoom(paramRoomNo);

		if (tRoom == null)	return -1;
		else				return tRoom.mTemperature.Current();
	}
	
	public double pressureAtRoom(int paramRoomNo)
	{
		RoomConfig tRoom = mFloorRooms.findRoom(paramRoomNo);

		if (tRoom == null)	return -1;
		else				return tRoom.mHVAC.Pressure;
	}
	
	public RoomConfig RoomAt(int paramIdx)
	{
		return mFloorRooms.getRoom(paramIdx);
	}
	
	public int getForce(int paramIdx)
	{
		return mFloorRooms.getRoom(paramIdx).mHVAC.Force;
	}
	
	public int getDirection(int paramIdx)
	{
		return mFloorRooms.getRoom(paramIdx).mHVAC.Direction;
	}
		
	public void   printFloor()
	{
		System.out.println("Floor: " + mFloorName);
		System.out.println("Width: " + mFloorWidth);
		System.out.println("Height: " + mFloorHeight);
		System.out.println("Unit: " + mCellUnitSize);
		System.out.println("Padding: " + mPadSize);
		mFloorWalls.printPlan();
		mFloorRooms.printPlan();
	}
}

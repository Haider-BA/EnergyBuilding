package SEBM.LBM;

import SEBM.Configuration.FloorPlan;
import SEBM.Configuration.RoomConfig;

public class LBMLattice 
{
	private int mWidth;
	private int mHeight;
	private int mPad;
	
	private int mRealWidth;
	private int mRealHeight;
	
	private LBMUnit[][] mLattice;
	private LBMUnit[][] mTempLattice;
	
	private FloorPlan   mFloorPlan;
	
	public LBMLattice(int paramW, int paramH)
	{
		mWidth = paramW;
		mHeight = paramH;
		
		mLattice = new LBMUnit[mWidth + 2][mHeight + 2];
		mTempLattice = new LBMUnit[mWidth + 2][mHeight + 2];
	}

	public LBMLattice(FloorPlan paramPlan)
	{
		mFloorPlan = paramPlan;
		
		mWidth = (int)paramPlan.Width();
		mHeight = (int)paramPlan.Height();
		mPad    = (int)paramPlan.Pad();
		
		mRealWidth = mWidth + mPad * 2;
		mRealHeight = mHeight + mPad *2;
		
		mLattice = new LBMUnit[mRealWidth][mRealHeight];
		mTempLattice = new LBMUnit[mRealWidth][mRealHeight];
	}
	
	public void initLattice()
	{
		int i, j;
		int tIdx;
		int tRoomNo;
		
		for (i = 0; i < mRealWidth; i++)
		{
			for (j = 0; j < mRealHeight; j++)
			{
				mLattice[i][j] = new LBMUnit();
				
				mTempLattice[i][j] = new LBMUnit();

				tRoomNo = mFloorPlan.isRoom(i, j);
				
				if (mFloorPlan.isWall(i, j) == true)
				{
					mLattice[i][j].Type(D2Q9.WALL_TYPE);
					mTempLattice[i][j].Type(D2Q9.WALL_TYPE);
				}
				
				tIdx = mFloorPlan.isHVAC(i, j);
				if (tIdx > -1)
				{
					mLattice[i][j].Type(D2Q9.HVAC_TYPE);
					mLattice[i][j].Room(tRoomNo);
					mLattice[i][j].F(mFloorPlan.getDirection(tIdx), mFloorPlan.getForce(tIdx), 0);
					mLattice[i][j].initCalculation();
					
					mTempLattice[i][j].Type(D2Q9.HVAC_TYPE);
				}
				else
				{
					if (tRoomNo > 0)
						mLattice[i][j].Room(tRoomNo);
				}
			}
		}
	}
	
	public void resetLattice()
	{
		int i, j;
		
		for (i = 0; i < mRealWidth; i++)
		{
			for (j = 0; j < mRealHeight; j++)
			{
				if (mLattice[i][j].Type() == D2Q9.NORMAL_TYPE)
					mLattice[i][j].resetUnit();
			}
		}
	}
	
	public int getRoomNumber(int paramX, int paramY)
	{
		return mLattice[paramX][paramY].Room();
	}
	
	public int  getCellType(int paramX, int paramY)
	{
		return mLattice[paramX][paramY].Type();
	}
	
	public void addInitForce()
	{
		int i;
		for (i = 10; i < 12; i++)
		{
			mLattice[1][i].increaseF(1, 10);
		}
	}
	
	public void streamingLattice()
	{
		int i, j;
		
		for (i = 0; i < mFloorPlan.RoomCount(); i++)
		{
			RoomConfig tRoomConfig = mFloorPlan.RoomAt(i);
			if (tRoomConfig.mHVAC.isOn())
				mLattice[tRoomConfig.mHVAC.X][tRoomConfig.mHVAC.Y].F(tRoomConfig.mHVAC.Direction, tRoomConfig.mHVAC.Force, 0);
			else
				mLattice[tRoomConfig.mHVAC.X][tRoomConfig.mHVAC.Y].F(0, 0, 0);
		}
		
		for (i = 0; i < mRealWidth; i++)
		{
			for (j = 0; j < mRealHeight; j++)
			{
				mLattice[i][j].step();
			}
		}
		
		copyLattice();
	}

	public void boundaryConditions()
	{
		int i;
		
		for(i = 2; i < mWidth; i++) 
		{
			mLattice[i][0].F(6, mLattice[i][mHeight].F(6));
			mLattice[i][0].F(2, mLattice[i][mHeight].F(2));
			mLattice[i][0].F(5, mLattice[i][mHeight].F(5));
			mLattice[i][mHeight + 1].F(7, mLattice[i][1].F(7));
			mLattice[i][mHeight + 1].F(4, mLattice[i][1].F(4));
			mLattice[i][mHeight + 1].F(8, mLattice[i][1].F(8));
		}
		
		// East & West Boundary
		for(i = 1; i < mHeight + 1; i++) 
		{
			mLattice[0][i].F(5, mLattice[mWidth][i].F(5));
			mLattice[0][i].F(8, mLattice[mWidth][i].F(8));
			mLattice[0][i].F(1, mLattice[mWidth][i].F(1));
			mLattice[mWidth+1][i].F(7, mLattice[1][i].F(7));
			mLattice[mWidth+1][i].F(3, mLattice[1][i].F(3));
			mLattice[mWidth+1][i].F(6, mLattice[1][i].F(6));
		}
		
		// Corners
		mLattice[mWidth+1][0].F(6, mLattice[1][mHeight].F(6));
		mLattice[0][mHeight+1].F(8, mLattice[mWidth][1].F(8));
		mLattice[mWidth][mHeight+1].F(7, mLattice[1][1].F(7));
		mLattice[0][0].F(5, mLattice[mWidth][mHeight].F(5));		
	}
	
	public void copyLattice()
	{
		int i, j;

		for (i = 0; i < mWidth + 2; i++)
		{
			for (j = 0; j < mHeight + 2; j++)
			{
				mTempLattice[i][j].Copy(mLattice[i][j]);
			}
		}
	}
	
	public int bounceBack(int paramDirection)
	{
		switch (paramDirection)
		{
		case 1: return 3;
		case 2: return 4;
		case 3: return 1;
		case 4: return 2;
		case 5: return 7;
		case 6: return 8;
		case 7: return 5;
		case 8: return 6;
		default: return 0;
		}
	}
	
	public void propagateLattice()
	{
		int i, j, k;
		int adjX, adjY;
		
		copyLattice();	// copy mLattice to mTempLattice
		
		for (i = 1; i <= mWidth; i++)
		{
			for (j = 1; j <= mHeight; j++)
			{
				for (k = 1; k < D2Q9.Q_COUNT; k++)	// start from 1 because index '0' means stay, and index '0' also means itself
				{
					adjX = (int)(i + D2Q9.E[k][0]);
					adjY = (int)(j + D2Q9.E[k][1]);

					if (mFloorPlan.isWall(adjX, adjY) == true)
						mLattice[i][j].addF(bounceBack(k) / 3, mTempLattice[i][j].F(k));
					else
						mLattice[adjX][adjY].addF(k, mTempLattice[i][j].F(k));
				}
			}
		}
	}
	
	public void updateLattice()
	{
//		System.out.println("*******************");

//		System.out.println("=========================");
//		showAdjacent(1, 10);
//		showAdjacentUnitVector(1, 10);
//		System.out.println("=========================");
		
//		showUnit(9, 1);
		
		streamingLattice();

//		System.out.println("=========================");
//		showAdjacent(1, 10);
//		showAdjacentUnitVector(1, 10);
//		System.out.println("=========================");
		
//		boundaryConditions();

		propagateLattice();
		
//		System.out.println("*******************");

//		initForce();
	}
	
	public double getUnitX(int paramX, int paramY) { return mLattice[paramX][paramY].Ux(); }
	public double getUnitY(int paramX, int paramY) { return mLattice[paramX][paramY].Uy(); }
	
	public double F(int paramX, int paramY, int paramIdx) 
	{ 
		return mLattice[paramX][paramY].F(paramIdx);
	}
	
	public void F(int paramX, int paramY, int paramIdx, double paramVal)
	{
		mLattice[paramX][paramY].F(paramIdx, paramVal);
	}
	
	public void showLattice()
	{
		int i, j;
		
		for (i = 0; i < mWidth + 2; i++)
		{
			for (j = 0; j < mHeight + 2; j++)
			{
				System.out.println(mLattice[i][j].toString());
			}
		}
	}
	
	public void showAdjacent(int paramX, int paramY)
	{
		String strTemp = "";

		strTemp = mLattice[paramX - 1][paramY - 1].toMatrix(1) + " | " + mLattice[paramX][paramY - 1].toMatrix(1) + " | " + mLattice[paramX + 1][paramY - 1].toMatrix(1) + "\n";
		strTemp += mLattice[paramX - 1][paramY - 1].toMatrix(2) + " | " + mLattice[paramX][paramY - 1].toMatrix(2) + " | " + mLattice[paramX + 1][paramY - 1].toMatrix(2) + "\n";
		strTemp += mLattice[paramX - 1][paramY - 1].toMatrix(3) + " | " + mLattice[paramX][paramY - 1].toMatrix(3) + " | " + mLattice[paramX + 1][paramY - 1].toMatrix(3) + "\n";
		strTemp += "---------------------------------------------------------------------------------\n";
		strTemp += mLattice[paramX - 1][paramY].toMatrix(1) + " | " + mLattice[paramX][paramY].toMatrix(1) + " | " + mLattice[paramX + 1][paramY].toMatrix(1) + "\n";
		strTemp += mLattice[paramX - 1][paramY].toMatrix(2) + " | " + mLattice[paramX][paramY].toMatrix(2) + " | " + mLattice[paramX + 1][paramY].toMatrix(2) + "\n";
		strTemp += mLattice[paramX - 1][paramY].toMatrix(3) + " | " + mLattice[paramX][paramY].toMatrix(3) + " | " + mLattice[paramX + 1][paramY].toMatrix(3) + "\n";
		strTemp += "---------------------------------------------------------------------------------\n";
		strTemp += mLattice[paramX - 1][paramY + 1].toMatrix(1) + " | " + mLattice[paramX][paramY + 1].toMatrix(1) + " | " + mLattice[paramX + 1][paramY + 1].toMatrix(1) + "\n";
		strTemp += mLattice[paramX - 1][paramY + 1].toMatrix(2) + " | " + mLattice[paramX][paramY + 1].toMatrix(2) + " | " + mLattice[paramX + 1][paramY + 1].toMatrix(2) + "\n";
		strTemp += mLattice[paramX - 1][paramY + 1].toMatrix(3) + " | " + mLattice[paramX][paramY + 1].toMatrix(3) + " | " + mLattice[paramX + 1][paramY + 1].toMatrix(3) + "\n";
		
		System.out.println(strTemp);
	}
	
	public void showAdjacentUnitVector(int paramX, int paramY)
	{
		String strTemp = "";

		strTemp = mLattice[paramX - 1][paramY - 1].toUVector() + " | " + mLattice[paramX][paramY - 1].toUVector() + " | " + mLattice[paramX + 1][paramY - 1].toUVector() + "\n";
		strTemp += "---------------------------------------------------------------------------------\n";
		strTemp += mLattice[paramX - 1][paramY].toUVector() + " | " + mLattice[paramX][paramY].toUVector() + " | " + mLattice[paramX + 1][paramY].toUVector() + "\n";
		strTemp += "---------------------------------------------------------------------------------\n";
		strTemp += mLattice[paramX - 1][paramY + 1].toUVector() + " | " + mLattice[paramX][paramY + 1].toUVector() + " | " + mLattice[paramX + 1][paramY + 1].toUVector() + "\n";
		
		System.out.println(strTemp);
	}
	
	public void showUnit(int paramX, int paramY)
	{
		System.out.println( "Cur: " + mLattice[paramX][paramY].toString());
	}
	
	public void showSaveUnit(int paramX, int paramY)
	{
		System.out.println( "Sav: " + mTempLattice[paramX][paramY].toString());
	}

	public void showInitForces()
	{
		int i;
		
		for (i = 10; i < 11; i++)
		{
			showSaveUnit(1,i);
			showUnit(1, i);
		}
	}
}

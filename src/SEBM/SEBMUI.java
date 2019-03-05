package SEBM;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import SEBM.Configuration.FloorPlan;
import SEBM.DrawPrimitives.TileArrow;
import SEBM.DrawPrimitives.TileHVAC;
import SEBM.DrawPrimitives.TileWall;
import SEBM.DrawPrimitives.TileWhite;
import SEBM.LBM.D2Q9;
import SEBM.LBM.LBMLattice;

@SuppressWarnings("serial")

public class SEBMUI extends Frame
{
	FloorPlan mFloorPlan;
	long      mWidth, mHeight;
	long      mCaptionHeight;
	long      mLogHeight;
	long      mFrameCount;
	long      mSimulationCount;
	
	private int mRoundCount;
	private int mRoundSubCount;
	
	// below two measurement will be used to calculate base efficiency ratio.
	SEBMMeasureElement			  mFirstMeasure;	// time difference measurement of first simulation
	SEBMMeasureElement			  mSecondMeasure;	// time difference measurement of second simulation
	SEBMMeasureElement			  mIterMeasure;		// for dynamic test
	
	// store each simulation with adjusted weights.
	ArrayList<SEBMMeasureElement> mMeasures;
	private double        mModifyWeight;
	private double        mModifyWeightPrev;
	
	private LBMLattice    mLattice;
	private BufferedImage mImgFrame;
	
	private TileArrow mTileArrow;
	private TileWhite mTileWhite;
	private TileWall  mTileWall;
	private TileHVAC  mTileHVAC;
	
	private Font      mLogFont;
	
	private AffineTransform mAffTrans;
	
	private FileWriter mFileWriter, mResultWriter;
	private BufferedWriter mBufWriter, mResultBufWriter;
	
	public SEBMUI(FloorPlan paramFloor)
	{
		mFloorPlan = paramFloor;
		mFrameCount = 0;
		mSimulationCount = 0;
		mRoundCount = 0;
		
		// initialize modification weight to 10% (0.1)
		mModifyWeight = 0.1;
		mModifyWeightPrev = 0.0;
		
		openLogFile();
		
		mWidth = (mFloorPlan.Width() + mFloorPlan.Pad() * 2) * mFloorPlan.Unit();
		mHeight = (mFloorPlan.Height() + mFloorPlan.Pad() * 2) * mFloorPlan.Unit();
		mLogHeight = mFloorPlan.Unit() * 8;
		mCaptionHeight = (int)(mFloorPlan.Unit() * 1.5);
		setSize((int)mWidth, (int)(mHeight + mCaptionHeight + mLogHeight));
		
		mLogFont = new Font("Arial", Font.PLAIN, 16);
		
		// data structure for calculating results
		mMeasures = new ArrayList<SEBMMeasureElement>();
		mFirstMeasure = new SEBMMeasureElement();
		mSecondMeasure = new SEBMMeasureElement();
		
		// save first and second configuration of pressure of each room.
		// these two simulations will have same pressure values, but use different algorithm
		for (int i = 0; i < mFloorPlan.RoomCount(); i++)
		{
			mFirstMeasure.addWeight(mFloorPlan.pressureAtRoom(i + 1));
			mSecondMeasure.addWeight(mFloorPlan.pressureAtRoom(i + 1));
		}
		
		mLattice = new LBMLattice(mFloorPlan);
		mLattice.initLattice();
		
		mImgFrame = new BufferedImage((int)mWidth, (int)(mHeight + mCaptionHeight + mLogHeight), BufferedImage.TYPE_INT_RGB);
		
		mTileArrow = new TileArrow((int)mFloorPlan.Unit(), Color.WHITE, Color.BLACK);
		mTileWhite = new TileWhite((int)mFloorPlan.Unit());
		mTileWall = new TileWall((int)mFloorPlan.Unit());
		mTileHVAC = new TileHVAC((int)mFloorPlan.Unit(), Color.BLUE);

		addWindowListener( new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
				System.exit(0);
			}
		});
	}

	private void openLogFile()
	{
		String strFilePath = "./log/SimLog" + (mSimulationCount + 1) + ".log";
		String strResultFilePath = "./log/SimResult.log";
		
		try
		{
			mFileWriter = new FileWriter(strFilePath);
			mBufWriter = new BufferedWriter(mFileWriter);
			
			// result data will be accumulated, so append option is on
			mResultWriter = new FileWriter(strResultFilePath, true);
			mResultBufWriter = new BufferedWriter(mResultWriter);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void writeLogLine()
	{
		String strLogFile = "";
		
		for (int i = 0; i < mFloorPlan.RoomCount(); i++)
		{
			strLogFile += " " + mFrameCount;
			strLogFile += ", " + mFloorPlan.Rooms().getRoom(i).Number();
			strLogFile += ", " + mFloorPlan.Rooms().getRoom(i).mTemperature.Target();
			strLogFile += ", " + mFloorPlan.Rooms().getRoom(i).mTemperature.Current() + "\n";
		}
		
		try
		{
			mBufWriter.write(strLogFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	private void writeResultLogLine()
	{
		String strLogFile;
		
		
		try
		{
			strLogFile  = "Simulation: " + mSimulationCount + " Round: " + mRoundCount;
			strLogFile += " SubRound: " + mRoundSubCount;
			strLogFile += " Weights: (" + mModifyWeight + ", " + mModifyWeightPrev + ") \n";
			strLogFile += mFloorPlan.Rooms().toTimeLines();
			if (mSimulationCount == 1)
				strLogFile += "Ratio: Unknown\n";
			else if (mSimulationCount == 2)
				strLogFile += "Ratio: " + getRatio(1) + " \n";
			else
				strLogFile += "Ratio: " + getRatio(2) + " \n";
				
			mResultBufWriter.write(strLogFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	private void closeLogFile()
	{
		try
		{
			mBufWriter.close();
			mFileWriter.close();
			
			mResultBufWriter.close();
			mResultWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
		
	public void finalize()
	{
		closeLogFile();
	}
	
	public void clearFrame()
	{
		int i, j;
		
		for (i = 0; i < mWidth; i++)
		{
			for (j = 0; j < mHeight; j++)
			{
				mImgFrame.setRGB(i, j, Color.WHITE.getRGB());
			}
		}
	}
		
	public void clearLogFrame()
	{
		int i, j;
		
		for (i = 0; i < mWidth; i++)
		{
			for (j = (int)(mHeight + mCaptionHeight); j < (int)(mHeight + mCaptionHeight + mLogHeight); j++)
			{
				mImgFrame.setRGB(i, j, Color.BLACK.getRGB());
			}
		}
	}
	
	public void drawFrame()
	{
		int i, j;
		int tWidth, tHeight;
		int tRoomNo;
		
		Graphics2D gr2d = mImgFrame.createGraphics();

		tWidth = (int)(mFloorPlan.Width() + mFloorPlan.Pad() * 2);
		tHeight = (int)(mFloorPlan.Height() + mFloorPlan.Pad() * 2);
		
		for (i = 0; i < tWidth; i++)
		{
			for (j = 0; j < tHeight; j++)
			{
				if (mLattice.getCellType(i, j) == D2Q9.WALL_TYPE)
				{
					gr2d.drawImage(mTileWall.Tile(), (int)(i * mFloorPlan.Unit()), (int)(j * mFloorPlan.Unit() + mCaptionHeight),  null);
				}
				else if (mLattice.getCellType(i, j) == D2Q9.HVAC_TYPE)
				{
					tRoomNo = mLattice.getRoomNumber(i, j);
					
					if (mFloorPlan.isOnHVAC(tRoomNo) == true)
					{
						mTileHVAC.turnOn();
						mTileArrow.colorTile(Color.WHITE);
					}
					else
					{
						mTileHVAC.turnOff();
						mTileArrow.colorTile(Color.BLACK);
					}

					mTileArrow.drawArrow();
					mAffTrans = mTileArrow.rotateTileByCoordinate(mLattice.getUnitX(i, j), (-1) * mLattice.getUnitY(i, j));
					
					mTileHVAC.mixTile(mTileArrow.Tile(), mAffTrans);
					gr2d.drawImage(mTileHVAC.Tile(), (int)(i * mFloorPlan.Unit()), (int)(j * mFloorPlan.Unit() + mCaptionHeight), null);
				}
				else
				{
					tRoomNo = mLattice.getRoomNumber(i, j);
					
//					mTileWhite.clearTile();
//					mTileArrow.clearTile();
					
					mTileWhite.ColorTileByTemperature(mFloorPlan.temperatureAtRoom(tRoomNo));
					mTileArrow.ColorTileByTemperature(mFloorPlan.temperatureAtRoom(tRoomNo));
					mTileArrow.drawArrow();
					mAffTrans = mTileArrow.rotateTileByCoordinate(mLattice.getUnitX(i, j), (-1) * mLattice.getUnitY(i, j));
					
					mTileWhite.mixTile(mTileArrow.Tile(), mAffTrans);
					gr2d.drawImage(mTileWhite.Tile(), (int)(i * mFloorPlan.Unit()), (int)(j * mFloorPlan.Unit() + mCaptionHeight), null);
				}
			}
		}
				
		gr2d.setFont(mLogFont);
		
		String strLog = "<Simulation Count: " + mSimulationCount + ">, " 
					  + "<Frame Count: " + mFrameCount + ">, "
					  + "<Total Volume: " + mFloorPlan.Rooms().Volume() + ">, "
					  + "<Used Volume: " + mFloorPlan.Rooms().UsedVolume() + ">";
		
		gr2d.drawString(strLog, 40, (int)(mHeight + mCaptionHeight + 10));
		
		for (int r = 0; r < mFloorPlan.RoomCount(); r++)
		{
			strLog = mFloorPlan.Rooms().toLog(r);
			gr2d.drawString(strLog, 40, (int)(mHeight + mCaptionHeight + 30 + r * 20));
		}

		gr2d.dispose();
	}
	
	public void getFirstMeasurement()
	{
		for (int i = 0; i < mFloorPlan.RoomCount(); i++)
			mFirstMeasure.addTimeDiff(mFloorPlan.Rooms().mTimeDiffs.get(i));
		
		// From now on, pressure of each room will be redistributed when HVAC is off				
		mFloorPlan.setDistribute(); 	
	}
	
	public void getSecondMeasurement()
	{
		for (int i = 0; i < mFloorPlan.RoomCount(); i++)
			mSecondMeasure.addTimeDiff(mFloorPlan.Rooms().mTimeDiffs.get(i));

		mSecondMeasure.calcRatio(mFirstMeasure);
	}
	
	public void getIterationMeasurement()
	{
		for (int i = 0; i < mFloorPlan.RoomCount(); i++)
			mIterMeasure.addTimeDiff(mFloorPlan.Rooms().mTimeDiffs.get(i));

		mIterMeasure.calcRatio(mFirstMeasure);
	}

	private double getRatio(int paramWhich)
	{
		if (paramWhich == 1)
			return mSecondMeasure.Ratio() * 100;
		else
			return mIterMeasure.Ratio() * 100;
	}

	private double findMaxRatio()
	{
		double tMaxRatio = 0;
		
		for (int i = 0; i < mFloorPlan.RoomCount(); i++)
		{
			if (mMeasures.get(i + mRoundCount * mFloorPlan.RoomCount()).Ratio() > tMaxRatio)
				tMaxRatio = mMeasures.get(i).Ratio();
		}
		
		return tMaxRatio;
	}

	private double calcSlope()
	{
		double tPrevR, tCurrR;
		
		if (mRoundCount == 0)
			tPrevR = mSecondMeasure.Ratio();
		else
			tPrevR = mMeasures.get(mMeasures.size() - 1).Ratio();
		
		tCurrR = findMaxRatio();
		
		return (tCurrR / tPrevR);
	}
	
	private double absoluteDouble(double paramDouble)
	{
		if (paramDouble < 0) return -paramDouble;
		else                 return  paramDouble;
	}
	
	private void prepareNextIteration()
	{
		double tSlope;
		double tDelta;
		double tWeight;
		
		if ( mRoundSubCount == mFloorPlan.RoomCount() )
		{
			tSlope = calcSlope();
			tWeight = mModifyWeight;
			
			tDelta = absoluteDouble(mModifyWeight - mModifyWeightPrev) / 2;
			
			if (tSlope > 1)
				mModifyWeight = mModifyWeight + tDelta;
			else
				mModifyWeight = mModifyWeight - tDelta;
				
			mModifyWeightPrev = tWeight;
			
			mRoundSubCount = 0;
			mRoundCount++;
		}
		
		mIterMeasure = new SEBMMeasureElement();
		mIterMeasure.copyWeights(mSecondMeasure);
		mIterMeasure.adjustWeights(mRoundSubCount++, mModifyWeight);
		mFloorPlan.applyNewPressures(mIterMeasure);
		mMeasures.add(mIterMeasure);
	}

	
	public void paint(Graphics paramG)
	{
		Graphics2D gr2d = (Graphics2D)paramG;

		mFrameCount++;
		
		if (mFrameCount > mFloorPlan.mFrameMax)
		{
			mFrameCount = 0;

			mSimulationCount++;
			
			// show time line of HVAC is off, it means each time the room reaches to target temperature
			mFloorPlan.Rooms().printTimeLines();
			
			// calculate average time difference between consecutive two time lines.
			mFloorPlan.Rooms().calcTimeDiffs();
							
			if (mSimulationCount == 1) // with algorithm 1 (no-distribute algorithm)
			{
				getFirstMeasurement();

				writeResultLogLine();
				
				// prepare algorithm 2				
				mFloorPlan.setDistribute(); 	
			}
			else if (mSimulationCount == 2) // with algorithm 2 (redistribute algorithm)
			{
				getSecondMeasurement();
				
				System.out.println("Ratio: " + mSecondMeasure.Ratio() * 100);

				writeResultLogLine();

				// prepare algorithm 3
				mRoundCount = 0;		// algorithm 3 has rounds
				mRoundSubCount = 0;		// each round has sub-rounds
				prepareNextIteration();
			}
			else // with algorithm 3 (dynamic redistribute algorithm)
			{					
				getIterationMeasurement();
				
				System.out.println("Ratio: " + mIterMeasure.Ratio() * 100);

				writeResultLogLine();
				
				prepareNextIteration();
			}

			
			closeLogFile();
			
			// reset simulation environment
			mLattice.resetLattice();
			mFloorPlan.resetFloorPlan();
			mFloorPlan.initRoomPressures();
			
			if (mRoundCount == 10)
				System.exit(0);
			
			openLogFile();
		}
		
//		clearFrame();
		clearLogFrame();
		
		drawFrame();
		
		writeLogLine();
		
		gr2d.drawImage(mImgFrame, 0, 0, null);

		repaint();
		
		gr2d.dispose();

		try
		{
			Thread.sleep(5);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		mFloorPlan.updateRooms();
		mLattice.updateLattice();
	}
}

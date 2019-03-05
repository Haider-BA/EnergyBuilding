package SEBM.LBM;

public class LBMUnit 
{
	// Unit Type
	private int      mType;
	
	private int      mRoomIndex;
	
	// Force Vectors
	private double[] mF;
	
	// Force Vectors after propagation
	private double[] mFEQ;
	
	// Force Vectors to save current values
	private double[] mFSave;
	
	// Sum of all Force Vectors
	private double   mRHO;
	
	// Initial value of ?????
	private double   mTAU;
	
	// Unit Vector of "RHO"
	private double[] mU;
	
	public LBMUnit()
	{
		allocUnit();
		resetUnit();
	}
	
	private void allocUnit()
	{
		mType   = D2Q9.NORMAL_TYPE;
		
		mF 		= new double[D2Q9.Q_COUNT];
		mFEQ 	= new double[D2Q9.Q_COUNT];
		mFSave	= new double[D2Q9.Q_COUNT];
		mU 		= new double[2];
		mRoomIndex = -1;
	}
	
	public void resetUnit()
	{
		int i;

		mRHO 	= 1;
		mTAU 	= 1;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			initF(i, true);	// initialize F with Zero
			mFEQ[i] = 0;
			mFSave[i] = 0;
		}
		
		mU[0] = 0;
		mU[1] = 0;
	}
	
	public void   Copy(LBMUnit paramLBM)
	{
		int i;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			mF[i] = paramLBM.F(i);
			mFEQ[i] = paramLBM.Feq(i);
		}
		
		mU[0] = paramLBM.Ux();
		mU[1] = paramLBM.Uy();
		
		mRHO = paramLBM.RHO();
	}
	
	public void initCalculation()
	{
		calcRHO();
		calcU();
	}
	
	private void  calcRHO()
	{
		int i;
		
		mRHO = 0;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
			mRHO += mF[i];
	}
	
	// Prerequisite: calcRHO is called right before
	private void calcU()
	{
		int i;
				
		mU[0] = 0;
		mU[1] = 0;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			mU[0] += mF[i] * D2Q9.W[i] * D2Q9.E[i][0];
			mU[1] += mF[i] * D2Q9.W[i] * D2Q9.E[i][1];
		}
		
		mU[0] /= mRHO;
		mU[1] /= mRHO;
	}
	
	private double squareU()
	{
		return (mU[0] * mU[0] + mU[1] * mU[1]);
	}
	
	private double dotEU(int paramIdx)
	{
		return (mU[0] * D2Q9.E[paramIdx][0] + mU[1] * D2Q9.E[paramIdx][1]);
	}
	
	// Prerequisite: calcRHO and calcU are called right before
	private void calcFEQ()
	{
		int i;
		double uSquare;
		double euDot;
		
		uSquare = squareU();
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			euDot = dotEU(i);	// calculate dot product of Ei and U
			mFEQ[i] = mRHO * D2Q9.W[i] * (1 + 3 * euDot + 4.5 * euDot * euDot - 1.5 * uSquare);
		}
	}
	
	public void step()
	{
		int i;
		
		if (mType != D2Q9.NORMAL_TYPE) return;
		
		// below three function calls are mandatory with its sequence.
		calcRHO();
		
		if (mRHO == 0) return;
		
		if (Double.isInfinite(mRHO)) return;
		
		calcU();
		calcFEQ();
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			mFSave[i] = mF[i] - (mF[i] - mFEQ[i]) / mTAU;
			
			if (Double.isInfinite(mFSave[i]))
				System.out.println(this.toString());
		}
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
			mF[i] = mFSave[i];
	}
	
	
	public void   Room(int paramNo) { mRoomIndex = paramNo; }
	public int    Room()            { return mRoomIndex; }
	
	public double F(int paramIdx)	{ return mF[paramIdx]; }
	public void   F(int paramIdx, double paramVal) { mF[paramIdx] = paramVal; }
	public void   F(int paramIdx, double paramVal, double paramAdj)
	{
		int i;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
			mF[i] = paramAdj;
		
		mF[paramIdx] = paramVal;
	}
	
	public void   F(double[] paramFs)
	{
		int i;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
			mF[i] = paramFs[i];
	}
	public void   addF(int paramIdx, double paramVal) 
	{ 
		switch (mType)
		{
		case D2Q9.NORMAL_TYPE:
			mF[paramIdx] += paramVal;
			break;
		case D2Q9.HVAC_TYPE:
			// Do Nothing
			break;
		case D2Q9.WALL_TYPE:
			// Bounce Back or Decay
			break;
		}
	}
	
//	{ mF[paramIdx] += paramVal; }
	public void   increaseF(int paramIdx, double paramVal) { mF[paramIdx] += paramVal; }
	public void   initF(int paramIdx, boolean paramZero)
	{
		if (paramZero == true)
		{
			mF[paramIdx] = 0;
		}
		else
		{
			mF[paramIdx] = D2Q9.W[paramIdx];
		}
	}

	public double Feq(int paramIdx)	{ return mFEQ[paramIdx]; }
	public void   Feq(int paramIdx, double paramVal) { mFEQ[paramIdx] = paramVal; }
	public void   Feq(double[] paramFEQs)
	{
		int i;
		
		for (i = 0; i < D2Q9.Q_COUNT; i++)
			mFEQ[i] = paramFEQs[i];
	}
	
	public double RHO()				{ return mRHO; }
	public void   RHO(double paramVal)	{ mRHO = paramVal; }
	
	public double TAU()				{ return mTAU; }
	public void   TAU(double paramVal)	{ mTAU = paramVal; }

	public double Ux()	{ return mU[0]; }
	public void   Ux(double paramVal) { mU[0] = paramVal; }
	
	public double Uy()	{ return mU[1]; }
	public void   Uy(double paramVal) { mU[1] = paramVal; }

	public void   U(double paramX, double paramY) { mU[0] = paramX; mU[1] = paramY; }
	
	public int    Type() { return mType; }
	public void   Type(int paramType) { mType = paramType; }
	
	private String FtoString()
	{
		int i;
		
		String strTemp = "[";
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			if (i == D2Q9.Q_COUNT - 1)
				strTemp += " " + mF[i];
			else
				strTemp += " " + mF[i] + ",";
		}
		strTemp += "]";
		
		return strTemp;
	}
/*	
	private String FtoMatix()
	{
		String strTemp = "[" + mF[8] + "," + mF[1] + "," + mF[5] + "]\n";
		strTemp       += "[" + mF[4] + "," + mF[0] + "," + mF[2] + "]\n"; 
		strTemp       += "[" + mF[7] + "," + mF[3] + "," + mF[6] + "]\n"; 
		
		return strTemp;
	}
*/
	
	private String FEQtoString()
	{
		int i;
		
		String strTemp = "[";
		for (i = 0; i < D2Q9.Q_COUNT; i++)
		{
			if (i == D2Q9.Q_COUNT - 1)
				strTemp += " " + mFEQ[i];
			else
				strTemp += " " + mFEQ[i] + ",";
		}
		strTemp += "]";
		
		return strTemp;
	}
	
	public String toString()
	{
		String strTemp = "{";
		
		strTemp += "[" + mRHO + "],";
		strTemp += "<" + mU[0] + ", " + mU[1] + ">,\n";
		strTemp += FtoString() + ",\n";
		strTemp += FEQtoString();
		strTemp += "}\n";
		
		return strTemp;
	}
	
	public String toUVector()
	{
		String strTemp = "{";
		
		strTemp += mRHO + ",";
		strTemp += "<" + mU[0] + ", " + mU[1] + ">";
		strTemp += "}";
		
		return strTemp;
	}
	
	public String toMatrix(int paramRow)
	{
		String strTemp = "";

		switch (paramRow)
		{
		case 1: strTemp = "[" + mF[8] + "," + mF[4] + "," + mF[5] + "]"; break;
		case 2: strTemp = "[" + mF[3] + "," + mF[0] + "," + mF[1] + "]"; break; 
		case 3: strTemp = "[" + mF[7] + "," + mF[2] + "," + mF[6] + "]"; break; 
		}
		
		return strTemp;
	}
}

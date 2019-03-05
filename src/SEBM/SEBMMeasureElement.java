package SEBM;

import java.util.ArrayList;

public class SEBMMeasureElement 
{
	// each room has its own weight.
	private ArrayList<Double> mWeights;
	
	// each room has average value of time difference.
	private ArrayList<Double> mTimeDiffs;
	
	// compared to other result
	private double            mRatio;
	
	public SEBMMeasureElement()
	{
		mWeights   = new ArrayList<Double>();
		mTimeDiffs = new ArrayList<Double>();
		mRatio = 1;
	}
	
	public void resetMeasureElement()
	{
		mWeights.clear();
		mTimeDiffs.clear();
		mRatio = 1;
	}
	
	public int WSize()
	{
		return mWeights.size();
	}
	
	public int TSize()
	{
		return mTimeDiffs.size();
	}
	
	public void copyTimeDiff(SEBMMeasureElement paramE)
	{
		int nSize = paramE.TSize();
		
		for (int i = 0; i < nSize; i++)
			mTimeDiffs.add(new Double(paramE.getTimeDiff(i)));
	}
	
	public void addTimeDiff(double paramDiff)
	{
		mTimeDiffs.add(new Double(paramDiff));
	}
	
	public double getTimeDiff(int paramIdx)
	{
		if (paramIdx >= mTimeDiffs.size()) 
			return 0;
		else 
			return (mTimeDiffs.get(paramIdx).doubleValue());
	}
	
	public void copyWeights(SEBMMeasureElement paramE)
	{
		int nSize = paramE.WSize();
		
		for (int i = 0; i < nSize; i++)
			mWeights.add(new Double(paramE.getWeight(i)));
	}
	
	// adjust weight of room(= paramR) by delta weight(= paramD).
	// and modify weight of all the other rooms to make sum of weight must be the same.
	public void adjustWeights(int paramR, double paramD)
	{
		double tPressure = mWeights.get(paramR);
		double tDistribute = (-1) * (paramD / (mWeights.size() - 1));

		mWeights.set(paramR, tPressure + paramD);
		
		for (int i = 0; i < mWeights.size(); i++)
		{
			if (i == paramR) continue;
			tPressure = mWeights.get(i);
			mWeights.set(i, tPressure + tDistribute);
		}
	}
	
	public void addWeight(double paramW)
	{
		mWeights.add(new Double(paramW));
	}
	
	public double getWeight(int paramIdx)
	{
		if (paramIdx >= mWeights.size())
			return 0;
		else
			return (mWeights.get(paramIdx).doubleValue());
	}
	
	public double Ratio()
	{
		return mRatio;
	}
	
	public void calcRatio(SEBMMeasureElement paramElement)
	{
		int nSize = mTimeDiffs.size();
		double tSum = 0;
		
		for (int i = 0; i < nSize; i++)
			tSum += (paramElement.getTimeDiff(i) - this.getTimeDiff(i)) / paramElement.getTimeDiff(i);

		mRatio =  tSum / (mTimeDiffs.size());			
	}
}

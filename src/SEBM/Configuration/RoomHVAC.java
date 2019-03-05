package SEBM.Configuration;

public class RoomHVAC 
{
	public int X;	// x coordinate
	public int Y;	// y coordinate
	public int Force;	// initial force
	public int Direction;	// direction
	public double Pressure;	// pressure of HVAC air for this room.
	public double PressureSave;

	private boolean mOn;
	
	public RoomHVAC(boolean paramOn, int paramX, int paramY, int paramF, int paramD)
	{
		mOn = paramOn;
		X = paramX;
		Y = paramY;
		Force = paramF;
		Direction = paramD;
		Pressure = 0.0;
		PressureSave = 0.0;
	}
	
	public void Turn(boolean paramOn)
	{
		mOn = paramOn;
	}
	
	public boolean isOn()
	{
		return mOn;
	}
	
	public String toString()
	{
		String strTemp = "";
		
		strTemp += "HVAC: <";
		strTemp += "(" + X + ", " + Y + "), ";
		strTemp += "(" + Force + "), ";
		strTemp += "(" + Direction + ")";
		strTemp += ">";
		
		return strTemp;
	}
}

package SEBM.LBM;

public class D2Q9 
{
	public static final int Q_COUNT = 9;

	public static final int WALL_TYPE = 0;
	public static final int NORMAL_TYPE = 1;
	public static final int HVAC_TYPE = 2;

	// Weight of Q from 1 to 9 respectively.
	public static final double[] W = 
		{
				16.0 / 36.0,			// 4/9
				
				4.0  / 36.0,			// 1/9
				4.0  / 36.0,			// 1/9
				4.0  / 36.0,			// 1/9
				4.0  / 36.0,			// 1/9
				
				1.0  / 36.0,			// 1/36
				1.0  / 36.0,			// 1/36
				1.0  / 36.0,			// 1/36
				1.0  / 36.0,			// 1/36
		};
	
	// Direction vector with Cartesian coordinates. 
	public static final double[][] E =
		{
				{  0,  0},		// Stay

				{  1,  0},		// East
				{  0,  1},		// South
				{ -1,  0},		// West
				{  0, -1},		// North
			
				{  1, -1},		// NorthEast
				{  1,  1},		// SouthEast
				{ -1,  1},		// SouthWest
				{ -1, -1}		// NorthWest
		};
	
	public static final int[] ADJ =
		{
				8,	// NorthWest
				4,	// North
				5,	// NorthEast
				
				3,	// West
				0,	// Stay
				1,	// East
				
				7,	// SouthWest
				2,	// South
				6	// SouthEast
		};
}

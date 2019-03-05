package SEBM.DrawPrimitives;

import java.awt.Color;

public class TileHVAC extends TilePrimitive 
{
	public TileHVAC(int paramUnit, Color paramColor) 
	{
		super(paramUnit, paramColor);
		colorTile(paramColor);
	}
	
	public void turnOn()
	{
		colorTile(Color.BLUE);
	}

	public void turnOff()
	{
		colorTile(Color.GREEN);
	}
}

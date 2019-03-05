package SEBM.DrawPrimitives;

import java.awt.Color;
import java.awt.Graphics2D;

public class TileArrow extends TilePrimitive
{
	private Color mArrowColor;
	
	public TileArrow(int paramUnit, Color paramBaseColor, Color paramArrowColor)
	{
		super(paramUnit, paramBaseColor);
		
		mArrowColor = paramArrowColor;
		
		drawArrow();
	}
	
	public void drawArrow()
	{
		Graphics2D g2d = mImgTile.createGraphics();
		
		int center;
		int sx, ex;
		int tx;
		int ty1, ty2;

		sx = 2;
		ex = (int)(mCellUnitSize - 2 - 1);
		center = (int)(mCellUnitSize / 2);
		tx = center + (ex - center) / 2;
		ty1 = center - (ex - center) / 2;
		ty2 = center + (ex - center) / 2;
		
		g2d.setColor(mArrowColor);
		g2d.drawLine(sx, center, ex, center);	// arrow body
		g2d.drawLine(ex, center, tx, ty1);	// left arrow head
		g2d.drawLine(ex, center, tx, ty2);	// right arrow head
		g2d.dispose();
	}
	
	public void drawArrow(double paramTemperature)
	{
		mArrowColor = TemperatureToColor(paramTemperature);
		drawArrow();
	}

	public void drawTriangle()
	{
		Graphics2D g2d = mImgTile.createGraphics();
		
		int center;
		int sx, ex;
		int ty1, ty2;

		sx = 2;
		ex = (int)(mCellUnitSize - 2 - 1);
		center = (int)(mCellUnitSize / 2);
		ty1 = center - 1;
		ty2 = center + 1;
		
		g2d.setColor(mArrowColor);
		g2d.drawLine(sx, ty1, ex, center);	// arrow body
		g2d.drawLine(sx, ty2, ex, center);	// left arrow head
		g2d.drawLine(sx, ty1, sx, ty2);	// right arrow head
		g2d.dispose();
	}
	
	public void drawTriangle(double paramTemperature)
	{
		mArrowColor = TemperatureToColor(paramTemperature);
		
		drawTriangle();
	}
}

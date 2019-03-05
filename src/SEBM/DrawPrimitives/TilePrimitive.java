package SEBM.DrawPrimitives;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TilePrimitive 
{
	protected int mCellUnitSize;
	private   Color mBaseColor;
	
	protected BufferedImage mImgTile;
	
	public TilePrimitive(int paramUnit, Color paramColor)
	{
		mCellUnitSize = paramUnit;

		mImgTile  = new BufferedImage(mCellUnitSize, mCellUnitSize, BufferedImage.TYPE_INT_RGB);
		
		mBaseColor = paramColor;
		
		colorTile(paramColor);
	}
	
	public BufferedImage Tile() { return mImgTile; }
	
	public Color BaseColor() { return mBaseColor; }
	
	public void colorTile(Color paramColor)
	{
		for (int i = 0; i < mCellUnitSize; i++)
			for (int j = 0; j < mCellUnitSize; j++)
				mImgTile.setRGB(i, j, paramColor.getRGB());
	}

	public void clearTile()
	{
		colorTile(Color.WHITE);
	}
	
	public Color TemperatureToColor(double paramTemperature)
	{
		Color tColor;
		
		if (paramTemperature < 0)
			tColor = new Color(25, 25, 112);	// Midnight Blue
		else if (paramTemperature < 5)
			tColor = new Color(0, 0, 255);	//  Blue
		else if (paramTemperature < 10)
			tColor = new Color(0, 128, 0);	// Green
		else if (paramTemperature < 15)
			tColor = new Color(144, 248, 144);	// Light Green
		else if (paramTemperature < 20)
			tColor = new Color(255, 255, 0);	// yellow
		else if (paramTemperature < 25)
			tColor = new Color(255, 165, 0);	// Orange
		else if (paramTemperature < 30)
			tColor = new Color(255, 0, 0);	// Red
		else
			tColor = new Color(128, 0, 128);	// Purple
		
		return tColor;
	}
	
	public void ColorTileByTemperature(double paramTemperature)
	{
		colorTile(TemperatureToColor(paramTemperature));
	}
	
	public AffineTransform rotateTileByDegree(double paramDegree)
	{
		int cx, cy;
		
		if (paramDegree == 0) return null;
		
		AffineTransform at = new AffineTransform();
		
		cx = (int)(mCellUnitSize / 2);
		cy = cx;
		
		at.translate(cx, cy);
		at.rotate(Math.toRadians(paramDegree));
		at.rotate(Math.toRadians(-90));
		at.translate(-cx, -cy);

		return at;
	}
	
	public AffineTransform rotateTileByCoordinate(double paramX, double paramY)
	{
		int cx, cy;
		
		AffineTransform at = new AffineTransform();

		cx = (int)(mCellUnitSize / 2);
		cy = cx;
		
		if ( (paramX != 0) || (paramY != 0) )
		{
			at.translate(cx, cy);
			at.rotate(Math.atan2(paramX, paramY));
			at.rotate(Math.toRadians(-90));
			at.translate(-cx, -cy);
			
			return at;
		}
		else
		{
//			System.out.println("Rotation value is Zero with (" + paramX + ", " + paramY + ")");
			return null;
		}
	}
	
	public void mixTile(BufferedImage paramTile)
	{
		Graphics2D g2d = mImgTile.createGraphics();

		AffineTransform at = new AffineTransform();
		
		g2d.drawImage(paramTile,  at,  null);
		
		g2d.dispose();
	}
		
	public void mixTile(BufferedImage paramTile, AffineTransform paramAt)
	{
		if (paramAt == null) return;
		
		Graphics2D g2d = mImgTile.createGraphics();

		g2d.drawImage(paramTile,  paramAt,  null);
		
		g2d.dispose();
	}
}

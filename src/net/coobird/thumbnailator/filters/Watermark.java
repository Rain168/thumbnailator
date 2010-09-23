package net.coobird.thumbnailator.filters;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import net.coobird.thumbnailator.Position;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;

/**
 * This class applies a watermark to an image.
 * 
 * @author coobird
 *
 */
public class Watermark implements ImageFilter
{
	/**
	 * The position of the watermark.
	 */
	private final Position position;
	
	/**
	 * The watermark image.
	 */
	private final BufferedImage watermarkImg;
	
	/**
	 * The opacity of the watermark. 
	 */
	private final float opacity;
	

	/**
	 * Instantiates a filter which applies a watermark to an image.
	 * 
	 * @param position			The position of the watermark.
	 * @param watermarkImg		The watermark image.
	 * @param opacity			The opacity of the watermark.
	 * 							<p>
	 * 							The value should be between {@code 0.0f} and 
	 * 							{@code 1.0f}, where {@code 0.0f} is completely 
	 * 							transparent, and {@code 1.0f} is completely
	 * 							opaque.
	 */
	public Watermark(Position position, BufferedImage watermarkImg,
			float opacity)
	{
		this.position = position;
		this.watermarkImg = watermarkImg;
		this.opacity = opacity;
	}

	public BufferedImage apply(BufferedImage img)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		int type = img.getType();

		BufferedImage imgWithWatermark =
			new BufferedImageBuilder(width, height, type).build();
		
		int watermarkWidth = watermarkImg.getWidth();
		int watermarkHeight = watermarkImg.getHeight();

		Point p = 
			position.calculate(watermarkWidth, watermarkHeight, width, height,
				0, 0, 0, 0);

		Graphics2D g = imgWithWatermark.createGraphics();
		g.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
		);
		
		g.drawImage(watermarkImg, p.x, p.y, null);
		g.dispose();

		return imgWithWatermark;
	}
}
package net.coobird.thumbnailator.tasks;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import net.coobird.thumbnailator.ThumbnailParameter;

/**
 * A thumbnail generation task which streams data from an {@link InputStream}
 * to an {@link OutputStream}.
 * <p>
 * This class does not close the {@link InputStream} and {@link OutputStream}
 * upon the completion of processing.
 * <p>
 * Only the first image included in the data stream will be processed.
 * 
 * @author coobird
 *
 */
public class StreamThumbnailTask extends ThumbnailTask
{
	/**
	 * {@link InputStream} which is used to retrieve image data.
	 */
	private final InputStream is;
	
	/**
	 * {@link OutputStream} to which resized image data is written to. 
	 */
	private final OutputStream os;
	
	/**
	 * Creates a {@link ThumbnailTask} in which streamed image data from the 
	 * specified {@link InputStream} is output to a specified 
	 * {@link OutputStream}, using the parameters provided in the specified
	 * {@link ThumbnailParameter}.
	 * 
	 * @param param		The parameters to use to create the thumbnail.
	 * @param is		The {@link InputStream} from which to obtain image data.
	 * @param os		The {@link OutputStream} to send thumbnail data to.
	 */
	public StreamThumbnailTask(ThumbnailParameter param, InputStream is, OutputStream os)
	{
		super(param);
		this.is = is;
		this.os = os;
	}

	@Override
	public BufferedImage read() throws IOException
	{
		ImageInputStream iis = ImageIO.createImageInputStream(is);
		
		Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
		if (!readers.hasNext())
		{
			throw new IOException(
					"No acceptable ImageReader found for source data.");
		}
		
		ImageReader reader = readers.next();
		reader.setInput(iis);
		inputFormatName = reader.getFormatName();
		
		return reader.read(0);
	}

	@Override
	public boolean write(BufferedImage img) throws IOException
	{
		String formatName;
		if (param.getOutputFormat() == ThumbnailParameter.ORIGINAL_FORMAT)
		{
			formatName = inputFormatName;
		}
		else
		{
			formatName = param.getOutputFormat();
		}
			
		Iterator<ImageWriter> writers = 
			ImageIO.getImageWritersByFormatName(formatName);
		
		if (!writers.hasNext())
		{
			return false;
		}
		
		ImageWriter writer = writers.next();
		
		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		if (writeParam.canWriteCompressed())
		{
			writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			
			/*
			 * Sets the compression quality, if specified.
			 * 
			 * Note:
			 * The value to denote that the codec's default compression quality
			 * should be used is Float.NaN. 
			 */
			if (!Float.isNaN(param.getOutputQuality()))
			{
				writeParam.setCompressionQuality(param.getOutputQuality());
			}
		}
		
		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		
		writer.setOutput(ios);
		writer.write(null, new IIOImage(img, null, null), writeParam);
		
		return true;
	}
}
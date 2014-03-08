package com.messages.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class ImageProcessing {
	
	public static void reduceImageQuality(int sizeThreshold, String srcImg, String destImg, String fileType) throws Exception {
		float quality = 1.0f;// Varies from 0.0 to 1.0

		File file = new File(srcImg);

		long fileSize = file.length();

		if (fileSize <= sizeThreshold) {
			System.out.println("Image file size is under threshold");
			return;
		}
		
		// file type like 'image/png', 'image/jpeg', 'image/gif'
		fileType = fileType.substring(fileType.indexOf("/")+1);
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(fileType);
		if (!iter.hasNext())
			return;
		
		ImageWriter writer = (ImageWriter) iter.next();

		ImageWriteParam iwp = writer.getDefaultWriteParam();

		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		FileInputStream inputStream = new FileInputStream(file);

		BufferedImage originalImage = ImageIO.read(inputStream);
		IIOImage image = new IIOImage(originalImage, null, null);

		float percent = 0.1f; // 10% of 1

		while (fileSize > sizeThreshold) {
			if (percent >= quality) {
				percent = percent * 0.1f;
			}

			quality -= percent;

			File fileOut = new File(destImg);
			if (fileOut.exists()) {
				fileOut.delete();
			}
			FileImageOutputStream output = new FileImageOutputStream(fileOut);

			writer.setOutput(output);

			iwp.setCompressionQuality(quality);

			writer.write(null, image, iwp);

			File fileOut2 = new File(destImg);
			long newFileSize = fileOut2.length();
			if (newFileSize == fileSize) {
				// cannot reduce more, return
				break;
			} else {
				fileSize = newFileSize;
			}
			System.out.println("quality = " + quality + ", new file size = " + fileSize);
			output.close();
		}

		inputStream.close();
		writer.dispose();
	}
}

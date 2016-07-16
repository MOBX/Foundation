package com.lamfire.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;


public class PictureMarker {

	private static final String Watermark = "resources/mark.png";
	private static BufferedImage MarkImage;
	
	private static final int RIGHT = 10;
	private static final int BOTTOM = 10;
	
	public static BufferedImage getMarkImage(){
		if(MarkImage == null){
			try {
				URL url = ClassLoaderUtils.getResource(Watermark, PictureMarker.class);
				MarkImage = ImageIO.read(url);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return MarkImage;
	}
	
	/** */
	/**
	 * ��ͼƬӡˢ��ͼƬ��
	 * 
	 * @param pressImg
	 *            -- ˮӡ�ļ�
	 * @param targetImg
	 *            -- Ŀ���ļ�
	 * @param right
	 * @param bottom
	 */
	public final static void markImage(File source, File target, int right, int bottom) {
		try {
			//ԭ�ļ�
			BufferedImage sourceImage = ImageIO.read(source);
			int wideth = sourceImage.getWidth();
			int height = sourceImage.getHeight();
			
			//��ͼ��
			BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.drawImage(sourceImage, 0, 0, wideth, height, null);

			//ˮӡ�ļ�
			BufferedImage markImage = getMarkImage();
			int markWidth = markImage.getWidth();
			int markHeight = markImage.getHeight();
			g.drawImage(markImage, wideth - markWidth - right, height - markHeight - bottom, markWidth, markHeight, null);
			g.dispose();
			
			
			ImageIO.write(image, "JPEG", target);
			
			/**
			//������ļ���
			FileOutputStream out=new FileOutputStream(target);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);//��JPEG����	
	        out.close();
	        **/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void markImage(File source,File target){
		markImage(source,target,RIGHT,BOTTOM);
	}

	public static void main(String[] args) {
		File source = new File("D:\\data\\source.jpg");
		File target = new File("D:\\data\\target.jpg");
		markImage(source, target);
	}

}

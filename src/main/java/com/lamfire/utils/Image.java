package com.lamfire.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.lamfire.logger.Logger;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Image {
	private static final Logger LOGGER = Logger
			.getLogger(Image.class.getName());
	public static final float DEFAULT_JPEG_QUALITY = 0.85f;

	private BufferedImage image;

	public Image(BufferedImage image) {
		this.image = image;
	}

	public Image(byte[] bytes) {
		this.image = toBufferedImage(bytes);
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

	public int getColorSpaceType() {
		return image.getColorModel().getColorSpace().getType();
	}

	public int getWidth() {
		if (image == null)
			return 0;
		return image.getWidth();
	}

	public int getHeight() {
		if (image == null)
			return 0;
		return image.getHeight();
	}

	/**
	 * �Լ�ͼƬ
	 * 
	 * @param w
	 * @param h
	 */
	public void clip(int w, int h) {
		if (image == null) {
			throw new RuntimeException(
					"image file not be load.please execute 'load' function agin.");
		}

		int iSrcWidth = getWidth(); // �õ�Դͼ��
		int iSrcHeight = getHeight(); // �õ�Դͼ��

		// ���ԴͼƬ�Ŀ�Ⱥ͸߶�С��Ŀ��ͼƬ�Ŀ�Ȼ�߶ȣ���ֱ�ӷ���ԭͼ
		if (iSrcWidth < w && iSrcHeight < h) {
			LOGGER.warn("source image size too small.");
			return;
		}

		int iDstLeft = (iSrcWidth - w) / 2;
		int iDstTop = (iSrcHeight - h) / 2;

		// ����---
		this.image = image.getSubimage(iDstLeft, iDstTop, w, h);

	}

	/**
	 * �Լ�ͼƬ
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void clip(int x, int y, int w, int h) {
		if (image == null) {
			throw new RuntimeException(
					"image file not be load.please execute 'load' function agin.");
		}

		int iSrcWidth = getWidth(); // �õ�Դͼ��
		int iSrcHeight = getHeight(); // �õ�Դͼ��

		// ���ԴͼƬ�Ŀ�Ⱥ͸߶�С��Ŀ��ͼƬ�Ŀ�Ȼ�߶ȣ���ֱ�ӷ���ԭͼ
		if (iSrcWidth < w && iSrcHeight < h) {
			LOGGER.warn("source image size too small.");
			return;
		}

		if (iSrcWidth < w) {
			iSrcWidth = w;
		}

		if (iSrcHeight < h) {
			iSrcHeight = h;
		}

		// ����---
		this.image = image.getSubimage(x, y, w, h);
	}

	/**
	 * ����ͼƬ��ָ����С
	 * 
	 * @param width
	 * @param height
	 */
	public void zoomScale(int width, int height) {
		this.image = zoomScale(image, width, height);
	}

	/**
	 * ����ͼƬ����ָ��ɫ���
	 * 
	 * @param w
	 * @param h
	 * @param bgColor
	 */
	public void zoomScale(int w, int h, Color bgColor) {
		this.image = zoomScale(image, w, h, bgColor);
	}

	/**
	 * ��ͼƬ�ȱ���С���̶����,���ԭͼ��С������.
	 * 
	 * @param data
	 * @param width
	 * @return
	 */
	public void zoomScaleWidth(int width) {
		try {
			Image image = this;
			int imgW = image.getWidth();
			int imgH = image.getHeight();

			double wRate = ((double) imgW / (double) width);

			int height = (int) (imgH / wRate);

			image.zoomScale(width, height);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ͼƬ�ȱ���С���̶����,���ԭͼ��С������.
	 * 
	 * @param data
	 * @param width
	 * @return
	 */
	public void zoomScaleHeight(int fixHeight) {
		try {
			Image image = this;
			int imgW = image.getWidth();
			int imgH = image.getHeight();

			double hRate = ((double) imgH / (double) fixHeight);

			int width = (int) (imgW / hRate);

			image.zoomScale(width, fixHeight);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ͼƬ���ż��е��̶��ߴ�
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public void zoomClip(int width, int height) {
		try {

			Image image = this;

			int imgW = image.getWidth();
			int imgH = image.getHeight();

			if (imgW == width && imgH == height) {
				return;
			}

			double wRate = ((double) imgW / (double) width);
			double hRate = ((double) imgH / (double) height);

			int zoomW = width;
			int zoomH = height;

			if (wRate < hRate) {
				zoomW = (int) ((double) imgW / wRate) + 1;
				zoomH = (int) ((double) imgH / wRate) + 1;
			} else {
				zoomW = (int) ((double) imgW / hRate) + 1;
				zoomH = (int) ((double) imgH / hRate) + 1;
			}

			image.zoomScale(zoomW, zoomH);
			image.clip(width, height);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void saveAs(File file, String format) throws IOException {
		ImageIO.write(image, format, file);
	}

	public void saveAsJPEG(File file, float quality) throws IOException {
		OutputStream bos = new FileOutputStream(file);
		try {
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
			param.setQuality(quality, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(image);
			bos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				bos.close();
			} catch (IOException e) {

			}
		}
	}

	public byte[] getBytes(String format) {
		return getBytes(image, format);
	}

	public byte[] getJPEGBytes(float quality) {
		return getJPEGBytes(image, quality);
	}

	/**
	 * ����ˮӡͼ��
	 * 
	 * @param markImage
	 * @param right
	 * @param bottom
	 * @param alpha
	 */
	public void drawMarkImage(BufferedImage markImage, int right, int bottom,
			int colorType, boolean transluceny) {
		// ��ͼ��
		int wideth = this.image.getWidth();
		int height = this.image.getHeight();

		BufferedImage tagImage = new BufferedImage(wideth, height, colorType);

		Graphics g = tagImage.createGraphics();

		// ���Ϊ��͸��������ư�ɫ��
		if (!transluceny) {
			Color c = g.getColor();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, wideth, height);
			g.setColor(c);
		}

		// ����ԭͼ
		g.drawImage(this.image, 0, 0, wideth, height, null);

		// ����ˮӡ�ļ�
		int markWidth = markImage.getWidth();
		int markHeight = markImage.getHeight();
		g.drawImage(markImage, wideth - markWidth - right, height - markHeight
				- bottom, markWidth, markHeight, null);
		g.dispose();
		this.image = tagImage;
	}

	public static byte[] read(File file) throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			return read(in);
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static byte[] read(URL url) throws IOException {
		InputStream in = null;
		try {
			in = url.openStream();
			return read(in);
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static byte[] read(InputStream in) throws IOException {
		try {
			byte[] datas = IOUtils.toByteArray(in);
			return datas;
		} catch (IOException e) {
			throw e;
		}
	}

	public static String getFormatName(byte[] imgData) {
		ImageInputStream input = null;
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				imgData);
		try {
			input = ImageIO.createImageInputStream(byteArrayInputStream);
			Iterator<ImageReader> it = ImageIO.getImageReaders(input);
			if (it.hasNext()) {
				ImageReader reader = it.next();
				return reader.getFormatName();
			}
		} catch (Exception e) {
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
			}
			try {
				byteArrayInputStream.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Image parse(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		return new Image(image);
	}

	public static Image parse(URL url) throws IOException {
		InputStream in = null;
		try {
			in = url.openStream();
			BufferedImage image = ImageIO.read(in);
			return new Image(image);
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
		}

	}
	
	/**
	 * ���ֽ�����ͼ��ת��ΪBufferedImage����
	 * 
	 * @param bytes
	 * @return
	 */
	public static BufferedImage toBufferedImage(byte[] bytes) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				bytes);
		try {
			BufferedImage image = ImageIO.read(byteArrayInputStream);
			return image;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				byteArrayInputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ת��ͼ���ָ����ʽ���ֽ���
	 * 
	 * @param image
	 * @param format
	 * @return
	 */
	public static byte[] getBytes(BufferedImage image, String format) {
		if ("JPEG".equals(format.toUpperCase())
				|| "JPG".equals(format.toUpperCase())) {
			return getJPEGBytes(image, DEFAULT_JPEG_QUALITY);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				bos.close();
			} catch (IOException e) {

			}
		}
	}

	/**
	 * ��һ��ͼ�������ת
	 * 
	 * @param image
	 * @param degree
	 * @return
	 */
	public static BufferedImage rotate(BufferedImage image, int degree) {
		int iw = image.getWidth();// ԭʼͼ��Ŀ��
		int ih = image.getHeight();// ԭʼͼ��ĸ߶�
		int w = 0;
		int h = 0;
		int x = 0;
		int y = 0;
		degree = degree % 360;
		if (degree < 0)
			degree = 360 + degree;// ���Ƕ�ת����0-360��֮��
		double ang = degree * 0.0174532925;// ���Ƕ�תΪ����

		/**
		 * ȷ����ת���ͼ��ĸ߶ȺͿ��
		 */

		if (degree == 180 || degree == 0 || degree == 360) {
			w = iw;
			h = ih;
		} else if (degree == 90 || degree == 270) {
			w = ih;
			h = iw;
		} else {
			int d = iw + ih;
			w = (int) (d * Math.abs(Math.cos(ang)));
			h = (int) (d * Math.abs(Math.sin(ang)));
		}

		x = (w / 2) - (iw / 2);// ȷ��ԭ������
		y = (h / 2) - (ih / 2);
		BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
		Graphics gs = rotatedImage.getGraphics();
		gs.fillRect(0, 0, w, h);// �Ը�����ɫ������ת��ͼƬ�ı���
		AffineTransform at = new AffineTransform();
		at.rotate(ang, w / 2, h / 2);// ��תͼ��
		at.translate(x, y);
		AffineTransformOp op = new AffineTransformOp(at,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(image, rotatedImage);
		image = rotatedImage;
		return image;
	}

	/**
	 * �Զ��ȱ�����һ��ͼƬ������Ĳ��֣��ø���������ɫ����
	 * 
	 * @param im
	 * @param w
	 * @param h
	 * @param bgColor
	 * @return
	 */
	public static BufferedImage zoomScale(BufferedImage im, int w, int h,
			Color bgColor) {
		if (w == -1 || h == -1) {
			return zoomScale(im, w, h);
		}

		// ��鱳����ɫ
		bgColor = null == bgColor ? Color.black : bgColor;
		// ��óߴ�
		int oW = im.getWidth();
		int oH = im.getHeight();
		float oR = (float) oW / (float) oH;
		float nR = (float) w / (float) h;

		int nW, nH, x, y;
		/*
		 * ����
		 */
		// ԭͼ̫�����㵱ԭͼ�뻭��ͬ��ʱ��ԭͼ�ĵȱȿ��
		if (oR > nR) {
			nW = w;
			nH = (int) (((float) w) / oR);
			x = 0;
			y = (h - nH) / 2;
		}
		// ԭͼ̫��
		else if (oR < nR) {
			nH = h;
			nW = (int) (((float) h) * oR);
			x = (w - nW) / 2;
			y = 0;
		}
		// ������ͬ
		else {
			nW = w;
			nH = h;
			x = 0;
			y = 0;
		}

		// ����ͼ��
		BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
		// �õ�һ�����ƽӿ�
		Graphics gc = re.getGraphics();
		gc.setColor(bgColor);
		gc.fillRect(0, 0, w, h);
		gc.drawImage(im, x, y, nW, nH, bgColor, null);
		// ����
		return re;
	}

	/**
	 * �Զ��ȱ�����һ��ͼƬ
	 * 
	 * @param im
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage zoomScale(BufferedImage im, int w, int h) {
		// ��óߴ�
		int oW = im.getWidth();
		int oH = im.getHeight();

		int nW = w, nH = h;

		/*
		 * ����
		 */
		// δָ��ͼ��߶ȣ�����ԭͼ�ߴ������߶�
		if (h == -1) {
			nH = (int) ((float) w / oW * oH);
		}
		// δָ��ͼ���ȣ�����ԭͼ�ߴ��������
		else if (w == -1) {
			nW = (int) ((float) h / oH * oW);
		}

		// ����ͼ��
		BufferedImage re = new BufferedImage(nW, nH, ColorSpace.TYPE_RGB);
		re.getGraphics().drawImage(im, 0, 0, nW, nH, null);
		// ����
		return re;
	}

	/**
	 * �Զ����ż���һ��ͼƬ��������ϸ����ĳߴ�
	 * 
	 * @param im
	 * @param w
	 * @param h
	 * @return
	 */
	public static BufferedImage clipScale(BufferedImage im, int w, int h) {
		// ��óߴ�
		int oW = im.getWidth();
		int oH = im.getHeight();
		float oR = (float) oW / (float) oH;
		float nR = (float) w / (float) h;

		int nW, nH, x, y;
		/*
		 * �ü�
		 */
		// ԭͼ̫�����㵱ԭͼ�뻭��ͬ��ʱ��ԭͼ�ĵȱȿ��
		if (oR > nR) {
			nW = (h * oW) / oH;
			nH = h;
			x = (w - nW) / 2;
			y = 0;
		}
		// ԭͼ̫��
		else if (oR < nR) {
			nW = w;
			nH = (w * oH) / oW;
			x = 0;
			y = (h - nH) / 2;
		}
		// ������ͬ
		else {
			nW = w;
			nH = h;
			x = 0;
			y = 0;
		}
		// ����ͼ��
		BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
		re.getGraphics().drawImage(im, x, y, nW, nH, Color.black, null);
		// ����
		return re;
	}

	/**
	 * ת��ͼ��ΪJPEG�ֽ���
	 * 
	 * @param image
	 * @param quality
	 * @return
	 */
	public static byte[] getJPEGBytes(BufferedImage image, float quality) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
			param.setQuality(quality, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(image);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				bos.close();
			} catch (IOException e) {

			}
		}
	}

	/**
	 * ���ͼ����ɫ�ռ�����
	 * 
	 * @param image
	 * @return
	 */
	public static int getColorSpaceType(BufferedImage image) {
		return image.getColorModel().getColorSpace().getType();
	}

	/**
	 * ����JPEG�ļ�
	 * 
	 * @param file
	 * @return
	 * @throws java.io.IOException
	 */
	public static BufferedImage decodeJPEG(File file) throws IOException {
		InputStream input = new FileInputStream(file);
		try {
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(input);
			BufferedImage image = decoder.decodeAsBufferedImage();
			return image;
		} catch (IOException e) {
			throw e;
		} finally {
			input.close();
		}
	}
	
	/**
	 * ����JPEG�ֽ�
	 * @param bytes
	 * @return
	 */
	public static BufferedImage decodeJPEG(byte[] bytes) {
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		try {
			return decodeJPEG(input);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	/**
	 * ����JPEG��
	 * @param input
	 * @return
	 * @throws java.io.IOException
	 */
	public static BufferedImage decodeJPEG(InputStream input)  throws IOException{
		JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(input);
		BufferedImage image = decoder.decodeAsBufferedImage();
		return image;
	}

	/**
	 * CMYK��ȡJPEG4ͼ��
	 * 
	 * @param raster
	 * @return
	 */
	public static BufferedImage cymk2jpeg(Raster raster) {
		int w = raster.getWidth();
		int h = raster.getHeight();
		byte[] rgb = new byte[w * h * 3];

		float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
		float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
		float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
		float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

		for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
			float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

			double val = y + 1.402 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
					: (byte) (val + 0.5);

			val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
					: (byte) (val + 0.5);

			val = y + 1.772 * (cb - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
					: (byte) (val + 0.5);
		}

		raster = Raster.createInterleavedRaster(new DataBufferByte(rgb,
				rgb.length), w, h, w * 3, 3, new int[] { 0, 1, 2 }, null);

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, true,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, (WritableRaster) raster, true, null);
	}

	public static final int TYPE_INT_RGB = 1;

	public static final int TYPE_INT_ARGB = 2;

	public static final int TYPE_INT_ARGB_PRE = 3;

	public static final int TYPE_INT_BGR = 4;

	public static final int TYPE_3BYTE_BGR = 5;

	public static final int TYPE_4BYTE_ABGR = 6;

	public static final int TYPE_4BYTE_ABGR_PRE = 7;

	public static final int TYPE_USHORT_565_RGB = 8;

	public static final int TYPE_USHORT_555_RGB = 9;

	public static final int TYPE_BYTE_GRAY = 10;

	public static final int TYPE_USHORT_GRAY = 11;

	public static final int TYPE_BYTE_BINARY = 12;

	public static final int TYPE_BYTE_INDEXED = 13;

}

package com.lamfire.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.lamfire.logger.Logger;
import com.sun.image.codec.jpeg.*;

@SuppressWarnings("restriction")
public class Image {

    private static final Logger LOGGER               = Logger.getLogger(Image.class.getName());
    public static final float   DEFAULT_JPEG_QUALITY = 0.85f;

    private BufferedImage       image;

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
        if (image == null) return 0;
        return image.getWidth();
    }

    public int getHeight() {
        if (image == null) return 0;
        return image.getHeight();
    }

    /**
     * 栽剪图片
     * 
     * @param w
     * @param h
     */
    public void clip(int w, int h) {
        if (image == null) {
            throw new RuntimeException("image file not be load.please execute 'load' function agin.");
        }

        int iSrcWidth = getWidth(); // 得到源图宽
        int iSrcHeight = getHeight(); // 得到源图长

        // 如果源图片的宽度和高度小于目标图片的宽度或高度，则直接返回原图
        if (iSrcWidth < w && iSrcHeight < h) {
            LOGGER.warn("source image size too small.");
            return;
        }

        int iDstLeft = (iSrcWidth - w) / 2;
        int iDstTop = (iSrcHeight - h) / 2;

        // 剪裁---
        this.image = image.getSubimage(iDstLeft, iDstTop, w, h);

    }

    /**
     * 栽剪图片
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void clip(int x, int y, int w, int h) {
        if (image == null) {
            throw new RuntimeException("image file not be load.please execute 'load' function agin.");
        }

        int iSrcWidth = getWidth(); // 得到源图宽
        int iSrcHeight = getHeight(); // 得到源图长

        // 如果源图片的宽度和高度小于目标图片的宽度或高度，则直接返回原图
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

        // 剪裁---
        this.image = image.getSubimage(x, y, w, h);
    }

    /**
     * 缩放图片到指定大小
     * 
     * @param width
     * @param height
     */
    public void zoomScale(int width, int height) {
        this.image = zoomScale(image, width, height);
    }

    /**
     * 缩放图片并用指定色填充
     * 
     * @param w
     * @param h
     * @param bgColor
     */
    public void zoomScale(int w, int h, Color bgColor) {
        this.image = zoomScale(image, w, h, bgColor);
    }

    /**
     * 将图片等比缩小到固定宽度,如果原图较小则不缩放.
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
     * 将图片等比缩小到固定宽度,如果原图较小则不缩放.
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
     * 将图片缩放剪切到固定尺寸
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
     * 绘制水印图象
     * 
     * @param markImage
     * @param right
     * @param bottom
     * @param alpha
     */
    public void drawMarkImage(BufferedImage markImage, int right, int bottom, int colorType, boolean transluceny) {
        // 新图像
        int wideth = this.image.getWidth();
        int height = this.image.getHeight();

        BufferedImage tagImage = new BufferedImage(wideth, height, colorType);

        Graphics g = tagImage.createGraphics();

        // 如果为不透明，则绘制白色底
        if (!transluceny) {
            Color c = g.getColor();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, wideth, height);
            g.setColor(c);
        }

        // 绘制原图
        g.drawImage(this.image, 0, 0, wideth, height, null);

        // 绘制水印文件
        int markWidth = markImage.getWidth();
        int markHeight = markImage.getHeight();
        g.drawImage(markImage, wideth - markWidth - right, height - markHeight - bottom, markWidth, markHeight, null);
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
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imgData);
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
     * 将字节数组图象转换为BufferedImage对象
     * 
     * @param bytes
     * @return
     */
    public static BufferedImage toBufferedImage(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
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
     * 转换图象成指定格式的字节流
     * 
     * @param image
     * @param format
     * @return
     */
    public static byte[] getBytes(BufferedImage image, String format) {
        if ("JPEG".equals(format.toUpperCase()) || "JPG".equals(format.toUpperCase())) {
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
     * 对一个图像进行旋转
     * 
     * @param image
     * @param degree
     * @return
     */
    public static BufferedImage rotate(BufferedImage image, int degree) {
        int iw = image.getWidth();// 原始图象的宽度
        int ih = image.getHeight();// 原始图象的高度
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        degree = degree % 360;
        if (degree < 0) degree = 360 + degree;// 将角度转换到0-360度之间
        double ang = degree * 0.0174532925;// 将角度转为弧度

        /**
         * 确定旋转后的图象的高度和宽度
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

        x = (w / 2) - (iw / 2);// 确定原点坐标
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        Graphics gs = rotatedImage.getGraphics();
        gs.fillRect(0, 0, w, h);// 以给定颜色绘制旋转后图片的背景
        AffineTransform at = new AffineTransform();
        at.rotate(ang, w / 2, h / 2);// 旋转图象
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        op.filter(image, rotatedImage);
        image = rotatedImage;
        return image;
    }

    /**
     * 自动等比缩放一个图片，多余的部分，用给定背景颜色补上
     * 
     * @param im
     * @param w
     * @param h
     * @param bgColor
     * @return
     */
    public static BufferedImage zoomScale(BufferedImage im, int w, int h, Color bgColor) {
        if (w == -1 || h == -1) {
            return zoomScale(im, w, h);
        }

        // 检查背景颜色
        bgColor = null == bgColor ? Color.black : bgColor;
        // 获得尺寸
        int oW = im.getWidth();
        int oH = im.getHeight();
        float oR = (float) oW / (float) oH;
        float nR = (float) w / (float) h;

        int nW, nH, x, y;
        /*
         * 缩放
         */
        // 原图太宽，计算当原图与画布同高时，原图的等比宽度
        if (oR > nR) {
            nW = w;
            nH = (int) (((float) w) / oR);
            x = 0;
            y = (h - nH) / 2;
        }
        // 原图太长
        else if (oR < nR) {
            nH = h;
            nW = (int) (((float) h) * oR);
            x = (w - nW) / 2;
            y = 0;
        }
        // 比例相同
        else {
            nW = w;
            nH = h;
            x = 0;
            y = 0;
        }

        // 创建图像
        BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
        // 得到一个绘制接口
        Graphics gc = re.getGraphics();
        gc.setColor(bgColor);
        gc.fillRect(0, 0, w, h);
        gc.drawImage(im, x, y, nW, nH, bgColor, null);
        // 返回
        return re;
    }

    /**
     * 自动等比缩放一个图片
     * 
     * @param im
     * @param w
     * @param h
     * @return
     */
    public static BufferedImage zoomScale(BufferedImage im, int w, int h) {
        // 获得尺寸
        int oW = im.getWidth();
        int oH = im.getHeight();

        int nW = w, nH = h;

        /*
         * 缩放
         */
        // 未指定图像高度，根据原图尺寸计算出高度
        if (h == -1) {
            nH = (int) ((float) w / oW * oH);
        }
        // 未指定图像宽度，根据原图尺寸计算出宽度
        else if (w == -1) {
            nW = (int) ((float) h / oH * oW);
        }

        // 创建图像
        BufferedImage re = new BufferedImage(nW, nH, ColorSpace.TYPE_RGB);
        re.getGraphics().drawImage(im, 0, 0, nW, nH, null);
        // 返回
        return re;
    }

    /**
     * 自动缩放剪切一个图片，令其符合给定的尺寸
     * 
     * @param im
     * @param w
     * @param h
     * @return
     */
    public static BufferedImage clipScale(BufferedImage im, int w, int h) {
        // 获得尺寸
        int oW = im.getWidth();
        int oH = im.getHeight();
        float oR = (float) oW / (float) oH;
        float nR = (float) w / (float) h;

        int nW, nH, x, y;
        /*
         * 裁减
         */
        // 原图太宽，计算当原图与画布同高时，原图的等比宽度
        if (oR > nR) {
            nW = (h * oW) / oH;
            nH = h;
            x = (w - nW) / 2;
            y = 0;
        }
        // 原图太长
        else if (oR < nR) {
            nW = w;
            nH = (w * oH) / oW;
            x = 0;
            y = (h - nH) / 2;
        }
        // 比例相同
        else {
            nW = w;
            nH = h;
            x = 0;
            y = 0;
        }
        // 创建图像
        BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
        re.getGraphics().drawImage(im, x, y, nW, nH, Color.black, null);
        // 返回
        return re;
    }

    /**
     * 转换图像为JPEG字节流
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
     * 获得图像颜色空间类型
     * 
     * @param image
     * @return
     */
    public static int getColorSpaceType(BufferedImage image) {
        return image.getColorModel().getColorSpace().getType();
    }

    /**
     * 解码JPEG文件
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
     * 解码JPEG字节
     * 
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
     * 解码JPEG流
     * 
     * @param input
     * @return
     * @throws java.io.IOException
     */
    public static BufferedImage decodeJPEG(InputStream input) throws IOException {
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(input);
        BufferedImage image = decoder.decodeAsBufferedImage();
        return image;
    }

    /**
     * CMYK获取JPEG4图像
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
            rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

            val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

            val = y + 1.772 * (cb - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
        }

        raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3, new int[] { 0, 1,
                2 }, null);

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }

    public static final int TYPE_INT_RGB        = 1;

    public static final int TYPE_INT_ARGB       = 2;

    public static final int TYPE_INT_ARGB_PRE   = 3;

    public static final int TYPE_INT_BGR        = 4;

    public static final int TYPE_3BYTE_BGR      = 5;

    public static final int TYPE_4BYTE_ABGR     = 6;

    public static final int TYPE_4BYTE_ABGR_PRE = 7;

    public static final int TYPE_USHORT_565_RGB = 8;

    public static final int TYPE_USHORT_555_RGB = 9;

    public static final int TYPE_BYTE_GRAY      = 10;

    public static final int TYPE_USHORT_GRAY    = 11;

    public static final int TYPE_BYTE_BINARY    = 12;

    public static final int TYPE_BYTE_INDEXED   = 13;

}

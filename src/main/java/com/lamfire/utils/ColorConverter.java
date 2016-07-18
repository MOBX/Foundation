package com.lamfire.utils;

import java.awt.Color;

public class ColorConverter {

    // RGB转换CMYk
    public int[] rgb2cmyk(int R, int G, int B) {
        int cmyk[] = new int[4];
        cmyk[3] = (int) (Math.min(Math.min(255 - R, 255 - G), 255 - B) / 2.55);// cmykK
        int MyR = (int) (R / 2.55);
        int Div = 100 - cmyk[3];
        if (Div == 0) Div = 1;
        cmyk[0] = ((100 - MyR - cmyk[3]) / Div) * 100;// cmykC
        int MyG = (int) (G / 2.55);
        cmyk[1] = ((100 - MyG - cmyk[3]) / Div) * 100;
        int MyB = (int) (B / 2.55);
        cmyk[2] = ((100 - MyB - cmyk[3]) / Div) * 100;

        return cmyk;
    }

    public Color cmyk2rgb(int C, int M, int Y, int K) {
        float MyC = C / 100;
        float MyM = M / 100;
        float MyY = Y / 100;
        float MyK = K / 100;

        int R = (int) ((1 - (MyC * (1 - MyK) + MyK)) * 255);
        int G = (int) ((1 - (MyM * (1 - MyK) + MyK)) * 255);
        int B = (int) ((1 - (MyY * (1 - MyK) + MyK)) * 255);

        if (R < 0) R = 0;
        if (G < 0) G = 0;
        if (B < 0) B = 0;
        if (R > 255) R = 255;
        if (G > 255) G = 255;
        if (B > 255) B = 255;

        Color rgb = new Color(R, G, B);
        return rgb;
    }

    public int[] rgb2yuv(int R, int G, int B) {

        int yuv[] = new int[3];
        yuv[0] = (int) (0.299 * R + 0.587 * G + 0.114 * B);
        yuv[1] = (int) (-0.147 * R - 0.289 * G + 0.437 * B);
        yuv[2] = (int) (0.615 * R - 0.515 * G - 0.1 * B);
        return yuv;
    }

    public Color yuv2rgb(int y, int u, int v) {

        int R = (int) (y + 1.14 * v);
        int G = (int) (y - 0.394 * u - 0.581 * v);
        int B = (int) (y + 2.028 * u);
        if (R < 0) R = 0;
        if (G < 0) G = 0;
        if (B < 0) B = 0;
        if (R > 255) R = 255;
        if (G > 255) G = 255;
        if (B > 255) B = 255;

        Color rgb = new Color(R, G, B);
        return rgb;
    }

    public int[] rgb2yiq(int R, int G, int B) {

        int yiq[] = new int[3];
        yiq[0] = (int) (0.299 * R + 0.587 * G + 0.114 * B);
        yiq[1] = (int) (0.596 * R - 0.274 * G - 0.322 * B);
        yiq[2] = (int) (0.212 * R - 0.523 * G + 0.311 * B);
        return yiq;
    }

    public Color yiq2rgb(float y, float i, float q) {

        int R = (int) (y + 0.956 * i + 0.621 * q);
        int G = (int) (y - 0.272 * i - 0.647 * q);
        int B = (int) (y - 1.105 * i + 1.702 * q);

        if (R < 0) R = 0;
        if (G < 0) G = 0;
        if (B < 0) B = 0;
        if (R > 255) R = 255;
        if (G > 255) G = 255;
        if (B > 255) B = 255;

        Color rgb = new Color(R, G, B);
        return rgb;
    }

    // RGB转换YCbCr
    public int[] rgb2ycbcr(int R, int G, int B) {

        int ycbcr[] = new int[3];
        ycbcr[0] = (int) (0.299 * R + 0.587 * G + 0.114 * B);
        ycbcr[1] = (int) (-0.1687 * R - 0.3313 * G + 0.5 * B + 128);
        if (ycbcr[1] > 255) ycbcr[1] = 255;
        ycbcr[2] = (int) (0.5 * R - 0.4187 * G - 0.0813 * B + 128);
        if (ycbcr[2] > 255) ycbcr[2] = 255;

        return ycbcr;
    }

    public Color ycbcr2rgb(int Y, int Cb, int Cr) {
        int MyR = (int) (Y + 1.402 * (Cr - 128));
        int MyG = (int) (Y - 0.34414 * (Cb - 128) - 0.71414 * (Cr - 128));
        int MyB = (int) (Y + 1.772 * (Cb - 128));

        if (MyR > 255) MyR = 255;
        if (MyG > 255) MyG = 255;
        if (MyB > 255) MyB = 255;

        if (MyR < 0) MyR = 0;
        if (MyG < 0) MyG = 0;
        if (MyB < 0) MyB = 0;

        Color rgb = new Color(MyR, MyG, MyB);
        return rgb;
    }
}

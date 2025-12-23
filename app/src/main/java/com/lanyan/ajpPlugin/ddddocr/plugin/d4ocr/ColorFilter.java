package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 颜色过滤器 - 用于提取特定颜色的文字
 */
public class ColorFilter {
    private static final String TAG = "ColorFilter";
    
    static {
        try {
            System.loadLibrary("opencv_java4");
        } catch (Exception e) {
            Log.m18e(TAG + " OpenCV 加载失败", e);
        }
    }
    
    // HSV颜色预设
    private static final Map<String, int[][]> COLOR_PRESETS = new HashMap<>();
    
    static {
        // 红色 (两个范围，因为红色在HSV色轮的两端)
        COLOR_PRESETS.put("red", new int[][]{{0, 50, 50, 10, 255, 255}, {170, 50, 50, 180, 255, 255}});
        // 蓝色
        COLOR_PRESETS.put("blue", new int[][]{{100, 50, 50, 130, 255, 255}});
        // 绿色
        COLOR_PRESETS.put("green", new int[][]{{40, 50, 50, 80, 255, 255}});
        // 黄色
        COLOR_PRESETS.put("yellow", new int[][]{{20, 50, 50, 40, 255, 255}});
        // 橙色
        COLOR_PRESETS.put("orange", new int[][]{{10, 50, 50, 25, 255, 255}});
        // 紫色
        COLOR_PRESETS.put("purple", new int[][]{{130, 50, 50, 160, 255, 255}});
        // 青色
        COLOR_PRESETS.put("cyan", new int[][]{{80, 50, 50, 100, 255, 255}});
        // 黑色
        COLOR_PRESETS.put("black", new int[][]{{0, 0, 0, 180, 255, 50}});
        // 白色
        COLOR_PRESETS.put("white", new int[][]{{0, 0, 200, 180, 30, 255}});
        // 灰色
        COLOR_PRESETS.put("gray", new int[][]{{0, 0, 50, 180, 30, 200}});
    }

    /**
     * 根据颜色名称过滤图像
     * @param bitmap 输入图像
     * @param colorNames 颜色名称列表，如 ["red", "blue"]
     * @return 过滤后的图像
     */
    public static Bitmap filterByColorNames(Bitmap bitmap, String[] colorNames) {
        if (bitmap == null || colorNames == null || colorNames.length == 0) {
            return bitmap;
        }

        try {
            Mat src = bitmapToMat(bitmap);
            
            // 转换到HSV色彩空间
            Mat hsv = new Mat();
            Imgproc.cvtColor(src, hsv, Imgproc.COLOR_RGB2HSV);
            
            // 创建总的掩码
            Mat totalMask = Mat.zeros(hsv.size(), CvType.CV_8UC1);
            
            // 对每个颜色创建掩码并合并
            for (String colorName : colorNames) {
                int[][] ranges = COLOR_PRESETS.get(colorName.toLowerCase());
                if (ranges != null) {
                    for (int[] range : ranges) {
                        Mat mask = new Mat();
                        Scalar lower = new Scalar(range[0], range[1], range[2]);
                        Scalar upper = new Scalar(range[3], range[4], range[5]);
                        Core.inRange(hsv, lower, upper, mask);
                        Core.bitwise_or(totalMask, mask, totalMask);
                        mask.release();
                    }
                }
            }
            
            // 应用掩码
            Mat result = new Mat();
            Core.bitwise_and(src, src, result, totalMask);
            
            // 将未匹配的区域设为白色
            Mat invMask = new Mat();
            Core.bitwise_not(totalMask, invMask);
            Mat white = new Mat(src.size(), src.type(), new Scalar(255, 255, 255));
            Mat whiteBg = new Mat();
            Core.bitwise_and(white, white, whiteBg, invMask);
            Core.add(result, whiteBg, result);
            
            // 转换回Bitmap
            Bitmap resultBitmap = matToBitmap(result);
            
            // 释放资源
            src.release();
            hsv.release();
            totalMask.release();
            result.release();
            invMask.release();
            white.release();
            whiteBg.release();
            
            return resultBitmap;
            
        } catch (Exception e) {
            Log.m18e(TAG + " 颜色过滤失败: " + e.getMessage(), e);
            return bitmap;
        }
    }

    /**
     * 根据自定义HSV范围过滤图像
     * @param bitmap 输入图像
     * @param customRanges 自定义HSV范围 [[h_min, s_min, v_min, h_max, s_max, v_max], ...]
     * @return 过滤后的图像
     */
    public static Bitmap filterByCustomRanges(Bitmap bitmap, int[][] customRanges) {
        if (bitmap == null || customRanges == null || customRanges.length == 0) {
            return bitmap;
        }

        try {
            Mat src = bitmapToMat(bitmap);
            
            Mat hsv = new Mat();
            Imgproc.cvtColor(src, hsv, Imgproc.COLOR_RGB2HSV);
            
            Mat totalMask = Mat.zeros(hsv.size(), CvType.CV_8UC1);
            
            for (int[] range : customRanges) {
                if (range.length >= 6) {
                    Mat mask = new Mat();
                    Scalar lower = new Scalar(range[0], range[1], range[2]);
                    Scalar upper = new Scalar(range[3], range[4], range[5]);
                    Core.inRange(hsv, lower, upper, mask);
                    Core.bitwise_or(totalMask, mask, totalMask);
                    mask.release();
                }
            }
            
            Mat result = new Mat();
            Core.bitwise_and(src, src, result, totalMask);
            
            Mat invMask = new Mat();
            Core.bitwise_not(totalMask, invMask);
            Mat white = new Mat(src.size(), src.type(), new Scalar(255, 255, 255));
            Mat whiteBg = new Mat();
            Core.bitwise_and(white, white, whiteBg, invMask);
            Core.add(result, whiteBg, result);
            
            Bitmap resultBitmap = matToBitmap(result);
            
            // 释放资源
            src.release();
            hsv.release();
            totalMask.release();
            result.release();
            invMask.release();
            white.release();
            whiteBg.release();
            
            return resultBitmap;
            
        } catch (Exception e) {
            Log.m18e(TAG + " 自定义颜色过滤失败: " + e.getMessage(), e);
            return bitmap;
        }
    }

    /**
     * 获取所有可用的颜色预设名称
     */
    public static String[] getAvailableColors() {
        return COLOR_PRESETS.keySet().toArray(new String[0]);
    }
    
    private static Mat bitmapToMat(Bitmap bitmap) {
        // 手动将 Bitmap 转换为 Mat
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Mat mat = new Mat(bmp32.getHeight(), bmp32.getWidth(), CvType.CV_8UC4);
        
        int[] pixels = new int[bmp32.getWidth() * bmp32.getHeight()];
        bmp32.getPixels(pixels, 0, bmp32.getWidth(), 0, 0, bmp32.getWidth(), bmp32.getHeight());
        
        byte[] data = new byte[pixels.length * 4];
        for (int i = 0; i < pixels.length; i++) {
            data[i * 4] = (byte) ((pixels[i] >> 16) & 0xFF); // R
            data[i * 4 + 1] = (byte) ((pixels[i] >> 8) & 0xFF); // G
            data[i * 4 + 2] = (byte) (pixels[i] & 0xFF); // B
            data[i * 4 + 3] = (byte) ((pixels[i] >> 24) & 0xFF); // A
        }
        
        mat.put(0, 0, data);
        return mat;
    }
    
    private static Bitmap matToBitmap(Mat mat) {
        // 手动将 Mat 转换为 Bitmap
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        
        byte[] data = new byte[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, data);
        
        int[] pixels = new int[mat.cols() * mat.rows()];
        for (int i = 0; i < pixels.length; i++) {
            int r = data[i * 4] & 0xFF;
            int g = data[i * 4 + 1] & 0xFF;
            int b = data[i * 4 + 2] & 0xFF;
            int a = data[i * 4 + 3] & 0xFF;
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        
        bitmap.setPixels(pixels, 0, mat.cols(), 0, 0, mat.cols(), mat.rows());
        return bitmap;
    }
}

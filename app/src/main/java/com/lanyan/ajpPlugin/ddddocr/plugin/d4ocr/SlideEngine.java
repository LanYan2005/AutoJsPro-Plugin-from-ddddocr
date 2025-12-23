package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑块匹配引擎 - 用于滑块验证码识别
 */
public class SlideEngine {
    private static final String TAG = "SlideEngine";
    
    static {
        try {
            System.loadLibrary("opencv_java4");
        } catch (Exception e) {
            Log.m18e(TAG + " OpenCV 加载失败", e);
        }
    }

    /**
     * 滑块匹配算法1 - 通过模板匹配找到滑块位置
     * @param targetInput 滑块图像（透明背景）
     * @param backgroundInput 背景图像
     * @param simpleTarget 是否为简单目标（无透明背景）
     * @return 匹配位置的x坐标，失败返回-1
     */
    public int slideMatch(String targetInput, String backgroundInput, boolean simpleTarget) {
        try {
            Bitmap targetBitmap = decodeImage(targetInput);
            Bitmap backgroundBitmap = decodeImage(backgroundInput);
            
            if (targetBitmap == null || backgroundBitmap == null) {
                Log.m16d(TAG, "图像解码失败");
                return -1;
            }

            Mat target = bitmapToMat(targetBitmap);
            Mat background = bitmapToMat(backgroundBitmap);

            // 转换为灰度图
            Mat targetGray = new Mat();
            Mat backgroundGray = new Mat();
            Imgproc.cvtColor(target, targetGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(background, backgroundGray, Imgproc.COLOR_BGR2GRAY);

            int result;
            if (simpleTarget) {
                // 简单模板匹配
                result = simpleTemplateMatch(targetGray, backgroundGray);
            } else {
                // 边缘检测匹配（适用于透明背景滑块）
                result = edgeBasedMatch(targetGray, backgroundGray);
            }
            
            // 释放资源
            target.release();
            background.release();
            targetGray.release();
            backgroundGray.release();
            
            return result;
            
        } catch (Exception e) {
            Log.m18e(TAG + " 滑块匹配异常: " + e.getMessage(), e);
            return -1;
        }
    }

    /**
     * 滑块比较算法2 - 通过比较两张图的差异找到缺口位置
     * @param targetInput 带缺口的图像
     * @param backgroundInput 完整的背景图像
     * @return 缺口位置的x坐标，失败返回-1
     */
    public int slideComparison(String targetInput, String backgroundInput) {
        try {
            Bitmap targetBitmap = decodeImage(targetInput);
            Bitmap backgroundBitmap = decodeImage(backgroundInput);
            
            if (targetBitmap == null || backgroundBitmap == null) {
                Log.m16d(TAG, "图像解码失败");
                return -1;
            }

            Mat target = bitmapToMat(targetBitmap);
            Mat background = bitmapToMat(backgroundBitmap);

            // 转换为灰度图
            Mat targetGray = new Mat();
            Mat backgroundGray = new Mat();
            Imgproc.cvtColor(target, targetGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(background, backgroundGray, Imgproc.COLOR_BGR2GRAY);

            // 计算差异
            Mat diff = new Mat();
            Core.absdiff(targetGray, backgroundGray, diff);

            // 二值化
            Mat binary = new Mat();
            Imgproc.threshold(diff, binary, 30, 255, Imgproc.THRESH_BINARY);

            // 形态学操作去噪
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(5, 5));
            Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel);

            // 查找轮廓
            List<org.opencv.core.MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // 找到最大轮廓的位置
            double maxArea = 0;
            int targetX = -1;
            
            for (org.opencv.core.MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea && area > 100) {
                    maxArea = area;
                    org.opencv.core.Rect rect = Imgproc.boundingRect(contour);
                    targetX = rect.x;
                }
            }

            // 释放资源
            target.release();
            background.release();
            targetGray.release();
            backgroundGray.release();
            diff.release();
            binary.release();
            kernel.release();
            hierarchy.release();
            for (org.opencv.core.MatOfPoint contour : contours) {
                contour.release();
            }

            return targetX;
            
        } catch (Exception e) {
            Log.m18e(TAG + " 滑块比较异常: " + e.getMessage(), e);
            return -1;
        }
    }

    private int simpleTemplateMatch(Mat target, Mat background) {
        Mat result = new Mat();
        Imgproc.matchTemplate(background, target, result, Imgproc.TM_CCOEFF_NORMED);
        
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;
        
        result.release();
        return (int) matchLoc.x;
    }

    private int edgeBasedMatch(Mat target, Mat background) {
        // 边缘检测
        Mat targetEdges = new Mat();
        Mat backgroundEdges = new Mat();
        
        Imgproc.Canny(target, targetEdges, 50, 150);
        Imgproc.Canny(background, backgroundEdges, 50, 150);

        // 模板匹配
        Mat result = new Mat();
        Imgproc.matchTemplate(backgroundEdges, targetEdges, result, Imgproc.TM_CCOEFF_NORMED);
        
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;
        
        targetEdges.release();
        backgroundEdges.release();
        result.release();
        
        return (int) matchLoc.x;
    }

    private Bitmap decodeImage(String input) {
        try {
            if (input == null) return null;
            
            if (input.matches("^[A-Za-z0-9+/]+={0,2}$") || input.contains("data:image")) {
                String base64Image = input.contains(",") ? input.split(",")[1] : input;
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } else {
                return BitmapFactory.decodeFile(input);
            }
        } catch (Exception e) {
            Log.m18e(TAG + " 图像解码失败", e);
            return null;
        }
    }
    
    private Mat bitmapToMat(Bitmap bitmap) {
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
}

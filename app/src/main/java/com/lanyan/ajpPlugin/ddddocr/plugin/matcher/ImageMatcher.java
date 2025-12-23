package com.lanyan.ajpPlugin.ddddocr.plugin.matcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ImageMatcher {
    static {
        System.loadLibrary("opencv_java4");
    }

    public String findImage(String largeImagePath, String smallImagePath) {
        try {
            // 读取图片
            Bitmap largeBitmap = BitmapFactory.decodeFile(largeImagePath);
            Bitmap smallBitmap = BitmapFactory.decodeFile(smallImagePath);

            // 转换为Mat，保留alpha通道
            Mat largeMat = new Mat();
            Mat smallMat = new Mat();
            
            // 将图像转换为RGBA格式
            Imgproc.cvtColor(largeMat, largeMat, Imgproc.COLOR_BGRA2RGBA);
            Imgproc.cvtColor(smallMat, smallMat, Imgproc.COLOR_BGRA2RGBA);

            // 创建结果矩阵
            Mat result = new Mat();
            int resultCols = largeMat.cols() - smallMat.cols() + 1;
            int resultRows = largeMat.rows() - smallMat.rows() + 1;

            if (resultCols <= 0 || resultRows <= 0) {
                return "小图尺寸大于大图，无法进行匹配";
            }

            result.create(resultRows, resultCols, CvType.CV_32FC1);

            // 分离通道
            java.util.List<Mat> largeChannels = new java.util.ArrayList<>();
            java.util.List<Mat> smallChannels = new java.util.ArrayList<>();
            Core.split(largeMat, largeChannels);
            Core.split(smallMat, smallChannels);
            
            // 使用alpha通道作为mask进行模板匹配
            Mat mask = smallChannels.get(3);
            Mat resultRGB = new Mat();
            Imgproc.matchTemplate(largeMat, smallMat, resultRGB, Imgproc.TM_CCOEFF_NORMED, mask);
            
            // 释放通道资源
            for(Mat channel : largeChannels) channel.release();
            for(Mat channel : smallChannels) channel.release();
            mask.release();
            
            // 将结果复制到最终结果矩阵
            resultRGB.copyTo(result);
            resultRGB.release();

            // 找到最佳匹配位置
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            Point matchLoc = mmr.maxLoc;

            // 清理资源
            largeMat.release();
            smallMat.release();
            result.release();
            largeBitmap.recycle();
            smallBitmap.recycle();

            // 返回匹配位置的坐标
            return String.format("{\"x\":%d,\"y\":%d}", (int)matchLoc.x, (int)matchLoc.y);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtSession;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.ImageUtils;
import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.Log;
import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.ONNXRuntimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 目标检测引擎 - 用于检测图像中的目标位置
 */
public class DetectionEngine {
    private static final String TAG = "DetectionEngine";
    private static File detModelFile;

    public static void setModel(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            
            // 检查目标检测模型是否存在
            try {
                InputStream testStream = assetManager.open("common_det.onnx");
                testStream.close();
                
                // 模型存在，加载它
                InputStream modelStream = assetManager.open("common_det.onnx");
                byte[] modelBytes = new byte[modelStream.available()];
                modelStream.read(modelBytes);
                modelStream.close();

                // 创建临时文件
                File tempFile = File.createTempFile("det_model", ".onnx");
                tempFile.deleteOnExit();

                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(modelBytes);
                fos.close();

                detModelFile = tempFile;
                Log.m16d(TAG, "目标检测模型加载成功");
                
            } catch (Exception e) {
                Log.m16d(TAG, "目标检测模型不存在，功能将被禁用");
                detModelFile = null;
            }
            
        } catch (Exception e) {
            Log.m18e(TAG + " 模型配置异常: " + e.getMessage(), e);
            detModelFile = null;
        }
    }

    /**
     * 检测图像中的目标位置
     * @param input 图像路径、base64或文件路径
     * @return 检测到的边界框列表 [[x1,y1,x2,y2], ...]
     */
    public List<int[]> detection(String input) {
        List<int[]> results = new ArrayList<>();
        
        if (input == null) {
            Log.m16d(TAG, "检测输入不能为空");
            return results;
        }
        
        if (detModelFile == null || !detModelFile.exists()) {
            Log.m16d(TAG, "检测模型未加载");
            return results;
        }

        try {
            // 解码图像
            Bitmap bitmap = decodeImage(input);
            if (bitmap == null) {
                Log.m16d(TAG, "图像解码失败");
                return results;
            }

            // 预处理图像
            Bitmap resized = ImageUtils.resize(bitmap, 416, 416);
            float[] inputData = preprocessImage(resized);
            long[] shape = {1, 3, 416, 416};

            // 运行推理
            ONNXRuntimeUtils runtimeUtils = new ONNXRuntimeUtils();
            OnnxTensor inputTensor = runtimeUtils.createTensor(inputData, shape);
            OrtSession session = runtimeUtils.createSession(detModelFile.getAbsolutePath());
            
            Map<String, OnnxTensor> inputs = OCREngine1.m13m(
                new Map.Entry[]{new AbstractMap.SimpleEntry("images", inputTensor)}
            );
            
            OrtSession.Result output = session.run(inputs);
            float[][][] detections = (float[][][]) ((OnnxTensor) output.get(0)).getValue();

            // 后处理检测结果
            results = postprocessDetections(detections, bitmap.getWidth(), bitmap.getHeight());
            
            session.close();
            inputTensor.close();
            
        } catch (Exception e) {
            Log.m18e(TAG + " 检测异常: " + e.getMessage(), e);
        }

        return results;
    }

    private Bitmap decodeImage(String input) {
        try {
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

    private float[] preprocessImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[] result = new float[3 * width * height];
        
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        
        // 转换为CHW格式并归一化
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[i * width + j];
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                
                result[i * width + j] = r / 255.0f;
                result[width * height + i * width + j] = g / 255.0f;
                result[2 * width * height + i * width + j] = b / 255.0f;
            }
        }
        
        return result;
    }

    private List<int[]> postprocessDetections(float[][][] detections, int origWidth, int origHeight) {
        List<int[]> boxes = new ArrayList<>();
        float confThreshold = 0.5f;
        
        for (float[][] detection : detections) {
            for (float[] det : detection) {
                if (det.length >= 5 && det[4] > confThreshold) {
                    float x = det[0] * origWidth / 416;
                    float y = det[1] * origHeight / 416;
                    float w = det[2] * origWidth / 416;
                    float h = det[3] * origHeight / 416;
                    
                    int x1 = Math.max(0, (int)(x - w/2));
                    int y1 = Math.max(0, (int)(y - h/2));
                    int x2 = Math.min(origWidth, (int)(x + w/2));
                    int y2 = Math.min(origHeight, (int)(y + h/2));
                    
                    boxes.add(new int[]{x1, y1, x2, y2});
                }
            }
        }
        
        return boxes;
    }
}

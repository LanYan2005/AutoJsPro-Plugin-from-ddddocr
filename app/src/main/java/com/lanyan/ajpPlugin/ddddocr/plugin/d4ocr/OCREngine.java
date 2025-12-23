package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtSession;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Picture;
import android.util.Base64;

import com.caverock.androidsvg.SVG;
import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.ImageUtils;
import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.Log;
import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.ONNXRuntimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Map;
import org.json.JSONArray;

public class OCREngine {
    private static final String TAG = "OCREngineOldImpl";
    private static JSONArray charsetArray;
    private static File modelFile;

    public static void setModel(Context context) {
        try {
            AssetManager assetManager = context.getAssets();

            // 加载字符集配置
            InputStream charsetStream = assetManager.open("common_old_charset.json");
            byte[] bArr = new byte[charsetStream.available()];
            charsetStream.read(bArr);
            charsetStream.close();
            charsetArray = new JSONArray(new String(bArr, "UTF-8"));

            // 加载模型文件
            InputStream modelStream = assetManager.open("common_old.onnx");
            byte[] modelBytes = new byte[modelStream.available()];
            modelStream.read(modelBytes);
            modelStream.close();

            // 创建临时文件用于ONNX运行时加载
            File tempFile = File.createTempFile("onnx_model", ".onnx");
            tempFile.deleteOnExit(); // 确保程序退出时删除临时文件

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(modelBytes);
            fos.close();

            modelFile = tempFile;
        } catch (Exception e) {
            Log.m18e(TAG + "模型配置加载异常: " + e.getMessage(), e);
            // 确保在发生异常时清理可能部分创建的文件
            if (modelFile != null && modelFile.exists()) {
                modelFile.delete();
            }
            modelFile = null;
            charsetArray = null;
        }
    }

    public String recognize(String input) {
        return recognize(input, null, null, "auto");
    }
    
    public String recognize(String input, String ocrType) {
        return recognize(input, null, null, ocrType);
    }

    /**
     * 带颜色过滤的OCR识别
     * @param input 图像输入
     * @param colorFilterColors 颜色过滤名称数组，如 ["red", "blue"]
     * @param colorFilterCustomRanges 自定义HSV范围
     * @return 识别结果
     */
    public String recognize(String input, String[] colorFilterColors, int[][] colorFilterCustomRanges) {
        return recognize(input, colorFilterColors, colorFilterCustomRanges, "auto");
    }

    /**
     * 完整的OCR识别方法
     * @param input 图像输入
     * @param colorFilterColors 颜色过滤名称数组，如 ["red", "blue"]
     * @param colorFilterCustomRanges 自定义HSV范围
     * @param ocrType OCR类型：
     *                "auto" - 自动识别（默认）
     *                "number" - 纯数字
     *                "letter" - 纯字母
     *                "alphanumeric" - 英数混合
     *                "chinese" - 中文
     *                "math" - 数学计算表达式
     * @return 识别结果
     */
    public String recognize(String input, String[] colorFilterColors, int[][] colorFilterCustomRanges, String ocrType) {
        Map<String, OnnxTensor> m13m;
        if (input == null) {
            Log.m16d(TAG, "OCR输入不能为空");
            return "OCR输入不能为空";
        }
        File file = modelFile;
        if (file == null || !file.exists() || charsetArray == null) {
            Log.m16d(TAG, "OCR模型配置缺失");
            return "OCR模型配置缺失";
        }
        // 处理图像输入
        Bitmap decodeFile = null;
        try {
            if (input.startsWith("<svg") || input.contains("<svg")) {
                // 处理SVG格式字符串
                try {
                    SVG svg = SVG.getFromString(input);
                    if (svg == null) {
                        return "SVG对象创建失败";
                    }
                    Picture picture = svg.renderToPicture();
                    float width = picture.getWidth();
                    float height = picture.getHeight();

                    // 创建位图并设置合适的配置
                    decodeFile = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(decodeFile);
                    canvas.drawPicture(picture);

                    // 确保位图不为空且有效
                    if (decodeFile.getWidth() <= 0 || decodeFile.getHeight() <= 0) {
                        return "生成的位图无效";
                    }
                } catch (Exception e) {
                    Log.m16d(TAG, "SVG解析失败: " + e.getMessage());
                    return "SVG解析失败: " + e.getMessage();
                }
            } else if (input.matches("^[A-Za-z0-9+/]+={0,2}$")) {
                // 处理base64编码的图片
                String base64Image = input;
                if (input.contains(",")) {
                    base64Image = input.split(",")[1];
                }
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                if(input.startsWith("data:image/gif")) {
                    Movie movie = Movie.decodeByteArray(decodedString, 0, decodedString.length);
                    if (movie != null) {
                        // 创建位图来存储第一帧
                        decodeFile = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(decodeFile);
                        // 设置时间为0以获取第一帧
                        movie.setTime(0);
                        // 将第一帧绘制到位图上
                        movie.draw(canvas, 0, 0);
                    }
                } else {
                    decodeFile = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                }
            } else {
                    decodeFile = BitmapFactory.decodeFile(input);
            }
            if (decodeFile == null) {
                Log.m16d(TAG, "图像解码失败");
                return "图像解码失败";
            }
        } catch (Exception e) {
            Log.m16d(TAG, "图像解码失败: " + e.getMessage());
            return "图像解码失败";
        }
        
        // 应用颜色过滤
        if (colorFilterColors != null && colorFilterColors.length > 0) {
            decodeFile = ColorFilter.filterByColorNames(decodeFile, colorFilterColors);
        } else if (colorFilterCustomRanges != null && colorFilterCustomRanges.length > 0) {
            decodeFile = ColorFilter.filterByCustomRanges(decodeFile, colorFilterCustomRanges);
        }
        
        Bitmap gray = ImageUtils
                .toGray(ImageUtils.resize(decodeFile, (decodeFile.getWidth() * 64) / decodeFile.getHeight(), 64));
        long j = 1;
        long width = gray.getWidth();
        long[] jArr = { j, j, gray.getHeight(), width };
        int i = (int) (jArr[0] * jArr[1] * jArr[2] * width);
        float[] fArr = new float[(int) (jArr[0] * jArr[1] * jArr[2] * jArr[3])];
        int[] pixels = new int[i];
        gray.getPixels(pixels, 0, gray.getWidth(), 0, 0, gray.getWidth(), gray.getHeight());
        for (int i2 = 0; i2 < i; i2++) {
            int grayValue = pixels[i2] & 0xFF;
            fArr[i2] = (float) ((grayValue / 127.5) - 1.0);
        }
        try {
            ONNXRuntimeUtils oNNXRuntimeUtils = new ONNXRuntimeUtils();
            OnnxTensor createTensor = oNNXRuntimeUtils.createTensor(fArr, jArr);
            OrtSession createSession = oNNXRuntimeUtils.createSession(modelFile.getAbsolutePath());
            m13m = OCREngine1.m13m(new Map.Entry[] { new AbstractMap.SimpleEntry("input1", createTensor) });
            OnnxTensor onnxTensor = (OnnxTensor) createSession.run(m13m).get(0);
            long[][] jArr2 = (long[][]) onnxTensor.getValue();
            StringBuilder sb = new StringBuilder();
            for (long j2 : jArr2[0]) {
                sb.append((String) charsetArray.get((int) j2));
            }
            String rawResult = sb.toString();
            
            // 根据OCR类型过滤结果
            return filterResultByType(rawResult, ocrType);
        } catch (Exception e) {
            Log.m16d(TAG, new StringBuffer().append("OCR识别异常").append(e).toString());
            return null;
        }
    }
    
    /**
     * 根据OCR类型过滤识别结果
     * @param rawResult 原始识别结果
     * @param ocrType OCR类型
     * @return 过滤后的结果
     */
    private String filterResultByType(String rawResult, String ocrType) {
        if (rawResult == null || rawResult.isEmpty()) {
            return rawResult;
        }
        
        switch (ocrType.toLowerCase()) {
            case "number":
                // 只保留数字
                return rawResult.replaceAll("[^0-9]", "");
                
            case "letter":
                // 只保留字母
                return rawResult.replaceAll("[^a-zA-Z]", "");
                
            case "alphanumeric":
                // 只保留英数字符
                return rawResult.replaceAll("[^a-zA-Z0-9]", "");
                
            case "chinese":
                // 只保留中文字符
                return rawResult.replaceAll("[^\\u4e00-\\u9fa5]", "");
                
            case "math":
                // 保留数字、运算符和常见数学符号
                return rawResult.replaceAll("[^0-9+\\-*/=().\\s]", "");
                
            case "auto":
            default:
                // 自动识别，返回原始结果
                return rawResult;
        }
    }
}
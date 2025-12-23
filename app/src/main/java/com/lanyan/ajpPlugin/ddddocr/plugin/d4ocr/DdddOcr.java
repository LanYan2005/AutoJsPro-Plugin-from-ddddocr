package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr;

import android.content.Context;
import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils.Log;

import java.util.List;

/**
 * DdddOcr - 带带弟弟OCR Android版本
 * 
 * 功能：
 * 1. OCR文字识别（支持颜色过滤）
 * 2. 目标检测
 * 3. 滑块匹配
 * 
 * 使用示例：
 * 
 * // OCR识别
 * DdddOcr ocr = new DdddOcr(context, true, false);
 * String result = ocr.classification(imagePath);
 * 
 * // 带颜色过滤的OCR
 * String result = ocr.classification(imagePath, new String[]{"red", "blue"});
 * 
 * // 目标检测
 * DdddOcr det = new DdddOcr(context, false, true);
 * List<int[]> boxes = det.detection(imagePath);
 * 
 * // 滑块匹配
 * DdddOcr slide = new DdddOcr(context, false, false);
 * int x = slide.slideMatch(targetImage, backgroundImage, false);
 * int x2 = slide.slideComparison(imageWithGap, fullImage);
 */
public class DdddOcr {
    private static final String TAG = "DdddOcr";
    
    private boolean ocrEnabled;
    private boolean detEnabled;
    private OCREngine ocrEngine;
    private DetectionEngine detectionEngine;
    private SlideEngine slideEngine;
    
    /**
     * 构造函数
     * @param context Android上下文
     * @param enableOcr 是否启用OCR功能
     * @param enableDet 是否启用目标检测功能
     */
    public DdddOcr(Context context, boolean enableOcr, boolean enableDet) {
        this.ocrEnabled = enableOcr;
        this.detEnabled = enableDet;
        
        try {
            if (enableOcr) {
                OCREngine.setModel(context);
                ocrEngine = new OCREngine();
                Log.m16d(TAG, "OCR引擎初始化成功");
            }
            
            if (enableDet) {
                DetectionEngine.setModel(context);
                detectionEngine = new DetectionEngine();
                Log.m16d(TAG, "检测引擎初始化成功");
            }
            
            // 滑块引擎不需要模型，总是可用
            slideEngine = new SlideEngine();
            
        } catch (Exception e) {
            Log.m18e(TAG + " 初始化失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 简化构造函数 - 默认启用OCR
     */
    public DdddOcr(Context context) {
        this(context, true, false);
    }
    
    // ==================== OCR 相关方法 ====================
    
    /**
     * OCR文字识别
     * @param input 图像路径、base64或文件路径
     * @return 识别的文字
     */
    public String classification(String input) {
        if (!ocrEnabled || ocrEngine == null) {
            Log.m16d(TAG, "OCR功能未启用");
            return "OCR功能未启用";
        }
        return ocrEngine.recognize(input);
    }
    
    /**
     * OCR文字识别（指定类型）
     * @param input 图像路径、base64或文件路径
     * @param ocrType OCR类型：
     *                "auto" - 自动识别（默认）
     *                "number" - 纯数字
     *                "letter" - 纯字母
     *                "alphanumeric" - 英数混合
     *                "chinese" - 中文
     *                "math" - 数学计算表达式
     * @return 识别的文字
     */
    public String classification(String input, String ocrType) {
        if (!ocrEnabled || ocrEngine == null) {
            Log.m16d(TAG, "OCR功能未启用");
            return "OCR功能未启用";
        }
        return ocrEngine.recognize(input, ocrType);
    }
    
    /**
     * 带颜色过滤的OCR识别
     * @param input 图像输入
     * @param colorFilterColors 颜色名称数组，如 ["red", "blue"]
     * @return 识别的文字
     */
    public String classification(String input, String[] colorFilterColors) {
        if (!ocrEnabled || ocrEngine == null) {
            Log.m16d(TAG, "OCR功能未启用");
            return "OCR功能未启用";
        }
        return ocrEngine.recognize(input, colorFilterColors, null);
    }
    
    /**
     * 带颜色过滤和类型指定的OCR识别
     * @param input 图像输入
     * @param colorFilterColors 颜色名称数组，如 ["red", "blue"]
     * @param ocrType OCR类型
     * @return 识别的文字
     */
    public String classification(String input, String[] colorFilterColors, String ocrType) {
        if (!ocrEnabled || ocrEngine == null) {
            Log.m16d(TAG, "OCR功能未启用");
            return "OCR功能未启用";
        }
        return ocrEngine.recognize(input, colorFilterColors, null, ocrType);
    }
    
    /**
     * 带自定义HSV范围的OCR识别
     * @param input 图像输入
     * @param customRanges HSV范围数组 [[h_min, s_min, v_min, h_max, s_max, v_max], ...]
     * @return 识别的文字
     */
    public String classificationWithCustomColors(String input, int[][] customRanges) {
        if (!ocrEnabled || ocrEngine == null) {
            Log.m16d(TAG, "OCR功能未启用");
            return "OCR功能未启用";
        }
        return ocrEngine.recognize(input, null, customRanges);
    }
    
    // ==================== 目标检测相关方法 ====================
    
    /**
     * 目标检测
     * @param input 图像路径、base64或文件路径
     * @return 检测到的边界框列表 [[x1,y1,x2,y2], ...]
     */
    public List<int[]> detection(String input) {
        if (!detEnabled || detectionEngine == null) {
            Log.m16d(TAG, "检测功能未启用");
            return null;
        }
        return detectionEngine.detection(input);
    }
    
    // ==================== 滑块相关方法 ====================
    
    /**
     * 滑块匹配 - 算法1
     * @param targetInput 滑块图像
     * @param backgroundInput 背景图像
     * @param simpleTarget 是否为简单目标（无透明背景）
     * @return 滑块位置的x坐标
     */
    public int slideMatch(String targetInput, String backgroundInput, boolean simpleTarget) {
        if (slideEngine == null) {
            Log.m16d(TAG, "滑块引擎未初始化");
            return -1;
        }
        return slideEngine.slideMatch(targetInput, backgroundInput, simpleTarget);
    }
    
    /**
     * 滑块匹配 - 默认透明背景
     */
    public int slideMatch(String targetInput, String backgroundInput) {
        return slideMatch(targetInput, backgroundInput, false);
    }
    
    /**
     * 滑块比较 - 算法2
     * @param targetInput 带缺口的图像
     * @param backgroundInput 完整的背景图像
     * @return 缺口位置的x坐标
     */
    public int slideComparison(String targetInput, String backgroundInput) {
        if (slideEngine == null) {
            Log.m16d(TAG, "滑块引擎未初始化");
            return -1;
        }
        return slideEngine.slideComparison(targetInput, backgroundInput);
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 获取可用的颜色预设
     */
    public static String[] getAvailableColors() {
        return ColorFilter.getAvailableColors();
    }
    
    /**
     * 检查OCR是否可用
     */
    public boolean isOcrEnabled() {
        return ocrEnabled && ocrEngine != null;
    }
    
    /**
     * 检查检测是否可用
     */
    public boolean isDetectionEnabled() {
        return detEnabled && detectionEngine != null;
    }
}

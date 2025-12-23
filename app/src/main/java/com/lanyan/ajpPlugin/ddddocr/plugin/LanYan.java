package com.lanyan.ajpPlugin.ddddocr.plugin;

import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.autojs.plugin.sdk.Plugin;

/* loaded from: classes.dex */
public class LanYan extends Plugin {
    private static Object dcl = null;
    private static boolean isInitialized = false;
    private static Class<?> ocrEngine;
    private static Object ddddOcrInstance = null;
    private final Context context;
    private final Context selfContext;

    static {
        try {
            initOCRStatic(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LanYan(Context context, Context context2, Object obj, Object obj2) {
        super(context, context2, obj, obj2);
        this.context = context;
        this.selfContext = context2;
        if (isInitialized) {
            return;
        }
        try {
            initOCRStatic(context2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // org.autojs.plugin.sdk.Plugin
    public String getAssetsScriptDir() {
        return "plugin-LanYan";
    }

    public String getStringFromJava() {
        return "Hello, Auto.js!";
    }

    public void say(String str) {
        Toast.makeText(this.context, str, 0).show();
    }

    private static synchronized void initOCRStatic(Context context) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        synchronized (LanYan.class) {
            if (isInitialized) {
                return;
            }
            if (context == null) {
                return;
            }
            try {
                // 初始化旧的OCREngine（保持向后兼容）
                Class<?> cls = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.OCREngine");
                ocrEngine = cls;
                Constructor<?> constructor = cls.getConstructor(new Class[0]);
                ocrEngine.getMethod("setModel", Context.class).invoke(null, context);
                dcl = constructor.newInstance(new Object[0]);
                
                // 初始化新的DdddOcr
                Class<?> ddddOcrClass = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.DdddOcr");
                Constructor<?> ddddOcrConstructor = ddddOcrClass.getConstructor(Context.class, boolean.class, boolean.class);
                ddddOcrInstance = ddddOcrConstructor.newInstance(context, true, false);
                
                isInitialized = true;
            } catch (Exception e) {
                isInitialized = false;
                throw new RuntimeException("OCR初始化失败: " + e.getMessage(), e);
            }
        }
    }

    public synchronized String ocr(String str) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (!isInitialized) {
            initOCRStatic(this.selfContext);
        }
        return ocrEngine.getMethod("recognize", String.class).invoke(dcl, str).toString();
    }
    
    /**
     * OCR识别（指定类型）
     * @param imagePath 图像路径
     * @param ocrType OCR类型：
     *                "auto" - 自动识别（默认）
     *                "number" - 纯数字
     *                "letter" - 纯字母
     *                "alphanumeric" - 英数混合
     *                "chinese" - 中文
     *                "math" - 数学计算表达式
     * @return 识别结果
     */
    public String ocrWithType(String imagePath, String ocrType) {
        try {
            if (!isInitialized) {
                initOCRStatic(this.selfContext);
            }
            Class<?> ddddOcrClass = ddddOcrInstance.getClass();
            return (String) ddddOcrClass.getMethod("classification", String.class, String.class)
                    .invoke(ddddOcrInstance, imagePath, ocrType);
        } catch (Exception e) {
            e.printStackTrace();
            return "OCR识别失败: " + e.getMessage();
        }
    }

    public String findImagePosition(String largeImagePath, String smallImagePath) {
        try {
            Class<?> imageMatcherClass = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.matcher.ImageMatcher");
            Constructor<?> constructor = imageMatcherClass.getConstructor();
            Object imageMatcher = constructor.newInstance();
            
            Object result = imageMatcherClass.getMethod("findImage", String.class, String.class)
                    .invoke(imageMatcher, largeImagePath, smallImagePath);
            
            return result != null ? result.toString() : "未找到匹配位置";
        } catch (Exception e) {
            e.printStackTrace();
            return "图片匹配失败: " + e.getMessage();
        }
    }
    
    // ==================== 新增的 DdddOcr 功能 ====================
    
    /**
     * 带颜色过滤的OCR识别
     * @param imagePath 图像路径
     * @param colors 颜色数组，如 ["red", "blue"]
     * @return 识别结果
     */
    public String ocrWithColorFilter(String imagePath, String[] colors) {
        try {
            if (!isInitialized) {
                initOCRStatic(this.selfContext);
            }
            Class<?> ddddOcrClass = ddddOcrInstance.getClass();
            return (String) ddddOcrClass.getMethod("classification", String.class, String[].class)
                    .invoke(ddddOcrInstance, imagePath, colors);
        } catch (Exception e) {
            e.printStackTrace();
            return "OCR识别失败: " + e.getMessage();
        }
    }
    
    /**
     * 带颜色过滤和类型指定的OCR识别
     * @param imagePath 图像路径
     * @param colors 颜色数组，如 ["red", "blue"]
     * @param ocrType OCR类型
     * @return 识别结果
     */
    public String ocrWithColorFilterAndType(String imagePath, String[] colors, String ocrType) {
        try {
            if (!isInitialized) {
                initOCRStatic(this.selfContext);
            }
            Class<?> ddddOcrClass = ddddOcrInstance.getClass();
            return (String) ddddOcrClass.getMethod("classification", String.class, String[].class, String.class)
                    .invoke(ddddOcrInstance, imagePath, colors, ocrType);
        } catch (Exception e) {
            e.printStackTrace();
            return "OCR识别失败: " + e.getMessage();
        }
    }
    
    /**
     * 目标检测
     * @param imagePath 图像路径
     * @return 检测结果的JSON字符串
     */
    public String detection(String imagePath) {
        try {
            // 创建检测实例
            Class<?> ddddOcrClass = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.DdddOcr");
            Constructor<?> constructor = ddddOcrClass.getConstructor(Context.class, boolean.class, boolean.class);
            Object detInstance = constructor.newInstance(this.selfContext, false, true);
            
            Object result = ddddOcrClass.getMethod("detection", String.class)
                    .invoke(detInstance, imagePath);
            
            if (result == null) {
                return "[]";
            }
            
            // 转换为JSON字符串
            java.util.List<int[]> boxes = (java.util.List<int[]>) result;
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < boxes.size(); i++) {
                int[] box = boxes.get(i);
                json.append("[").append(box[0]).append(",").append(box[1]).append(",")
                    .append(box[2]).append(",").append(box[3]).append("]");
                if (i < boxes.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "检测失败: " + e.getMessage();
        }
    }
    
    /**
     * 滑块匹配 - 算法1
     * @param targetPath 滑块图像路径
     * @param backgroundPath 背景图像路径
     * @param simpleTarget 是否为简单目标
     * @return 滑块位置x坐标
     */
    public int slideMatch(String targetPath, String backgroundPath, boolean simpleTarget) {
        try {
            Class<?> ddddOcrClass = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.DdddOcr");
            Constructor<?> constructor = ddddOcrClass.getConstructor(Context.class, boolean.class, boolean.class);
            Object slideInstance = constructor.newInstance(this.selfContext, false, false);
            
            return (int) ddddOcrClass.getMethod("slideMatch", String.class, String.class, boolean.class)
                    .invoke(slideInstance, targetPath, backgroundPath, simpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 滑块匹配 - 默认透明背景
     */
    public int slideMatch(String targetPath, String backgroundPath) {
        return slideMatch(targetPath, backgroundPath, false);
    }
    
    /**
     * 滑块比较 - 算法2
     * @param targetPath 带缺口的图像路径
     * @param backgroundPath 完整背景图像路径
     * @return 缺口位置x坐标
     */
    public int slideComparison(String targetPath, String backgroundPath) {
        try {
            Class<?> ddddOcrClass = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.DdddOcr");
            Constructor<?> constructor = ddddOcrClass.getConstructor(Context.class, boolean.class, boolean.class);
            Object slideInstance = constructor.newInstance(this.selfContext, false, false);
            
            return (int) ddddOcrClass.getMethod("slideComparison", String.class, String.class)
                    .invoke(slideInstance, targetPath, backgroundPath);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 获取可用的颜色预设
     * @return 颜色名称数组
     */
    public String[] getAvailableColors() {
        try {
            Class<?> ddddOcrClass = Class.forName("com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.DdddOcr");
            return (String[]) ddddOcrClass.getMethod("getAvailableColors").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}
# LanYan-Plugin API 参考文档

## 概述

LanYan-Plugin 插件提供了完整的验证码识别、目标检测和滑块匹配功能。所有功能都通过 `index.js` 导出，可以在 Auto.js 脚本中直接使用。

## OCR 识别功能

### 基础 OCR

```javascript
// 基础 OCR 识别
var result = lanyan.ocr(imagePath);

// 兼容旧版本的类型参数
var result = lanyan.ocr(imagePath, '1'); // 英数类型
var result = lanyan.ocr(imagePath, '2'); // 计算类型
```

### 类型化 OCR

```javascript
// 指定类型的 OCR 识别
var result = lanyan.ocrWithType(imagePath, ocrType);
```

**支持的 OCR 类型：**
- `"auto"` - 自动识别（默认）
- `"number"` - 纯数字（0-9）
- `"letter"` - 纯字母（a-z, A-Z）
- `"alphanumeric"` - 英数混合
- `"chinese"` - 中文字符
- `"math"` - 数学表达式（数字 + 运算符）

### 便捷方法

```javascript
// 快速识别特定类型
var numbers = lanyan.ocrNumber(imagePath);        // 纯数字
var letters = lanyan.ocrLetter(imagePath);        // 纯字母
var alphanumeric = lanyan.ocrAlphanumeric(imagePath); // 英数混合
var chinese = lanyan.ocrChinese(imagePath);       // 中文
var math = lanyan.ocrMath(imagePath);             // 数学表达式
```

## 颜色过滤功能

### 颜色过滤 OCR

```javascript
// 只识别指定颜色的文字
var result = lanyan.ocrWithColorFilter(imagePath, ["red", "blue"]);

// 颜色过滤 + 类型指定
var result = lanyan.ocrWithColorFilterAndType(imagePath, ["red"], "number");
```

### 可用颜色

```javascript
// 获取所有可用的颜色预设
var colors = lanyan.getAvailableColors();
console.log(colors); // ["red", "blue", "green", "yellow", "orange", "purple", "cyan", "black", "white", "gray"]
```

**支持的颜色：**
- `"red"` - 红色
- `"blue"` - 蓝色
- `"green"` - 绿色
- `"yellow"` - 黄色
- `"orange"` - 橙色
- `"purple"` - 紫色
- `"cyan"` - 青色
- `"black"` - 黑色
- `"white"` - 白色
- `"gray"` - 灰色

## 目标检测功能

```javascript
// 检测图像中的目标位置
var result = lanyan.detection(imagePath);
var boxes = JSON.parse(result); // [[x1,y1,x2,y2], [x1,y1,x2,y2], ...]

// 遍历检测结果
for (var i = 0; i < boxes.length; i++) {
    var box = boxes[i];
    var x1 = box[0], y1 = box[1], x2 = box[2], y2 = box[3];
    console.log("目标 " + (i+1) + ": (" + x1 + "," + y1 + ") -> (" + x2 + "," + y2 + ")");
}
```

## 滑块匹配功能

### 模板匹配算法

```javascript
// 透明背景滑块（默认）- 支持文件路径和 base64
var x = lanyan.slideMatch(sliderPath, backgroundPath);
var x = lanyan.slideMatch(sliderBase64, bgBase64);

// 简单滑块（无透明背景）- 支持文件路径和 base64
var x = lanyan.slideMatch(sliderPath, backgroundPath, true);
var x = lanyan.slideMatch(sliderBase64, bgBase64, true);
```

### 差异比较算法

```javascript
// 比较带缺口的图和完整图 - 支持文件路径和 base64
var x = lanyan.slideComparison(gapImagePath, fullImagePath);
var x = lanyan.slideComparison(gapBase64, fullBase64);
```

## 图像匹配功能

```javascript
// 在大图中查找小图的位置
var result = lanyan.ocrImage(largeImagePath, smallImagePath);
```

## 完整使用示例

### 智能验证码识别

```javascript
function smartCaptchaRecognition(imagePath) {
    // 尝试多种类型
    var types = ["number", "alphanumeric", "auto"];
    var results = {};
    
    for (var i = 0; i < types.length; i++) {
        var type = types[i];
        try {
            results[type] = lanyan.ocrWithType(imagePath, type);
        } catch (e) {
            console.log(type + " 识别失败");
        }
    }
    
    // 选择最合适的结果
    return results.number || results.alphanumeric || results.auto;
}
```

### 彩色验证码识别

```javascript
function colorCaptchaRecognition(imagePath) {
    var colors = ["red", "blue", "black"];
    var bestResult = "";
    
    for (var i = 0; i < colors.length; i++) {
        var result = lanyan.ocrWithColorFilterAndType(imagePath, [colors[i]], "alphanumeric");
        if (result.length > bestResult.length) {
            bestResult = result;
        }
    }
    
    return bestResult;
}
```

### 数学验证码求解

```javascript
function solveMathCaptcha(imagePath) {
    // 识别数学表达式
    var expression = lanyan.ocrMath(imagePath);
    console.log("表达式: " + expression);
    
    try {
        // 计算结果
        var result = eval(expression);
        return result.toString();
    } catch (e) {
        // 降级到普通识别
        return lanyan.ocr(imagePath);
    }
}
```

### 自动滑块验证

```javascript
function autoSlideVerify(sliderPath, bgPath) {
    // 计算滑动距离
    var x = lanyan.slideMatch(sliderPath, bgPath);
    
    if (x > 0) {
        // 执行滑动操作
        var startX = 100, startY = 500;
        swipe(startX, startY, startX + x, startY, 500);
        return true;
    }
    
    return false;
}
```

## 性能说明

基于测试设备的平均性能：

| 功能 | 平均耗时 | 说明 |
|------|---------|------|
| 基础 OCR | 200-300ms | 不含颜色过滤 |
| 类型化 OCR | 200-300ms | 额外的后处理时间很少 |
| 颜色过滤 OCR | 250-350ms | 包含颜色过滤处理 |
| 目标检测 | 300-500ms | 依赖检测模型 |
| 滑块匹配 | 100-200ms | OpenCV 模板匹配 |

## 错误处理

所有方法都包含错误处理，失败时会返回：
- OCR 方法：返回错误信息字符串
- 检测方法：返回空数组 `"[]"`
- 滑块方法：返回 `-1`

建议在使用时添加适当的错误检查：

```javascript
var result = lanyan.ocrNumber(imagePath);
if (result && result.length > 0 && !result.includes("失败")) {
    // 识别成功
    console.log("识别结果: " + result);
} else {
    // 识别失败，尝试其他方法
    console.log("识别失败，尝试其他方法");
}
```

## 注意事项

1. **图片路径**：确保图片路径正确且文件存在
2. **权限**：确保应用有读取存储的权限
3. **图片格式**：支持 JPG、PNG、GIF、SVG、Base64
4. **OpenCV 依赖**：颜色过滤和滑块功能需要 OpenCV 库
5. **性能优化**：避免频繁创建新的识别实例，复用现有实例

## 更新日志

### v1.1.0 (2024-12-22)
- ✅ 新增 OCR 类型过滤功能
- ✅ 新增便捷方法
- ✅ 完善 index.js 导出
- ✅ 兼容旧版本 API

### v1.0.0 (2024-12-21)
- ✅ 基础 OCR 识别
- ✅ 颜色过滤功能
- ✅ 目标检测功能
- ✅ 滑块匹配功能
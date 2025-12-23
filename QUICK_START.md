# 快速开始指南

## 5分钟上手 DdddOcr Android

### 步骤1: 构建项目 (2分钟)

```bash
cd Piyan
gradlew.bat assembleDebug
```

### 步骤2: 安装到设备 (1分钟)

```bash
gradlew.bat installDebug
```

或者直接在 Android Studio 中点击运行按钮 ▶️

### 步骤3: 准备测试图片 (1分钟)

将测试图片放到手机存储：
```
/sdcard/test_captcha.jpg
```

### 步骤4: 运行测试 (1分钟)

在 Auto.js 中运行以下代码：

```javascript
// 基础 OCR 测试
var text = lanyan.ocr("/sdcard/test_captcha.jpg");
console.log("识别结果: " + text);

// 颜色过滤测试
var text = lanyan.ocrWithColorFilter("/sdcard/test_captcha.jpg", ["red"]);
console.log("红色文字: " + text);

// 查看可用颜色
var colors = lanyan.getAvailableColors();
console.log("可用颜色: " + colors.join(", "));
```

## 常用功能速查

### 1. OCR 识别
```javascript
// 识别本地图片
var text = lanyan.ocr("/sdcard/captcha.jpg");

// 识别 base64
var text = lanyan.ocr("iVBORw0KGgoAAAANSUhEUgAA...");
```

### 2. 颜色过滤
```javascript
// 单色过滤
var text = lanyan.ocrWithColorFilter(imagePath, ["red"]);

// 多色过滤
var text = lanyan.ocrWithColorFilter(imagePath, ["red", "blue"]);
```

### 3. 目标检测
```javascript
var boxes = lanyan.detection(imagePath);
var result = JSON.parse(boxes);
// result: [[x1,y1,x2,y2], ...]
```

### 4. 滑块匹配
```javascript
// 算法1: 模板匹配
var x = lanyan.slideMatch(sliderPath, bgPath);

// 算法2: 差异比较
var x = lanyan.slideComparison(gapPath, fullPath);
```

## 实际应用示例

### 自动登录验证码
```javascript
function autoLogin() {
    // 截取验证码
    var img = captureScreen();
    var path = "/sdcard/temp.jpg";
    images.save(img, path);
    
    // 识别
    var code = lanyan.ocr(path);
    
    // 输入
    setText(code);
    click("登录");
}
```

### 彩色验证码识别
```javascript
function recognizeColorCaptcha() {
    var path = "/sdcard/color_captcha.jpg";
    
    // 尝试不同颜色
    var colors = ["red", "blue", "black"];
    for (var i = 0; i < colors.length; i++) {
        var result = lanyan.ocrWithColorFilter(path, [colors[i]]);
        console.log(colors[i] + ": " + result);
    }
}
```

### 滑块验证码
```javascript
function autoSlide() {
    var x = lanyan.slideMatch(
        "/sdcard/slider.png",
        "/sdcard/background.png"
    );
    
    if (x > 0) {
        swipe(100, 500, 100 + x, 500, 500);
    }
}
```

## 下一步

- 📖 阅读 [完整文档](DDDDOCR_USAGE.md)
- 🔧 查看 [构建指南](BUILD_GUIDE.md)
- 📝 运行 [测试脚本](app/src/main/assets/plugin-LanYan/test_ddddocr.js)
- 💡 参考 [示例代码](app/src/main/assets/plugin-LanYan/ddddocr_example.js)

## 获取帮助

- 📖 [查看文档](README_DDDDOCR.md)
- 💬 加入讨论组

---

**祝使用愉快！** 🎉

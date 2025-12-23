# LanYan-Plugin - Auto.js Plugin with DdddOcr

<div align="center">

![Logo](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

**一个集成了 DdddOcr 验证码识别功能的 Auto.js 插件**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)

[快速开始](QUICK_START.md) | [使用文档](DDDDOCR_USAGE.md) | [构建指南](BUILD_GUIDE.md) | [更新日志](CHANGELOG.md)

</div>

## ✨ 特性

- 🔤 **OCR 文字识别** - 识别验证码中的文字（数字、英文、中文）
- 🎨 **颜色过滤** - 提取特定颜色的文字，提高识别准确率
- 🎯 **目标检测** - 检测图像中的目标位置
- 🧩 **滑块匹配** - 两种算法识别滑块验证码
- 📱 **Auto.js 集成** - 无缝集成到 Auto.js 脚本中
- ⚡ **高性能** - 基于 ONNX Runtime 和 OpenCV

## 📦 功能列表

| 功能 | 状态 | 说明 |
|------|------|------|
| OCR 识别 | ✅ | 支持数字、英文、中文 |
| OCR 类型过滤 | ✅ | 支持 6 种类型：auto、number、letter、alphanumeric、chinese、math |
| 颜色过滤 | ✅ | 10种预设颜色 + 自定义 |
| 颜色+类型组合 | ✅ | 颜色过滤与类型过滤组合使用 |
| 目标检测 | ✅ | 返回边界框坐标 |
| 滑块匹配 | ✅ | 两种算法可选 |
| Beta 模型 | ⏳ | 计划中 |
| 概率输出 | ⏳ | 计划中 |
| 自定义模型 | ⏳ | 计划中 |

## 🚀 快速开始

### 构建和安装

```bash
# 克隆项目
git clone https://github.com/LanYan2005/LanYan-Plugin.git
cd LanYan-Plugin

# 构建 APK
gradlew.bat assembleRelease

# 安装到设备
gradlew.bat installRelease
```

**环境要求：**
- Android Studio
- JDK 11+
- Android SDK 26+

### 使用方法

```javascript
// OCR 识别 - 支持文件路径和 base64
var text = lanyan.ocr("/sdcard/captcha.jpg");
var text = lanyan.ocr(base64Image);

// 指定类型的 OCR 识别
var numbers = lanyan.ocrWithType("/sdcard/captcha.jpg", "number");
var letters = lanyan.ocrWithType(base64Image, "letter");
var math = lanyan.ocrWithType("/sdcard/captcha.jpg", "math");

// 颜色过滤 - 支持文件路径和 base64
var text = lanyan.ocrWithColorFilter("/sdcard/captcha.jpg", ["red", "blue"]);
var text = lanyan.ocrWithColorFilter(base64Image, ["red", "blue"]);

// 颜色过滤 + 类型指定
var redNumbers = lanyan.ocrWithColorFilterAndType("/sdcard/captcha.jpg", ["red"], "number");
var redNumbers = lanyan.ocrWithColorFilterAndType(base64Image, ["red"], "number");

// 目标检测 - 支持文件路径和 base64
var boxes = lanyan.detection("/sdcard/image.jpg");
var boxes = lanyan.detection(base64Image);

// 滑块匹配 - 支持文件路径和 base64
var x = lanyan.slideMatch("/sdcard/slider.png", "/sdcard/bg.png");
var x = lanyan.slideMatch(sliderBase64, bgBase64);
```

详细使用方法请查看 [快速开始指南](QUICK_START.md) 和 [使用示例](examples/)

## 📖 文档

- 📘 [快速开始](QUICK_START.md) - 5分钟上手指南
- 🚀 [使用示例](examples/) - 基础和高级使用示例
- 📖 [API 参考](API_REFERENCE.md) - 完整的 API 文档
- 📔 [更新日志](CHANGELOG.md) - 版本历史和计划

## 💡 示例

### 自动识别登录验证码

```javascript
function autoLogin() {
    // 截取验证码区域
    var captcha = captureScreen();
    var path = "/sdcard/temp_captcha.jpg";
    images.save(captcha, path);
    
    // 识别验证码
    var code = lanyan.ocr(path);
    console.log("验证码: " + code);
    
    // 输入验证码
    setText(code);
    click("登录");
    
    // 清理
    files.remove(path);
}
```

### 识别彩色验证码

```javascript
function recognizeColorCaptcha() {
    var imagePath = "/sdcard/color_captcha.jpg";
    
    // 尝试不同颜色组合
    var colorSets = [
        ["red"],
        ["blue"],
        ["red", "blue"],
        ["black"]
    ];
    
    for (var i = 0; i < colorSets.length; i++) {
        var result = lanyan.ocrWithColorFilter(imagePath, colorSets[i]);
        console.log("颜色 " + colorSets[i].join("+") + ": " + result);
    }
}
```

### 自动滑动滑块

```javascript
function autoSlideVerify() {
    var sliderPath = "/sdcard/slider.png";
    var bgPath = "/sdcard/background.png";
    
    // 计算滑动距离
    var x = lanyan.slideMatch(sliderPath, bgPath);
    console.log("需要滑动到: " + x);
    
    // 执行滑动
    if (x > 0) {
        var startX = 100;
        var startY = 500;
        swipe(startX, startY, startX + x, startY, 500);
    }
}
```

更多示例请查看 [示例脚本](app/src/main/assets/plugin-LanYan/ddddocr_example.js)

## 🏗️ 项目结构

```
LanYan-Plugin/
├── app/
│   ├── src/main/
│   │   ├── java/com/lanyan/ajpPlugin/
│   │   │   └── plugin/
│   │   │       ├── LanYan.java              # 插件主类
│   │   │       └── d4ocr/
│   │   │           ├── DdddOcr.java         # 统一接口
│   │   │           ├── OCREngine.java       # OCR 引擎
│   │   │           ├── DetectionEngine.java # 检测引擎
│   │   │           ├── SlideEngine.java     # 滑块引擎
│   │   │           ├── ColorFilter.java     # 颜色过滤
│   │   │           └── utils/               # 工具类
│   │   ├── assets/
│   │   │   ├── common_old.onnx              # OCR 模型
│   │   │   ├── common_old_charset.json      # 字符集
│   │   │   ├── common_det.onnx              # 检测模型
│   │   │   └── plugin-LanYan/
│   │   │       ├── ddddocr_example.js       # 示例脚本
│   │   │       └── test_ddddocr.js          # 测试脚本
│   │   └── res/                             # 资源文件
│   └── build.gradle                         # 构建配置
├── docs/                                    # 文档目录
│   ├── QUICK_START.md                       # 快速开始
│   ├── DDDDOCR_USAGE.md                     # 使用文档
│   ├── BUILD_GUIDE.md                       # 构建指南
│   ├── README_DDDDOCR.md                    # 项目说明
│   ├── CHANGELOG.md                         # 更新日志
│   └── MIGRATION_SUMMARY.md                 # 移植总结
└── README.md                                # 本文件
```

## 🔧 技术栈

- **语言**: Java 11
- **平台**: Android 8.0+ (API 26+)
- **推理引擎**: ONNX Runtime Android 1.14.0
- **图像处理**: OpenCV 4.1.1.0
- **JavaScript 引擎**: Rhino 1.7.14
- **插件框架**: Auto.js Plugin SDK 0.2

## 📊 性能

基于测试设备（Xiaomi 12, Snapdragon 8 Gen 1）：

| 功能 | 平均耗时 | 内存占用 |
|------|---------|---------|
| OCR 识别 | ~200-300ms | ~80MB |
| 颜色过滤 OCR | ~250-350ms | ~80MB |
| 目标检测 | ~300-500ms | ~100MB |
| 滑块匹配 | ~100-200ms | ~50MB |

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

### 开发流程

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范

- 遵循 Java 命名规范
- 添加必要的注释
- 编写单元测试
- 更新相关文档

## 📝 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [ddddocr](https://github.com/sml2h3/ddddocr) - 原 Python 项目
- [ONNX Runtime](https://onnxruntime.ai/) - Microsoft
- [OpenCV](https://opencv.org/) - OpenCV Team
- [Auto.js](https://github.com/hyb1996/Auto.js) - hyb1996

## 📮 联系方式

- 📝 提交 [Issue](https://github.com/your-repo/issues)
- 💬 加入讨论组
- 📧 发送邮件反馈

## ⚠️ 免责声明

本项目仅供学习和研究使用，请勿用于非法用途。使用本项目所产生的一切后果由使用者自行承担。

---

<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐️ Star！**

Made with ❤️ by LanYan2005

</div>

# AutoJsPro-Plugin-from-ddddocr

🔤 带带弟弟OCR Android插件 - 支持文字识别、目标检测、滑块匹配，兼容Auto.js插件SDK

这个AutoJsPro插件是ddddocr移植过来的，如果有bug自己测一下吧，暂时不更新了

<div align="center">

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)

[快速开始](QUICK_START.md) | [API文档](API_REFERENCE.md) | [更新日志](CHANGELOG.md)

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

## 🚀 快速开始

### 构建和安装

```bash
# 克隆项目
git clone https://github.com/LanYan2005/AutoJsPro-Plugin-from-ddddocr.git
cd AutoJsPro-Plugin-from-ddddocr

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

// 颜色过滤 - 支持文件路径和 base64
var text = lanyan.ocrWithColorFilter("/sdcard/captcha.jpg", ["red", "blue"]);

// 目标检测 - 支持文件路径和 base64
var boxes = lanyan.detection("/sdcard/image.jpg");

// 滑块匹配 - 支持文件路径和 base64
var x = lanyan.slideMatch("/sdcard/slider.png", "/sdcard/bg.png");
```

详细使用方法请查看 [快速开始指南](QUICK_START.md) 和 [API文档](API_REFERENCE.md)

## 🔧 技术栈

- **语言**: Java 11
- **平台**: Android 8.0+ (API 26+)
- **推理引擎**: ONNX Runtime Android 1.12.1
- **图像处理**: OpenCV 4.5.3.0
- **插件框架**: Auto.js Plugin SDK 0.2

## 📝 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [ddddocr](https://github.com/sml2h3/ddddocr) - 原 Python 项目
- [ONNX Runtime](https://onnxruntime.ai/) - Microsoft
- [OpenCV](https://opencv.org/) - OpenCV Team
- [Auto.js](https://github.com/hyb1996/Auto.js) - hyb1996

## ⚠️ 免责声明

本项目仅供学习和研究使用，请勿用于非法用途。使用本项目所产生的一切后果由使用者自行承担。

---

<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐️ Star！**

Made with ❤️ by LanYan2005

</div>

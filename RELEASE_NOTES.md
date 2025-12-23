# LanYan-Plugin v1.0.0 发布说明

## 🎉 首次发布

LanYan-Plugin 是一个集成了 DdddOcr 验证码识别功能的 Auto.js 插件，将 Python 的 ddddocr 库成功移植到 Android 平台。

## ✨ 主要功能

### OCR 文字识别
- 支持数字、英文、中文识别
- 6种类型过滤：auto、number、letter、alphanumeric、chinese、math
- 支持文件路径和 base64 输入

### 颜色过滤
- 10种预设颜色过滤
- 支持自定义 HSV 范围
- 显著提高彩色验证码识别准确率

### 目标检测
- 检测图像中的目标位置
- 返回边界框坐标
- 适用于点击验证码

### 滑块匹配
- 两种算法：模板匹配、差异比较
- 支持透明背景和简单滑块
- 支持文件路径和 base64 输入

## 📦 构建变体

- **Full版本** - 包含所有功能（~40MB）
- **Basic版本** - OCR + 滑块功能（~30MB）
- **Lite版本** - 仅OCR功能（~20MB）

## 🚀 快速开始

1. 下载对应的 APK 文件
2. 安装到 Android 设备
3. 在 Auto.js 中启用插件
4. 参考 `examples/` 目录中的示例代码

## 📋 系统要求

- Android 8.0+ (API 26+)
- Auto.js 应用
- 推荐 2GB+ RAM

## 🔧 技术栈

- Java 11
- ONNX Runtime Android 1.14.0
- OpenCV 4.5.3.0
- Rhino JavaScript Engine 1.7.14

## 📖 文档

- [快速开始](QUICK_START.md)
- [API 参考](API_REFERENCE.md)
- [使用示例](examples/)

## 🐛 已知问题

- 首次启动可能需要较长时间（模型加载）
- 部分设备可能出现 OpenCV 加载失败（已有降级处理）

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📝 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

---

**下载地址**: [Releases](https://github.com/LanYan2005/LanYan-Plugin/releases)
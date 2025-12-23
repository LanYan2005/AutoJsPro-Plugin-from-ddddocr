# 更新日志

所有重要的项目变更都会记录在这个文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2024-12-21

### 新增 ✨

#### 核心功能
- ✅ **OCR 文字识别引擎** (`OCREngine.java`)
  - 支持数字、英文、中文识别
  - 支持 base64、文件路径、SVG 格式输入
  - 基于 ONNX Runtime 推理
  - 使用 `common_old.onnx` 模型

- ✅ **颜色过滤功能** (`ColorFilter.java`)
  - 10 种预设颜色：red, blue, green, yellow, orange, purple, cyan, black, white, gray
  - 支持自定义 HSV 颜色范围
  - 基于 OpenCV 的 HSV 色彩空间转换
  - 显著提高彩色验证码识别准确率

- ✅ **目标检测引擎** (`DetectionEngine.java`)
  - 检测图像中的目标位置
  - 返回边界框坐标 [x1, y1, x2, y2]
  - 使用 `common_det.onnx` 模型
  - 适用于点击验证码、文字定位

- ✅ **滑块匹配引擎** (`SlideEngine.java`)
  - **算法1 - 模板匹配**：适用于有滑块图和背景图
    - 支持透明背景滑块
    - 基于边缘检测的匹配
  - **算法2 - 差异比较**：适用于有缺口图和完整图
    - 通过图像差异定位缺口
    - 更适合复杂背景

#### 统一接口
- ✅ **DdddOcr 统一类** (`DdddOcr.java`)
  - 整合所有功能的统一接口
  - 支持功能选择性初始化
  - 提供简洁的 API 调用

#### Auto.js 插件集成
- ✅ **LanYan 插件扩展** (`LanYan.java`)
  - `ocr(imagePath)` - 基础 OCR 识别
  - `ocrWithColorFilter(imagePath, colors)` - 颜色过滤 OCR
  - `detection(imagePath)` - 目标检测
  - `slideMatch(target, bg, simple)` - 滑块匹配算法1
  - `slideComparison(gap, full)` - 滑块匹配算法2
  - `getAvailableColors()` - 获取可用颜色列表

#### 工具类
- ✅ **图像处理工具** (`ImageUtils.java`)
  - 图像缩放、灰度化、二值化
  - Base64 编解码
  - 图像保存

- ✅ **ONNX 运行时工具** (`ONNXRuntimeUtils.java`)
  - ONNX 模型加载和推理
  - 张量创建和管理

#### 文档
- ✅ **使用文档** (`DDDDOCR_USAGE.md`)
  - 详细的功能说明
  - 完整的代码示例
  - 常见问题解答

- ✅ **项目说明** (`README_DDDDOCR.md`)
  - 功能对比表
  - 项目结构说明
  - 快速开始指南

- ✅ **构建指南** (`BUILD_GUIDE.md`)
  - 详细的构建步骤
  - 常见问题解决
  - CI/CD 配置示例

- ✅ **示例脚本** (`ddddocr_example.js`)
  - Auto.js 使用示例
  - 实际应用场景
  - 性能测试代码

### 技术栈

- **推理引擎**: ONNX Runtime Android 1.14.0
- **图像处理**: OpenCV 4.1.1.0
- **SVG 支持**: AndroidSVG 1.4
- **JavaScript 引擎**: Rhino 1.7.14
- **插件框架**: Auto.js Plugin SDK 0.2

### 模型文件

- `common_old.onnx` (19.2 MB) - OCR 识别模型
- `common_old_charset.json` - 字符集配置文件
- `common_det.onnx` (6.3 MB) - 目标检测模型

### 性能指标

- **OCR 识别速度**: ~200-500ms (取决于图片大小)
- **目标检测速度**: ~300-600ms
- **滑块匹配速度**: ~100-300ms
- **内存占用**: ~50-100MB (模型加载后)

### 兼容性

- **最低 Android 版本**: Android 8.0 (API 26)
- **目标 Android 版本**: Android 14 (API 35)
- **支持架构**: ARM64, ARMv7, x86, x86_64

## [未来计划] - Roadmap

### v1.1.0 (计划中)

#### 新功能
- [ ] Beta 模型支持 (`common.onnx`)
- [ ] OCR 概率输出功能
- [ ] 字符集范围限定 (`set_ranges`)
- [ ] PNG 透明背景修复 (`png_fix`)

#### 优化
- [ ] 模型加载性能优化
- [ ] 内存使用优化
- [ ] 识别速度提升
- [ ] 缓存机制

#### 文档
- [ ] 视频教程
- [ ] 更多实际案例
- [ ] API 参考文档

### v1.2.0 (计划中)

#### 高级功能
- [ ] 自定义模型导入
- [ ] 批量识别支持
- [ ] 异步识别 API
- [ ] 识别结果缓存

#### 工具
- [ ] 模型转换工具
- [ ] 测试数据集
- [ ] 性能基准测试

### v2.0.0 (远期规划)

#### 重大更新
- [ ] 支持最新 ONNX Runtime
- [ ] GPU 加速支持
- [ ] 更多预训练模型
- [ ] 云端识别服务集成

## 版本对比

| 功能 | Python 版本 | Android v1.0.0 | 计划支持 |
|------|------------|----------------|---------|
| 基础 OCR | ✅ | ✅ | - |
| 颜色过滤 | ✅ | ✅ | - |
| 目标检测 | ✅ | ✅ | - |
| 滑块匹配 | ✅ | ✅ | - |
| Beta 模型 | ✅ | ❌ | v1.1.0 |
| 概率输出 | ✅ | ❌ | v1.1.0 |
| 自定义模型 | ✅ | ❌ | v1.2.0 |
| GPU 加速 | ✅ | ❌ | v2.0.0 |

## 贡献者

感谢所有为这个项目做出贡献的开发者！

- 原项目作者: [sml2h3](https://github.com/sml2h3)
- Android 移植: [Your Name]

## 反馈与支持

如果你在使用过程中遇到问题或有建议：

1. 📝 提交 [Issue](https://github.com/your-repo/issues)
2. 💬 加入讨论组
3. 📧 发送邮件反馈

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

**注意**: 本项目仅供学习和研究使用，请勿用于非法用途。

[1.0.0]: https://github.com/your-repo/releases/tag/v1.0.0

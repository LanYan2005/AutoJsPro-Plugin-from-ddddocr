module.exports = function (plugin) {
    var LanYan_plugin = {};

    // ==================== OCR 相关功能 ====================
    
    /**
     * 基础 OCR 识别
     * @param {string} image 图像路径或base64
     * @param {string} type 兼容旧版本的类型参数（可选）
     * @returns {string} 识别结果
     */
    LanYan_plugin.ocr = function (image, type) {
        // 兼容旧版本的类型参数
        if (type == '1') {
            // 旧版本的英数类型，使用新的 alphanumeric
            return plugin.ocrWithType(image, "alphanumeric");
        } else if (type == '2') {
            // 旧版本的计算类型，使用新的 math
            return plugin.ocrWithType(image, "math");
        } else {
            // 默认使用基础 OCR
            return plugin.ocr(image);
        }
    }
    
    /**
     * 指定类型的 OCR 识别
     * @param {string} image 图像路径或base64
     * @param {string} ocrType OCR类型：auto, number, letter, alphanumeric, chinese, math
     * @returns {string} 识别结果
     */
    LanYan_plugin.ocrWithType = function (image, ocrType) {
        return plugin.ocrWithType(image, ocrType);
    }
    
    /**
     * 带颜色过滤的 OCR 识别
     * @param {string} image 图像路径或base64
     * @param {string[]} colors 颜色数组，如 ["red", "blue"]
     * @returns {string} 识别结果
     */
    LanYan_plugin.ocrWithColorFilter = function (image, colors) {
        return plugin.ocrWithColorFilter(image, colors);
    }
    
    /**
     * 颜色过滤 + 类型指定的 OCR 识别
     * @param {string} image 图像路径或base64
     * @param {string[]} colors 颜色数组
     * @param {string} ocrType OCR类型
     * @returns {string} 识别结果
     */
    LanYan_plugin.ocrWithColorFilterAndType = function (image, colors, ocrType) {
        return plugin.ocrWithColorFilterAndType(image, colors, ocrType);
    }
    
    /**
     * 获取可用的颜色预设
     * @returns {string[]} 颜色名称数组
     */
    LanYan_plugin.getAvailableColors = function () {
        return plugin.getAvailableColors();
    }

    // ==================== 目标检测功能 ====================
    
    /**
     * 目标检测
     * @param {string} image 图像路径或base64
     * @returns {string} 检测结果的JSON字符串
     */
    LanYan_plugin.detection = function (image) {
        return plugin.detection(image);
    }

    // ==================== 滑块匹配功能 ====================
    
    /**
     * 滑块匹配 - 模板匹配算法
     * @param {string} targetImage 滑块图像路径
     * @param {string} backgroundImage 背景图像路径
     * @param {boolean} simpleTarget 是否为简单目标（可选，默认false）
     * @returns {number} 滑块位置x坐标
     */
    LanYan_plugin.slideMatch = function (targetImage, backgroundImage, simpleTarget) {
        if (simpleTarget === undefined) {
            return plugin.slideMatch(targetImage, backgroundImage);
        } else {
            return plugin.slideMatch(targetImage, backgroundImage, simpleTarget);
        }
    }
    
    /**
     * 滑块比较 - 差异比较算法
     * @param {string} targetImage 带缺口的图像路径
     * @param {string} backgroundImage 完整背景图像路径
     * @returns {number} 缺口位置x坐标
     */
    LanYan_plugin.slideComparison = function (targetImage, backgroundImage) {
        return plugin.slideComparison(targetImage, backgroundImage);
    }

    // ==================== 图像匹配功能 ====================
    
    /**
     * 图像位置匹配（原有功能保持）
     * @param {string} largeImagePath 大图路径
     * @param {string} smallImagePath 小图路径
     * @returns {string} 匹配位置信息
     */
    LanYan_plugin.ocrImage = function (largeImagePath, smallImagePath) {
        return plugin.findImagePosition(largeImagePath, smallImagePath);
    }

    // ==================== 便捷方法 ====================
    
    /**
     * 快速识别数字验证码
     * @param {string} image 图像路径或base64
     * @returns {string} 数字结果
     */
    LanYan_plugin.ocrNumber = function (image) {
        return plugin.ocrWithType(image, "number");
    }
    
    /**
     * 快速识别字母验证码
     * @param {string} image 图像路径或base64
     * @returns {string} 字母结果
     */
    LanYan_plugin.ocrLetter = function (image) {
        return plugin.ocrWithType(image, "letter");
    }
    
    /**
     * 快速识别英数验证码
     * @param {string} image 图像路径或base64
     * @returns {string} 英数结果
     */
    LanYan_plugin.ocrAlphanumeric = function (image) {
        return plugin.ocrWithType(image, "alphanumeric");
    }
    
    /**
     * 快速识别中文验证码
     * @param {string} image 图像路径或base64
     * @returns {string} 中文结果
     */
    LanYan_plugin.ocrChinese = function (image) {
        return plugin.ocrWithType(image, "chinese");
    }
    
    /**
     * 快速识别数学表达式
     * @param {string} image 图像路径或base64
     * @returns {string} 数学表达式结果
     */
    LanYan_plugin.ocrMath = function (image) {
        return plugin.ocrWithType(image, "math");
    }

    return LanYan_plugin;
}
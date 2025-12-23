# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ==================== ONNX Runtime 保护规则 ====================
# 保护 ONNX Runtime 相关类不被混淆
-keep class ai.onnxruntime.** { *; }
-keep class com.microsoft.onnxruntime.** { *; }
-keepclassmembers class ai.onnxruntime.** { *; }
-keepclassmembers class com.microsoft.onnxruntime.** { *; }

# 保护 JNI 方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保护枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==================== OpenCV 保护规则 ====================
# 保护 OpenCV 相关类
-keep class org.opencv.** { *; }
-keepclassmembers class org.opencv.** { *; }

# ==================== 项目特定保护规则 ====================
# 保护插件相关类
-keep class com.lanyan.ajpPlugin.ddddocr.plugin.** { *; }
-keepclassmembers class com.lanyan.ajpPlugin.ddddocr.plugin.** { *; }

# 保护 Auto.js 插件 SDK
-keep class org.autojs.plugin.sdk.** { *; }
-keepclassmembers class org.autojs.plugin.sdk.** { *; }

# 保护反射调用的类和方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 保护 Serializable 类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ==================== AndroidSVG 保护规则 ====================
# 保护 AndroidSVG 相关类
-keep class com.caverock.androidsvg.** { *; }
-keepclassmembers class com.caverock.androidsvg.** { *; }
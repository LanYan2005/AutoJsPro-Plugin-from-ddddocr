package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtException;
import java.nio.FloatBuffer;

public class ONNXRuntimeUtils implements AutoCloseable {
    private static OrtEnvironment globalEnv = null;
    private OrtEnvironment env;

    public ONNXRuntimeUtils() {
        try {
            // 使用单例模式创建全局环境
            if (globalEnv == null) {
                synchronized (ONNXRuntimeUtils.class) {
                    if (globalEnv == null) {
                        globalEnv = OrtEnvironment.getEnvironment();
                        Log.m16d("ONNXRuntimeUtils", "ONNX Runtime环境初始化成功");
                    }
                }
            }
            this.env = globalEnv;
        } catch (Exception e) {
            Log.m18e("ONNX Runtime环境初始化失败", e);
            throw new RuntimeException("ONNX Runtime初始化失败", e);
        }
    }

    public OrtSession createSession(String modelPath) {
        try {
            if (env == null) {
                throw new IllegalStateException("ONNX Runtime环境未初始化");
            }
            
            Log.m16d("ONNXRuntimeUtils", "正在创建ONNX会话: " + modelPath);
            OrtSession session = env.createSession(modelPath);
            Log.m16d("ONNXRuntimeUtils", "ONNX会话创建成功");
            return session;
        } catch (OrtException e) {
            Log.m18e("创建ONNX模型失败 - OrtException", e);
            return null;
        } catch (Exception e) {
            Log.m18e("创建ONNX模型失败 - 其他异常", e);
            return null;
        }
    }

    public OnnxTensor createTensor(FloatBuffer floatBuffer, long[] shape) {
        try {
            if (env == null) {
                throw new IllegalStateException("ONNX Runtime环境未初始化");
            }
            
            return OnnxTensor.createTensor(env, floatBuffer, shape);
        } catch (OrtException e) {
            Log.m18e("创建张量失败 - OrtException", e);
            return null;
        } catch (Exception e) {
            Log.m18e("创建张量失败 - 其他异常", e);
            return null;
        }
    }

    public OnnxTensor createTensor(float[] data, long[] shape) {
        return createTensor(FloatBuffer.wrap(data), shape);
    }

    @Override
    public void close() throws Exception {
        // 不关闭全局环境，让它在应用生命周期内保持活跃
        Log.m16d("ONNXRuntimeUtils", "ONNXRuntimeUtils实例关闭");
    }
    
    // 应用退出时调用此方法清理全局环境
    public static void cleanup() {
        if (globalEnv != null) {
            try {
                globalEnv.close();
                globalEnv = null;
                Log.m16d("ONNXRuntimeUtils", "全局ONNX Runtime环境已清理");
            } catch (Exception e) {
                Log.m18e("清理ONNX Runtime环境失败", e);
            }
        }
    }
}
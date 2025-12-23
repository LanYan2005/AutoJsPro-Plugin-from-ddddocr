package com.lanyan.ajpPlugin.ddddocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.DdddOcr;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private static boolean openCvAvailable = false;
    
    private ImageView ivPreview;
    private ImageView ivPreview2;
    private Button btnSelectImage;
    private Button btnSelectImage2;
    private Button btnRecognize;
    private RadioGroup rgFunction;
    private RadioButton rbOcr, rbColorFilter, rbDetection, rbSlideMatch, rbSlideComparison;
    private LinearLayout layoutColorOptions;
    private androidx.cardview.widget.CardView cardPreview2;
    private CheckBox cbRed, cbBlue, cbGreen, cbBlack, cbYellow, cbOrange;
    private TextView tvResult;
    private TextView tvTime;
    private TextView tvLog;
    
    private String currentImagePath;
    private String currentImagePath2;
    private DdddOcr ddddOcr;
    
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initDdddOcr();
        setupListeners();
        setupImagePicker();
    }
    
    private void initViews() {
        ivPreview = findViewById(R.id.ivPreview);
        ivPreview2 = findViewById(R.id.ivPreview2);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage2 = findViewById(R.id.btnSelectImage2);
        btnRecognize = findViewById(R.id.btnRecognize);
        rgFunction = findViewById(R.id.rgFunction);
        rbOcr = findViewById(R.id.rbOcr);
        rbColorFilter = findViewById(R.id.rbColorFilter);
        rbDetection = findViewById(R.id.rbDetection);
        rbSlideMatch = findViewById(R.id.rbSlideMatch);
        rbSlideComparison = findViewById(R.id.rbSlideComparison);
        layoutColorOptions = findViewById(R.id.layoutColorOptions);
        cardPreview2 = findViewById(R.id.cardPreview2);
        cbRed = findViewById(R.id.cbRed);
        cbBlue = findViewById(R.id.cbBlue);
        cbGreen = findViewById(R.id.cbGreen);
        cbBlack = findViewById(R.id.cbBlack);
        cbYellow = findViewById(R.id.cbYellow);
        cbOrange = findViewById(R.id.cbOrange);
        tvResult = findViewById(R.id.tvResult);
        tvTime = findViewById(R.id.tvTime);
        tvLog = findViewById(R.id.tvLog);
    }
    
    private void initDdddOcr() {
        addLog("初始化 DdddOcr...");
        
        // 尝试加载 OpenCV
        try {
            System.loadLibrary("opencv_java4");
            openCvAvailable = true;
            addLog("✅ OpenCV 加载成功");
        } catch (UnsatisfiedLinkError e) {
            openCvAvailable = false;
            addLog("⚠️ OpenCV 库未找到，滑块和颜色过滤功能不可用");
            addLog("提示：只有 OCR 和目标检测功能可用");
            
            // 禁用需要 OpenCV 的功能
            runOnUiThread(() -> {
                rbColorFilter.setEnabled(false);
                rbSlideMatch.setEnabled(false);
                rbSlideComparison.setEnabled(false);
                Toast.makeText(this, "OpenCV 未加载，部分功能不可用\n可用：OCR、目标检测", Toast.LENGTH_LONG).show();
            });
        }
        
        // 延迟初始化 DdddOcr，避免在主线程中进行耗时操作
        new Thread(() -> {
            try {
                addLog("正在初始化 ONNX Runtime...");
                ddddOcr = new DdddOcr(this, true, true);
                runOnUiThread(() -> {
                    addLog("✅ DdddOcr 初始化成功");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    addLog("❌ DdddOcr 初始化失败: " + e.getMessage());
                    Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void setupListeners() {
        rgFunction.setOnCheckedChangeListener((group, checkedId) -> {
            layoutColorOptions.setVisibility(View.GONE);
            btnSelectImage2.setVisibility(View.GONE);
            cardPreview2.setVisibility(View.GONE);
            
            if (checkedId == R.id.rbColorFilter) {
                layoutColorOptions.setVisibility(View.VISIBLE);
                btnSelectImage.setText("📷 选择图片");
            } else if (checkedId == R.id.rbSlideMatch) {
                btnSelectImage2.setVisibility(View.VISIBLE);
                cardPreview2.setVisibility(View.VISIBLE);
                btnSelectImage.setText("📷 选择滑块图");
                btnSelectImage2.setText("📷 选择背景图");
            } else if (checkedId == R.id.rbSlideComparison) {
                btnSelectImage2.setVisibility(View.VISIBLE);
                cardPreview2.setVisibility(View.VISIBLE);
                btnSelectImage.setText("📷 选择带缺口图");
                btnSelectImage2.setText("📷 选择完整图");
            } else {
                btnSelectImage.setText("📷 选择图片");
            }
            
            updateRecognizeButton();
        });
        
        btnSelectImage.setOnClickListener(v -> selectImage(1));
        btnSelectImage2.setOnClickListener(v -> selectImage(2));
        btnRecognize.setOnClickListener(v -> performRecognition());
    }
    
    private void updateRecognizeButton() {
        int checkedId = rgFunction.getCheckedRadioButtonId();
        boolean needsTwoImages = (checkedId == R.id.rbSlideMatch || checkedId == R.id.rbSlideComparison);
        
        if (needsTwoImages) {
            btnRecognize.setEnabled(currentImagePath != null && currentImagePath2 != null);
        } else {
            btnRecognize.setEnabled(currentImagePath != null);
        }
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleSelectedImage(imageUri, 1);
                    }
                }
            }
        );
        
        imagePickerLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleSelectedImage(imageUri, 2);
                    }
                }
            }
        );
    }
    
    private void selectImage(int imageNumber) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        if (imageNumber == 1) {
            imagePickerLauncher.launch(Intent.createChooser(intent, "选择图片1"));
        } else {
            imagePickerLauncher2.launch(Intent.createChooser(intent, "选择图片2"));
        }
    }
    
    private void handleSelectedImage(Uri imageUri, int imageNumber) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            if (imageNumber == 1) {
                ivPreview.setImageBitmap(bitmap);
                currentImagePath = imageUri.toString();
                addLog("✅ 图片1已选择: " + imageUri.getLastPathSegment());
            } else {
                ivPreview2.setImageBitmap(bitmap);
                currentImagePath2 = imageUri.toString();
                addLog("✅ 图片2已选择: " + imageUri.getLastPathSegment());
            }
            
            updateRecognizeButton();
            tvResult.setText("等待识别...");
            tvTime.setText("耗时: --");
            
        } catch (Exception e) {
            addLog("❌ 图片" + imageNumber + "加载失败: " + e.getMessage());
            Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void performRecognition() {
        if (currentImagePath == null) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (ddddOcr == null) {
            Toast.makeText(this, "DdddOcr 未初始化，请重启应用", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnRecognize.setEnabled(false);
        tvResult.setText("识别中...");
        addLog("开始识别...");
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            String result = "";
            
            try {
                Uri uri = Uri.parse(currentImagePath);
                InputStream inputStream = getContentResolver().openInputStream(uri);
                
                String tempPath = getCacheDir() + "/temp_ocr_image.jpg";
                java.io.FileOutputStream fos = new java.io.FileOutputStream(tempPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                inputStream.close();
                
                int checkedId = rgFunction.getCheckedRadioButtonId();
                
                if (checkedId == R.id.rbOcr) {
                    result = ddddOcr.classification(tempPath);
                    addLog("OCR 识别完成");
                    
                } else if (checkedId == R.id.rbColorFilter) {
                    if (!openCvAvailable) {
                        result = "颜色过滤功能需要 OpenCV 库";
                    } else {
                        List<String> colors = getSelectedColors();
                        if (colors.isEmpty()) {
                            result = "请至少选择一种颜色";
                        } else {
                            result = ddddOcr.classification(tempPath, colors.toArray(new String[0]));
                            addLog("颜色过滤 OCR 完成，颜色: " + colors);
                        }
                    }
                    
                } else if (checkedId == R.id.rbDetection) {
                    List<int[]> boxes = ddddOcr.detection(tempPath);
                    if (boxes != null && !boxes.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("检测到 ").append(boxes.size()).append(" 个目标:\n\n");
                        for (int i = 0; i < boxes.size(); i++) {
                            int[] box = boxes.get(i);
                            sb.append(String.format("目标 %d: [%d, %d, %d, %d]\n", 
                                i + 1, box[0], box[1], box[2], box[3]));
                        }
                        result = sb.toString();
                    } else {
                        result = "未检测到目标";
                    }
                    addLog("目标检测完成");
                    
                } else if (checkedId == R.id.rbSlideMatch) {
                    if (!openCvAvailable) {
                        result = "滑块匹配功能需要 OpenCV 库";
                    } else if (currentImagePath2 == null) {
                        result = "请选择第二张图片（背景图）";
                    } else {
                        Uri uri2 = Uri.parse(currentImagePath2);
                        InputStream inputStream2 = getContentResolver().openInputStream(uri2);
                        String tempPath2 = getCacheDir() + "/temp_ocr_image2.jpg";
                        java.io.FileOutputStream fos2 = new java.io.FileOutputStream(tempPath2);
                        byte[] buffer2 = new byte[1024];
                        int length2;
                        while ((length2 = inputStream2.read(buffer2)) > 0) {
                            fos2.write(buffer2, 0, length2);
                        }
                        fos2.close();
                        inputStream2.close();
                        
                        int x = ddddOcr.slideMatch(tempPath, tempPath2, false);
                        if (x >= 0) {
                            result = String.format("✅ 滑块位置: %d\n\n需要滑动 %d 像素", x, x);
                        } else {
                            result = "❌ 滑块匹配失败";
                        }
                        addLog("滑块匹配完成，位置: " + x);
                    }
                    
                } else if (checkedId == R.id.rbSlideComparison) {
                    if (!openCvAvailable) {
                        result = "滑块比较功能需要 OpenCV 库";
                    } else if (currentImagePath2 == null) {
                        result = "请选择第二张图片（完整图）";
                    } else {
                        Uri uri2 = Uri.parse(currentImagePath2);
                        InputStream inputStream2 = getContentResolver().openInputStream(uri2);
                        String tempPath2 = getCacheDir() + "/temp_ocr_image2.jpg";
                        java.io.FileOutputStream fos2 = new java.io.FileOutputStream(tempPath2);
                        byte[] buffer2 = new byte[1024];
                        int length2;
                        while ((length2 = inputStream2.read(buffer2)) > 0) {
                            fos2.write(buffer2, 0, length2);
                        }
                        fos2.close();
                        inputStream2.close();
                        
                        int x = ddddOcr.slideComparison(tempPath, tempPath2);
                        if (x >= 0) {
                            result = String.format("✅ 缺口位置: %d\n\n需要滑动 %d 像素", x, x);
                        } else {
                            result = "❌ 缺口检测失败";
                        }
                        addLog("滑块比较完成，位置: " + x);
                    }
                }
                
            } catch (Exception e) {
                result = "识别失败: " + e.getMessage();
                addLog("❌ 识别异常: " + e.getMessage());
                e.printStackTrace();
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            String finalResult = result;
            runOnUiThread(() -> {
                tvResult.setText(finalResult);
                tvTime.setText(String.format("耗时: %d ms", duration));
                btnRecognize.setEnabled(true);
                addLog(String.format("✅ 识别完成，耗时: %d ms", duration));
            });
        }).start();
    }
    
    private List<String> getSelectedColors() {
        List<String> colors = new ArrayList<>();
        if (cbRed.isChecked()) colors.add("red");
        if (cbBlue.isChecked()) colors.add("blue");
        if (cbGreen.isChecked()) colors.add("green");
        if (cbBlack.isChecked()) colors.add("black");
        if (cbYellow.isChecked()) colors.add("yellow");
        if (cbOrange.isChecked()) colors.add("orange");
        return colors;
    }
    
    private void addLog(String message) {
        runOnUiThread(() -> {
            try {
                if (tvLog == null) {
                    return;
                }
                String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String logMessage = String.format("[%s] %s\n", timestamp, message);
                tvLog.append(logMessage);
                
                final TextView tv = tvLog;
                tv.post(() -> {
                    try {
                        if (tv.getLayout() != null) {
                            int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
                            if (scrollAmount > 0) {
                                tv.scrollTo(0, scrollAmount);
                            }
                        }
                    } catch (Exception e) {
                        // 忽略滚动错误
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
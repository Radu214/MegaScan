package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.megascan.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ScanBarcodeActivity extends AppCompatActivity {

    private PreviewView previewView;
    private View overlay;
    private ImageView iconProfile, iconCart, iconScan, iconPromo, iconAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        previewView = findViewById(R.id.previewView);
        overlay = findViewById(R.id.overlay);

        iconProfile = findViewById(R.id.icon_profile);
        iconCart = findViewById(R.id.icon_cart);
        iconScan = findViewById(R.id.icon_scan);
        iconPromo = findViewById(R.id.icon_promo);
        iconAI = findViewById(R.id.icon_ai);

        setupBottomNav();
        startCamera();
    }

    private void setupBottomNav() {
        iconProfile.setOnClickListener(v -> {
            // Navigate to profile page
            Toast.makeText(this, "Profile Page", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, ProfileActivity.class));
        });

        iconCart.setOnClickListener(v -> {
            // Navigate to shopping cart page
            Toast.makeText(this, "Cart Page", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, CartActivity.class));
        });

        iconScan.setOnClickListener(v -> {
            // Already on Scan page, or refresh?
            Toast.makeText(this, "Already on Scan Page", Toast.LENGTH_SHORT).show();
        });

        iconPromo.setOnClickListener(v -> {
            // Navigate to promo page
            Toast.makeText(this, "Promo Page", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, PromoActivity.class));
        });

        iconAI.setOnClickListener(v -> {
            // Navigate to AI assistant page
            Toast.makeText(this, "AI Assistant Page", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, AiAssistantActivity.class));
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }, getMainExecutor());
        }
    }

    void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // If you need ImageCapture or ImageAnalysis, set them up here:
        // ImageCapture imageCapture = new ImageCapture.Builder().build();
        // ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

        // Bind to lifecycle
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }
}

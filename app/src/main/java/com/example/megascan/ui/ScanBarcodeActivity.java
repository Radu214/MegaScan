package com.example.megascan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.megascan.R;
import com.example.megascan.model.Cart;
import com.example.megascan.model.Produs;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull; // If using JetBrains annotations
import java.io.IOException;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;




public class ScanBarcodeActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 1001;
    private PreviewView previewView;
    private ImageView iconProfile, iconCart, iconScan, iconPromo, iconAI;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private boolean isScanning = true; // Control flag to prevent multiple popups for the same code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        previewView = findViewById(R.id.previewView);
        iconProfile = findViewById(R.id.icon_profile);
        iconCart = findViewById(R.id.icon_cart);
        iconScan = findViewById(R.id.icon_scan);
        iconPromo = findViewById(R.id.icon_promo);
        iconAI = findViewById(R.id.icon_ai);

        cameraExecutor = Executors.newSingleThreadExecutor();
        barcodeScanner = BarcodeScanning.getClient();

        setupBottomNav();
        checkCameraPermissionAndStart();
    }

    private void setupBottomNav() {
        iconCart.setOnClickListener(v -> {
            Intent intent = new Intent(ScanBarcodeActivity.this, CartActivity.class);
            startActivity(intent);});

        iconProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ScanBarcodeActivity.this, ProfileActivity.class);
            startActivity(intent);});

        iconScan.setOnClickListener(v -> Toast.makeText(this, "Already on Scan Page", Toast.LENGTH_SHORT).show());
        iconPromo.setOnClickListener(v -> Toast.makeText(this, "Promo Page", Toast.LENGTH_SHORT).show());
        iconAI.setOnClickListener(v -> Toast.makeText(this, "AI Assistant Page", Toast.LENGTH_SHORT).show());
    }

    private void checkCameraPermissionAndStart() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreviewAndAnalyzer(cameraProvider);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, new Handler(Looper.getMainLooper())::post);
    }

    private void bindPreviewAndAnalyzer(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new BarcodeAnalyzer());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
        @Override
        @ExperimentalGetImage
        public void analyze(@NonNull ImageProxy image) {
            if (!isScanning) {
                image.close();
                return;
            }

            @androidx.annotation.OptIn(markerClass = ExperimentalGetImage.class)
            android.media.Image mediaImage = image.getImage();
            if (mediaImage != null) {
                InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
                barcodeScanner.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                            @Override
                            public void onSuccess(List<Barcode> barcodes) {
                                if (!barcodes.isEmpty()) {
                                    // Get the first barcode and display a popup
                                    Barcode barcode = barcodes.get(0);
                                    String rawValue = barcode.getRawValue();

                                    // Prevent scanning while popup is visible
                                    isScanning = false;

                                    runOnUiThread(() -> showBarcodeDialog(rawValue));
                                }
                                image.close();
                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            image.close();
                        });
            } else {
                image.close();
            }
        }
    }

    private void showBarcodeDialog(String barcodeValue) {
        OkHttpClient client = new OkHttpClient();

        // Suppose you have a scannedBarcode string from your previous scanning logic
        final String scannedBarcode = barcodeValue; // This should come from your scanning result

        Request request = new Request.Builder()
                .url("http://10.200.20.238:3000/data") // Replace with your server's IP and port
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                String errorMessage = "F " + e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(ScanBarcodeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
                runOnUiThread(() -> {
                    new AlertDialog.Builder(ScanBarcodeActivity.this)
                            .setTitle("Connection Error")
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setCancelable(false)
                            .show();
                });
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle server error
                    runOnUiThread(() -> {
                        Toast.makeText(ScanBarcodeActivity.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                final String jsonData = response.body().string();

                //Bring the cart
                Cart cart = Cart.getInstance();

                // Parse JSON
                ObjectMapper objectMapper = new ObjectMapper();

                try {

                    List<Produs> produse = objectMapper.readValue(jsonData, new TypeReference<List<Produs>>() {});
                    // Print the list of Produs objects
                    Produs matchedProduct = produse.stream()
                            .filter(p -> p.getCod().equals(scannedBarcode))
                            .findFirst()
                            .orElse(null);
                    if (matchedProduct != null) {
                        // Add the matched product to the cart
                        // addToCart(matchedProduct) should be a method you define to handle adding the product to the cart


                        runOnUiThread(() -> {
                            new AlertDialog.Builder(ScanBarcodeActivity.this)
                                    .setTitle("New product")
                                    .setMessage("Do you want to add the following product to cart?\n " + matchedProduct.getDenumire())
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        dialog.dismiss();
                                        cart.addToCart(matchedProduct);
                                        isScanning = true;
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                        isScanning = true;
                                    })
                                    .setCancelable(false)
                                    .show();
                        });
                    } else {
                        // No matching product found
                        runOnUiThread(() -> {
                            Toast.makeText(ScanBarcodeActivity.this, "No product found for this barcode", Toast.LENGTH_SHORT).show();
                        });

                    }
                    isScanning = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(ScanBarcodeActivity.this)
                                .setTitle("Connection Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    //isScanning = true;
                                })
                                .setCancelable(false)
                                .show();
                    });
                }


                 //Find product by scannedBarcode

            }
        });
    }


}

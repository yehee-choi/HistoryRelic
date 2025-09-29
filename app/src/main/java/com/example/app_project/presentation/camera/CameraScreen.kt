package com.example.app_project.presentation.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraScreen(
    onBackClick: () -> Unit = {},
    onPhotoTaken: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var camera: Camera? by remember { mutableStateOf(null) }
    var isCameraReady by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        Log.d("CameraScreen", "Camera permission granted: $isGranted")
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        PermissionDeniedScreen(onBackClick = onBackClick)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onImageCaptureReady = { capture, cam ->
                    imageCapture = capture
                    camera = cam
                    isCameraReady = true
                    Log.d("CameraScreen", "Camera is ready")
                }
            )

            // 상단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color.White
                    )
                }

                Text(
                    text = "지문 촬영",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        camera?.cameraControl?.enableTorch(isFlashOn)
                        Log.d("CameraScreen", "Flash toggled: $isFlashOn")
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "플래시",
                        tint = Color.White
                    )
                }
            }

            // 가이드 프레임
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.4f)
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = "지문을 프레임 안에 맞춰주세요",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-24).dp)
                            .background(
                                Color.Black.copy(alpha = 0.7f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // 하단 촬영 버튼
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        Log.d("CameraScreen", "Capture button clicked")
                        Log.d("CameraScreen", "Camera ready: $isCameraReady, ImageCapture: ${imageCapture != null}")

                        imageCapture?.let { capture ->
                            takePhoto(
                                imageCapture = capture,
                                context = context,
                                onPhotoTaken = { uri ->
                                    Log.d("CameraScreen", "Photo taken: $uri")
                                    onPhotoTaken(uri)
                                }
                            )
                        } ?: run {
                            Log.e("CameraScreen", "ImageCapture is null")
                        }
                    },
                    modifier = Modifier.size(80.dp),
                    containerColor = if (isCameraReady) Color.White else Color.Gray,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "촬영하기",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture, Camera) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val imageCapture = ImageCapture.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                    Log.d("CameraPreview", "Camera bound successfully")
                    onImageCaptureReady(imageCapture, camera)

                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

@Composable
private fun PermissionDeniedScreen(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "카메라 권한이 필요합니다",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "지문을 촬영하기 위해 카메라 권한을 허용해주세요",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("확인")
            }
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onPhotoTaken: (Uri) -> Unit
) {
    Log.d("CameraScreen", "takePhoto called")

    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())

    // 두 가지 방식 모두 시도 가능하도록 수정
    try {
        // 방법 1: MediaStore 사용 (수정된 컬럼명)
        val contentValues = androidx.core.content.contentValuesOf(
            MediaStore.Images.Media.DISPLAY_NAME to "$name.jpg",
            MediaStore.Images.Media.MIME_TYPE to "image/jpeg",
            MediaStore.Images.Media.RELATIVE_PATH to Environment.DIRECTORY_PICTURES
        )

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraScreen", "Photo capture failed: ${exception.message}", exception)
                    // 실패 시 내부 저장소로 재시도
                    takePhotoToInternalStorage(imageCapture, context, name, onPhotoTaken)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("CameraScreen", "Photo captured successfully: ${output.savedUri}")
                    output.savedUri?.let(onPhotoTaken)
                }
            }
        )
    } catch (e: Exception) {
        Log.e("CameraScreen", "Failed to set up photo capture", e)
        // 실패 시 내부 저장소로 재시도
        takePhotoToInternalStorage(imageCapture, context, name, onPhotoTaken)
    }
}

private fun takePhotoToInternalStorage(
    imageCapture: ImageCapture,
    context: Context,
    name: String,
    onPhotoTaken: (Uri) -> Unit
) {
    Log.d("CameraScreen", "Trying internal storage method")

    val photoFile = File(
        context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
        "$name.jpg"
    )

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed (internal): ${exception.message}", exception)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "Photo saved to internal storage: ${photoFile.absolutePath}")
                onPhotoTaken(Uri.fromFile(photoFile))
            }
        }
    )
}
package com.example.nnapiapp

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.nnapiapp.ui.theme.NNAPIAppTheme
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {
    private lateinit var imageClassifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NNAPIAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
        imageClassifier = ImageClassifier(ModelLoader().loadModelFile(assets)) // Initialize ImageClassifier
    }

    private fun preprocessImage(image: Bitmap): ByteBuffer? {
        if (image.isRecycled) {
            return null
        }

        val numBytes = image.rowBytes * image.height

        // Allocate a ByteBuffer with a capacity sufficient to hold the image data
        val byteBuffer = ByteBuffer.allocateDirect(numBytes)

        // Copy the pixels of the image to the byteBuffer
        image.copyPixelsToBuffer(byteBuffer)
        return byteBuffer
    }

    // Define a method to handle classification and update UI
    private fun classifyImage(context: Context, bitmap: Bitmap) {
        val imageBuffer = preprocessImage(bitmap)
        val probabilities = imageClassifier.classifyImage(imageBuffer ?: ByteBuffer.allocate(0))

        val maxProbabilityIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val highestProbabilityLabel = "Class ${maxProbabilityIndex + 1}"

        classificationResult = highestProbabilityLabel
    }

    var classificationResult by mutableStateOf("")

    @Composable
    fun Content() {
        val context = LocalContext.current // Get the context from LocalContext
        MainScreen(context)
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun MainScreen(context: Context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

            val pickImage = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { selectedUri ->
                    val bitmap = context.contentResolver.openInputStream(selectedUri)?.use {
                        BitmapFactory.decodeStream(it)
                    }

                    bitmap?.let {
                        classifyImage(context, it)
                    }

                    imageUri = selectedUri // Update the imageUri with the selected image URI
                    capturedBitmap = null // Reset the captured bitmap
                }
            }

            val imageCapture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
                bitmap?.let {
                    classifyImage(context, it)
                }

                // Set the captured bitmap and clear the imageUri
                capturedBitmap = bitmap
                imageUri = null
            }

            val requestPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    imageCapture.launch()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                // Display the image from URI if available, otherwise display the captured bitmap
                if (imageUri != null) {
                    Image(
                        painter = rememberImagePainter(data = imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    capturedBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Button(
                onClick = { pickImage.launch("image/*") }
            ) {
                Text(text = "Capture or Select Image")
            }

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        imageCapture.launch()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Text(text = "Capture Image from Camera")
            }

            // Display the classification result in the UI
            Text(text = "Classification Result: $classificationResult")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        imageClassifier.close() // Close the ImageClassifier on activity destroy
    }

    private fun createTempUri(context: Context): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "temp_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
    }
}

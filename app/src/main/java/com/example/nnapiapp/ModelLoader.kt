package com.example.nnapiapp

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ModelLoader {
    // Load the TensorFlow Lite model from assets
    fun loadModelFile(assetManager: AssetManager): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assetManager.openFd("1.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}

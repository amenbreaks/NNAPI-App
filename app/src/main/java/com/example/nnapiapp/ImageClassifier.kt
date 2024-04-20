package com.example.nnapiapp

import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import kotlin.math.min

class ImageClassifier(modelBuffer: ByteBuffer) {

    private val interpreter: Interpreter = Interpreter(modelBuffer)
    private val inputBuffer: ByteBuffer
    private val outputBuffer: ByteBuffer
    private val executor = Executors.newSingleThreadExecutor()

    init {
        // Determine the input and output tensor shapes
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()

        // Calculate the size of the input and output buffers
        val inputSize = inputShape[1] * inputShape[2] * inputShape[3] * FLOAT_BYTES
        val outputSize = outputShape[1] * FLOAT_BYTES

        // Initialize input and output buffers
        inputBuffer = ByteBuffer.allocateDirect(inputSize).apply {
            order(ByteOrder.nativeOrder())
        }
        outputBuffer = ByteBuffer.allocateDirect(outputSize).apply {
            order(ByteOrder.nativeOrder())
        }
    }

    // Method to close the interpreter
    fun close() {
        interpreter.close()
    }

    // Method to classify the image
    fun classifyImage(imageBuffer: ByteBuffer): FloatArray {
        executor.execute {
            // Preprocess the image and copy it to the input buffer
            imageBuffer.rewind()
            inputBuffer.rewind()
            inputBuffer.put(imageBuffer)

            // Run inference
            interpreter.run(inputBuffer, outputBuffer)

            // Extract the output probabilities
            val probabilities = FloatArray(outputBuffer.remaining() / FLOAT_BYTES)
            outputBuffer.rewind()
            outputBuffer.asFloatBuffer().get(probabilities)
        }
        return floatArrayOf() // Return an empty array temporarily, actual classification will be handled asynchronously
    }

    companion object {
        private const val FLOAT_BYTES = 4 // Size of float in bytes
    }
}

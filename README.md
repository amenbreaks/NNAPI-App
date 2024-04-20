# NNAPIApp

NNAPIApp is an Android application that demonstrates image classification using neural networks. It allows users to classify images from either the device's camera or the gallery using a pre-trained neural network model.

## Features

- **Image Classification**: Users can capture or select images from the gallery and classify them using a pre-trained neural network model.
- **Camera Integration**: The app provides the option to capture images directly from the device's camera for classification.
- **Real-time Classification**: Images are classified in real-time as they are captured or selected, providing instant feedback to the user.
- **Display Classification Result**: The app displays the classification result, indicating the class or label assigned to the input image by the neural network model.
- **Support for Different Models**: The app supports the integration of different pre-trained neural network models for image classification.

## Components

### MainActivity

The main entry point of the Android application. It initializes the neural network model and manages the UI components.

### ImageClassifier

Responsible for loading the pre-trained neural network model and performing image classification. This class contains methods for preprocessing images, loading the model file, and performing inference.

### ModelLoader

Loads the neural network model file from the assets directory. It provides a method to load the model file into memory for use by the ImageClassifier.

### Content

Defines the UI layout and logic for the application's main screen. It uses Jetpack Compose to create a responsive and dynamic user interface.

### MainScreen

Displays the main user interface, including buttons for image capture and selection, image preview, and classification result. It interacts with the ImageClassifier to classify images and update the UI accordingly.

## Functionality

1. **Image Capture**: Users can capture images using the device's camera by tapping the "Capture Image from Camera" button. The captured image is then classified using the neural network model.
2. **Image Selection**: Users can select images from the device's gallery by tapping the "Capture or Select Image" button. The selected image is then classified using the neural network model.
3. **Real-time Classification**: Image classification occurs in real-time as images are captured or selected, providing instant feedback to the user.
4. **Display Classification Result**: The classification result, indicating the class or label assigned to the input image by the neural network model, is displayed on the screen.
5. **Support for Different Models**: The application supports the integration of different pre-trained neural network models for image classification. Users can replace the existing model with a different one by following the appropriate steps.

## TensorFlow Lite and NNAPI Integration

The NNAPIApp uses TensorFlow Lite for inference, which leverages Android's Neural Networks API (NNAPI) for hardware acceleration on compatible devices. Here's how TensorFlow Lite uses NNAPI underneath:

1. **Model Conversion**: The neural network model is converted to the TensorFlow Lite format, which is optimized for mobile deployment.
2. **NNAPI Delegate**: TensorFlow Lite provides an NNAPI delegate that allows inference operations to be offloaded to NNAPI for hardware acceleration.
3. **Inference**: When performing inference on the device, TensorFlow Lite utilizes the NNAPI delegate to execute neural network operations using hardware acceleration, resulting in faster inference times.
4. **Compatibility**: TensorFlow Lite automatically checks for NNAPI support on the device. If NNAPI is available, it seamlessly integrates with TensorFlow Lite for optimized performance. If NNAPI is not available, TensorFlow Lite falls back to CPU execution.

## Installation

To run the NNAPIApp on your Android device or emulator:

1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the project on your device or emulator.

## Requirements

- Android Studio
- Android device or emulator with Android OS version compatible with the project's minimum SDK version.


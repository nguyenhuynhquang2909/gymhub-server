import tensorflow as tf

# Load the trained model from the .h5 file
model = tf.keras.models.load_model('model.h5')

# Apply post-training quantization
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.optimizations = [tf.lite.Optimize.DEFAULT]

# Enable resource variables if needed
converter.experimental_enable_resource_variables = True

# Allow select TensorFlow ops to handle complex operations
converter.target_spec.supported_ops = [
    tf.lite.OpsSet.TFLITE_BUILTINS,  # Default TensorFlow Lite ops.
    tf.lite.OpsSet.SELECT_TF_OPS  # Enable TensorFlow ops that are not natively supported in TensorFlow Lite.
]

# Disable lowering of tensor list operations
converter._experimental_lower_tensor_list_ops = False

# Convert the model to a TensorFlow Lite model
tflite_model = converter.convert()

# Save the quantized model to a file
with open('model_quantized.tflite', 'wb') as f:
    f.write(tflite_model)
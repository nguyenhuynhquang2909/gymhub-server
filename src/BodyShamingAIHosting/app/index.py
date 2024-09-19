from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
import pandas as pd
from tensorflow.keras.layers import TextVectorization  # Import TextVectorization

# Initialize Flask app
app = Flask(__name__)

# Load the TensorFlow Lite model
interpreter = tf.lite.Interpreter(model_path='model_quantized.tflite')
interpreter.allocate_tensors()

# Get input and output details
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Load the dataset again to re-adapt the vectorizer
# Assuming the same dataset used during training is available
df = pd.read_csv('comments.csv')
X = df['comments']  # Use the 'comments' column for re-adaptation

# Initialize the vectorizer with the same configuration
MAX_FEATURES = 100000
vectorizer = TextVectorization(max_tokens=MAX_FEATURES,
                               output_sequence_length=1800,
                               output_mode='int')

# Re-adapt the vectorizer using the original dataset
vectorizer.adapt(X.values)

@app.route('/predict', methods=['POST'])
def predict():
    # Get data from POST request
    data = request.json
    
    # Assuming the input data is a string
    input_string = data['input']

    # Preprocess the input string to match your model's input requirements
    input_data = preprocess_input_string(input_string)

    # Set the input tensor
    interpreter.set_tensor(input_details[0]['index'], input_data)

    # Perform inference
    interpreter.invoke()

    # Get the output tensor
    prediction = interpreter.get_tensor(output_details[0]['index'])

    # Convert prediction to a Python list and return as JSON
    return jsonify({'prediction': prediction.tolist()})

def preprocess_input_string(input_string):
    # Convert input_string to the format expected by the model
    input_tensor = vectorizer([input_string])  # The vectorizer is now re-adapted
    input_tensor = np.array(input_tensor, dtype=np.float32)
    return input_tensor

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)
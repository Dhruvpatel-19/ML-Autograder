import ast
import re

import joblib
import pandas as pd
import uvicorn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from scipy.sparse import hstack, csr_matrix

app = FastAPI()

import io
from contextlib import redirect_stdout

def is_code_correct(code, expected_output):
    try:
        # Prepare the environment for code execution
        local_env = {}

        # Redirect stdout to capture print statements
        with io.StringIO() as buf, redirect_stdout(buf):
            # Execute the provided code
            exec(code, globals(), local_env)

            # Get the output from the buffer
            output = buf.getvalue().strip()

        # Compare the captured output to the expected output
        return output == str(expected_output)
    except Exception as e:
        # Optionally log the exception
        print(f"Error: {e}")
        return False

def extract_features(code, expected_output):
    features = {}

    # Code Length
    features['code_length'] = len(code.splitlines())

    # Number of Functions
    features['num_functions'] = len([node for node in ast.walk(ast.parse(code)) if isinstance(node, ast.FunctionDef)])

    # Number of Comments
    features['num_comments'] = len(re.findall(r'#.*', code))

    # Cyclomatic Complexity (basic example)
    features['complexity'] = len(re.findall(r'\b(if|for|while|try|except|with|def)\b', code))

    # Number of Import Statements
    features['num_imports'] = len(re.findall(r'\bimport\b', code))

    # Code Correctness
    features['is_correct'] = is_code_correct(code, expected_output)

    return features
# Load the model and vectorizer
with open('code_scoring_model.pkl', 'rb') as model_file:
    model = joblib.load(model_file)

with open('tfidf_vectorizer.pkl', 'rb') as vectorizer_file:
    tfidf = joblib.load(vectorizer_file)

class PredictionRequest(BaseModel):
    instruction: str
    output: str
    code_style: float
    # expected_output: str

@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.post("/predict")
async def predict_score(request: PredictionRequest):
    try:
        # Prepare input text data
        new_instruction = request.instruction
        new_output = request.output
        # new_expected_output = request.expected_output
        new_code_style = request.code_style

        # Vectorize the new instruction and output
        new_text_features = tfidf.transform([new_instruction + ' ' + new_output])

        # Extract additional features for the new data
        new_additional_features = extract_features(new_output, "15")  # Assuming expected output is 5 for factorial(3)
        new_additional_features_df = pd.DataFrame([new_additional_features])
        new_additional_features_df['is_correct'] = new_additional_features_df['is_correct'].astype(int)
        new_additional_features_sparse = csr_matrix(new_additional_features_df)

        # Create a DataFrame for the numerical feature
        new_num_features = pd.DataFrame([[new_code_style]], columns=['Code Style'])
        new_num_features_sparse = csr_matrix(new_num_features)

        # Combine all features
        new_data_combined = hstack([new_text_features, new_num_features_sparse, new_additional_features_sparse])

        # Predict the score
        predicted_score = model.predict(new_data_combined)

        return {"predicted_score": predicted_score[0]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == '__main__':
    uvicorn.run(app, host='127.0.0.1', port=5000)

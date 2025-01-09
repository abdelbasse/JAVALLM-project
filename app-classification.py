from flask import Flask, request, jsonify
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import json

# Initialize Flask app
app = Flask(__name__)

class PromptClassifier:
    def __init__(self, model_name="distilbert-base-uncased", num_labels=4):
        """
        Initializes the prompt classifier with the specified model and tokenizer.
        """
        self.model_name = model_name
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModelForSequenceClassification.from_pretrained(model_name, num_labels=num_labels)
        
        # Define categories
        self.categories = [
            "Generating Code",  # Category for code generation/fixing
            "Debugging or Explaining Concept",  # Category for debugging or explaining concepts
            "Generate and Explain Code",  # Category for generating and explaining code together
            "None"  # Category for unrelated prompts
        ]

    def refine_prompt(self, prompt):
        """
        Refines the prompt to provide the model with clear instructions for classification.
        Handles the case where the prompt is about code generation/fixing or concepts.
        """
        # Check for language-related context (e.g., Java)
        if "java" in prompt.lower():
            language_specification = "Java"
        else:
            language_specification = "Not specified"

        # Refine the prompt to guide the model
        return (
            f"Classify the following programming-related prompt as one of these categories:\n"
            "- Generating Code (requests asking to generate, fix, or improve Java code)\n"
            "- Debugging or Explaining Concept (requests asking to explain why Java code isn't working or to explain programming concepts like hashmaps, lists, etc.)\n"
            "- Generate and Explain Code (requests asking to generate Java code and explain why original Java code isn't working)\n"
            "- None (requests unrelated to Java programming or non-Java code)\n"
            f"Language specified: {language_specification}\n"
            f"Prompt: '{prompt}'"
        )

    def classify_prompt(self, prompt):
        """
        Classifies the given prompt into one of the defined categories.
        """
        refined_prompt = self.refine_prompt(prompt)

        # Tokenize the input
        inputs = self.tokenizer(refined_prompt, return_tensors="pt", truncation=True, max_length=512)

        # Get model predictions
        with torch.no_grad():
            logits = self.model(**inputs).logits
            probabilities = torch.softmax(logits, dim=1).squeeze().tolist()

        # Map probabilities to categories
        result = {
            "Probabilities": {self.categories[i]: probabilities[i] for i in range(len(self.categories))},
            "Predicted Category": self.categories[probabilities.index(max(probabilities))],
        }

        return result

# Initialize the classifier
classifier = PromptClassifier()

@app.route('/text-classifier')
def index():
    """
    Simple route to show that the service is running.
    """
    return "text-classification is running!"

@app.route('/text-classifier/prompt', methods=['POST'])
def classify_prompt_api():
    data = request.get_json()
    prompt = data.get("prompt", "")

    if not prompt:
        return jsonify({"error": "Prompt is required"}), 400

    # Classify the prompt
    classification_result = classifier.classify_prompt(prompt)

    # Return the classification result
    return jsonify(classification_result)

if __name__ == '__main__':
    # Run the Flask app on all IPs (0.0.0.0) and port 6000
    app.run(host='0.0.0.0', port=6000)



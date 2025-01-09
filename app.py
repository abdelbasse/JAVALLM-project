from flask import Flask, request, jsonify
import chromadb

app = Flask(__name__)

# Initialize ChromaDB clients for each collection
chroma_clients = {
    "JavaCodeDebuggingDB": chromadb.PersistentClient(path="JavaCodeDebugging-store-vdb"),
    "JavaCodeSnippetsDB": chromadb.PersistentClient(path="JavaCodeSnippets-store-vdb")
}

collections = {
    "JavaCodeDebuggingDB": chroma_clients["JavaCodeDebuggingDB"].get_collection("JavaCodeDebuggingDB"),
    "JavaCodeSnippetsDB": chroma_clients["JavaCodeSnippetsDB"].get_collection("JavaCodeSnippetsDB")
}

@app.route('/')
def home():
    return "ChromaDB provider is running!"

@app.route('/query', methods=['POST'])
def query_vector_db():
    data = request.json
    collection_name = data.get("collection_name")
    query = data.get("query")
    n_results = data.get("nbr_results", 5)  # Default to 5 if not specified

    if collection_name not in collections:
        return jsonify({"error": "Collection not found"}), 404

    collection = collections[collection_name]

    try:
        # Retrieve similar items from the collection
        results = collection.query(query_texts=[query], n_results=n_results)
        return jsonify({"results": results})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    print("Starting Flask app on 0.0.0.0:5000...")
    app.run(host="0.0.0.0", port=5000)

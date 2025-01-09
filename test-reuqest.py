import requests

# URL of your API
url = "http://localhost:1234/api/llm"

# Headers to specify streaming
headers = {
    "Content-Type": "application/json",
    "X-Stream-Mode": "true"
}

# The prompt you want to send
data = {
    "prompt": "Tell me a story about a robot"
}

# Send the POST request with streaming enabled
response = requests.post(url, json=data, headers=headers, stream=True)

# Check if the response is chunked (HTTP/1.1 with Transfer-Encoding: chunked)
if response.status_code == 200:
    print("Receiving chunks:")
    # Iterate over the response content by chunk
    for chunk in response.iter_lines(decode_unicode=True):
        if chunk:  # Ignore empty chunks
            print(chunk)
else:
    print(f"Failed to get a valid response: {response.status_code}")


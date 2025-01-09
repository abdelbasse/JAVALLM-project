import gdown

# File ID from the Google Drive link
file_id = "1fegS7hqSA50ZFc_rmIQL9azAAhVDwewX"
destination = "llmjava-backend.rar"  # Name of the file to save locally

# URL format for gdown
url = f"https://drive.google.com/uc?id={file_id}"

# Download the file
gdown.download(url, destination, quiet=False)

print(f"File downloaded as {destination}")

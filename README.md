# Multi Path REST API Project

This project demonstrates a simple REST API with multiple endpoints, including paths for `/hello/*` and `/users/*`, using Java, Servlets, and Jetty. A new feature has been added to restrict access to certain API paths, allowing them to be accessible only with a valid API key.

---

## Project Setup

This project uses the **Jetty Maven Plugin** for running the application and **Maven** for building the project. It includes two servlets:

1. **HelloWorldServlet**: Handles requests at `/hello/*` (Public access).
2. **UserServlet**: Handles requests at `/users/*` (Restricted access, requires an API key).

---

## Requirements

- Java 8 or higher
- Maven 3.x or higher

### Install Java 8 or Higher

1. Update the package list and install OpenJDK 8 (or later):

   ```bash
   sudo apt update
   sudo apt install openjdk-8-jdk
   ```

2. Verify the installation by checking the Java version:

   ```bash
   java -version
   ```

### Install Maven 3.x or Higher

1. Update the package list and install Maven:

   ```bash
   sudo apt update
   sudo apt install maven
   ```

2. Verify the installation by checking the Maven version:

   ```bash
   mvn -version
   ```

---

## Project Structure

- **src/main/java**: Contains the Java source files (servlets).
- **src/main/webapp**: Contains the web resources (e.g., `web.xml` configuration).
- **pom.xml**: Maven build file.
- **.env**: Environment file for storing sensitive information (e.g., API key).

---

## New Feature: Restricted Access API

### Overview

The `/users/*` endpoint is now restricted and requires an API key for access. This ensures that only authorized applications can access the private paths.

- API key is defined in the `.env` file as:

  ```
  API_KEY=d18d87ba-7baa-4954-bfe0-a89eb2c32b01
  ```

- Requests to `/users/*` must include the `API-Key` header with the correct value.

### Testing the Restricted Path

#### Using `curl`:

```bash
curl -H "API-Key: d18d87ba-7baa-4954-bfe0-a89eb2c32b01" http://localhost:1234/users/
```

Expected response for valid API key:

```
This is a private path, and you have access!
```

Expected response for invalid or missing API key:

```
Unauthorized. Invalid or missing API Key.
```

---

## How to Build and Run the Project

### Step 1: Build the Project

To build the project, run:

```bash
mvn clean package
```

### Step 2: Start the Server

To start the Jetty server, execute:

```bash
mvn jetty:run
```

Alternatively, run it in the background:

```bash
nohup mvn jetty:run &> output.log &
```

---

## Testing the REST API

### Public Path (`/hello/*`)

Accessible by anyone:

```bash
curl http://localhost:1234/hello/world
```

Expected output:

```
Hello, World!
```

### Restricted Path (`/users/*`)

Requires an API key:

```bash
curl -H "API-Key: d18d87ba-7baa-4954-bfe0-a89eb2c32b01" http://localhost:1234/users/
```

---

## Troubleshooting

1. **503 Service Unavailable**: Ensure the server is running.
2. **Unauthorized Errors**: Verify the API key in the request header matches the key in `.env`.
3. **Port Conflicts**: Update the `pom.xml` file to change the port if needed.

---

## Contribution and Branch Management

### Branch for Restricted Feature

All changes related to the restricted access feature are implemented in the `feature-restricted-access` branch. To switch to this branch:

```bash
git checkout feature-restricted-access
```

To create a new branch:

```bash
git checkout -b <branch_name>
```

### Feature Ideas or Issues

Feel free to raise an issue or propose a feature to improve the project further.
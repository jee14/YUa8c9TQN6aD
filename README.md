## Overview
![demo](https://github.com/user-attachments/assets/b08bc867-02ad-4e62-9280-afffe0b7ba2b)

This project features a Retrieval-Augmented Generation application developed using Spring AI.
By leveraging advanced NLP techniques, this application enhances its responses using a combination of chat history and few-shot learning templates.

## Features
- Retrieval Augmented Generation: Combines retrieval-based techniques with generative models to produce accurate and context-aware answers.
- Chat History: Maintains a history of user interactions to provide contextually relevant answers. Based on the user's previous question and the provided answer, suggest two or three follow-up questions that the user might find helpful.
- Few-Shot Learning Templates: Enhances the model's responses by using predefined templates for better accuracy and consistency.

## Requirements
- Server: Docker
- Client: Python3

## Usage
1. Clone the repository:
```sh
git clone https://github.com/jee14/YUa8c9TQN6aD.git
cd YUa8c9TQN6aD
```
2. Copy the provided .env file:
```sh
cp {.env path} .
```
3. Run Docker Compose:
```sh
docker compose up -d
```
4. Embed the Smart Store FAQ data:
```sh
curl -X PUT http://localhost:8080/embeddings \
      -H "Content-Type: application/json"
```
5. Run the client:
```sh
cd frontend
chmod +x ./run.sh
./run.sh
```

## References
- [Spring AI](https://docs.spring.io/spring-ai/reference/)
- [Few-Shot learning benchmark](https://arxiv.org/pdf/2005.14165)

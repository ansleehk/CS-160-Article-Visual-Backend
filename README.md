# Running the application

## OpenAI Key
You must have an OpenAI key to run this application.
```zsh
export OPENAI_KEY=openApiKey
```

## Docker
```zsh
git clone https://github.com/ansleehk/CS-160-Article-Visual-Backend.git
cd CS-160-Article-Visual-Backend
docker build . -t cs-160/article-visual
docker run -p 8080:8080 cs-160/article-visual
```

# Running the application

## Docker
> [!IMPORTANT]  
> You must have an OpenAI key to build the container

```zsh
git clone https://github.com/ansleehk/CS-160-Article-Visual-Backend.git
cd CS-160-Article-Visual-Backend
docker build . -t cs-160/article-visual --build-arg OPENAI_KEY=key
docker run -p 8080:8080 cs-160/article-visual
```

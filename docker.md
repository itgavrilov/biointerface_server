docker build -t db -f docker/db.Dockerfile .
docker build -t biointerface_server -f docker/app.Dockerfile .

docker exec -it 83ea9a37921f bash

docker run -v ./logs:/app/logs -p 8080:8080 -it cdb74df9665d bash
docker run -v ./logs:/app/logs -p 8080:8080 -t biointerface_server bash


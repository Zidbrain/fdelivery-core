Backend for f-delivery - a groceries delivery services.

## Adding static content
To serve frontend application files add static content to src/main/resources/app directory.
These files will be served from the root url of the server. 

## Running a server

First build docker image:
```bash
$ ./gradlew jibDockerBuild
```
Make sure your local instance of docker is running.
This will create an image 'fdelivery'

To start a server run:
```bash
$ docker compose up -d
```
This will start two docker containers:
- web - for the server
- db - for the database

The database will initialize automatically

The server will start at [http://localhost:8080](http://localhost:8080)

If you're using an IntelliJ based IDE you can use 'Compose Deployment' run configuration to start the server
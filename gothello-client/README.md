# Gothello-client

### Getting Started

1. Install node package manager (npm)

```
# On ubuntu
sudo apt install npm 
```

2. Install packages

```
npm install
```

3. Run the gothello-client

```
npm start
```

http://localhost:3000/

We use the react development server so changes are re-built on save.

#### Arguments

`PORT` - Which port should it run on

`REACT_APP_GOTHELLO_API` - The Gothello HTTP API endpoint

`REACT_APP_GOTHELLO_WS_API` - The Gothello Web-socket API endpoint

```
PORT=8080 \
	REACT_APP_GOTHELLO_WS_API="ws://localhost:8080/api/v0" \
	REACT_APP_GOTHELLO_API="http://localhost:80/api/v0" \
	npm start
```

##### Alternatively edit `.env`

`.env` contains default variables

```
REACT_APP_GOTHELLO_API = "http://localhost:8080/api/v0"
REACT_APP_GOTHELLO_WS_API = "ws://localhost:8080/api/v0"
```

##### Author

[Anton Lee](https://github.com/tachyonicClock)


var server_url = process.env.REACT_APP_GOTHELLO_API
var server_url_ws = process.env.REACT_APP_GOTHELLO_WS_API

// If we are in the production environment then our endpoint is the same as
// the servers
if (server_url === "PROD") {
  server_url = window.location.origin + "/api/v0"
}
if (server_url_ws === "PROD"){
  if (window.location.protocol === "https:") {
    server_url_ws = "wss://" + window.location.host + "/api/v0"
  }else {
    server_url_ws = "ws://" + window.location.host + "/api/v0"
  }
}
export const SERVER_URL =  server_url
export const SERVER_URL_WS = server_url_ws
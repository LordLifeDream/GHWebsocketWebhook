# GHWW
*It's better than its name*

## Purpose
GitHub offers a WebHook for events like push. However, that WebHook only does HTTP requests to a given URL.
Using that on something like a locally hosted pi is basically impossible with dynamic IPs.
GHWW is intended to be hosted on a static webserver and then allow for clients like a pi on a dynamic IP to connect and receive events via WebSockets.

## File Structure
The program will create two files in the same directory it's being run from:
- `auth.json`
- `secrets.json`
<br>
both are initialized as empty JSONObjects.

### auth.json structure
This JSONObject follows this structure:
```
{
  "<token>":{
    "allowed":[
      "user/repo1",
      "user/repo2",
      "*",
    ]
  }
}
```
The token is used by clients to authenticate when first connecting to the WebSocket server and can be any String, although long sequences are recommended.
The "allowed" key contains a JSONArray with allowed repo names. "*" gives access to every repository and basically skips the name check, although it does still need to be in the array.

### secrets.json structure
This JSONObject follows this structure:
```
{
  "user/repo1":"<github-secret>"
}
```
This file is intended to map GitHub WebHook secrets to their repository name for validation.
**If a repository does not have a secret assigned to it, requests will be trusted and not validated.**

## Web Endpoints
The program will start on port `7715`.
- WebHooks should be pointed to `/githubEndpoint`.
- The WebSocket server is running on `/listen`.
- A simple helloworld enpoint is on `/hello`.

## WebSocket communication
The WebSocket communicates using JSON. Every message from and to the server should have a `t` key that contains the **type** of the requests.
These types are in *screaming snake case*, so all uppercase and divided by underscores.<br>
When the websocket opens, GHWW will send a payload of the type `GREETINGS` with no further keys.<br>
The connected client is expected to respond using another `GREETINGS`-payload with a `token`-key containing a token from the `auth.json` file.<br>
Upon receiving and validating the client's greetings, GHWW will send an empty payload of type `GREETINGS_ACK` to acknowledge the greetings. After receiving this payload, the client is registered and can start subscribing.<br>
To subscribe, the client should send a payload of type `SUBSCRIBE` containing a `repo`-key containig the repo full name, so `user/repo1` for example. This is NOT the repository's URL and should not contain `.git` at the end.
After validating the client's token's access to the requested repository, GHWW will respond with an empty `SUBSCRIBE_ACK` payload.

Any bad payloads (greetings twice, or subscribe before greetings) will get a `USER_ERROR` response from GHWW which also gives a small explaination in the `why`-field.

## Info
This project was thrown together in one night and is therefore of rather mediocre quality and missing a lot of features. The following are planned:
- better internal error handeling
- User/Repository whitelist for GitHub's inbound requests
- config editing (optionally via a website too)
- better spam protection
- better WebSocket timeout/ping handeling (currently no timeout)
- better documentation & examples


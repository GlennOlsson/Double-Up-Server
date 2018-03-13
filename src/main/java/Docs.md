## Requests
### POST /newUser
#### Create a new user
##### NotificationToken is optional
```json
{
  "Username" : "YOUR_NICKNAME",
  "NotificationToken": "NOTIFICATION_TOKEN"
}
```

### POST /newGame
##### RequestedOpponent is optional, will be assigned a random person otherwise
```json
{
  "Token": "TOKEN",
  "RequestedOpponent": "USERNAME",
  "StartAmount": 200
}
```

### POST /changeUsername 
```json
{
   "Token": "TOKEN",
   "Username": "USERNAME"
}
```

### POST /playGame 
```json
{
   "GameID": "ID",
   "Token": "TOKEN",
   "DidDouble": true,
   "CurrentAmount": 20
}
```

### POST /newStartup 
##### NotificationToken and Version are optional, but recommended
```json
{
   "Token": "TOKEN",
   "NotificationToken": "TOKEN",
   "Version": "APP_VERSION"
}
```

## Responses (at code = 2XX)
### POST /newUsername
```json
{
   "UserToken" : "TOKEN"
}
```

### POST /newGame
```json
{
   "OpponentUsername" : "Username",
   "GameID": "GAME_ID"
}
```

### POST /newStartup 
#### Token may or may not be provided. If Token is provided, make sure to save the token. This is a way to force change user token
```json
{
   "Token": "USER_TOKEN"
}
```

### GET /gameInfo/:gameID
```json
{
  "Users": [
    "USERNAME1",
    "USERNAME2"
  ],
  "Turn": "USERNAME1",
  "IsOver": true,
  "CurrentAmount": 20
}
```

### GET /games/:userToken
```json
[
  {
  "GameID": "GAME1",
  "OpponentUsername": "OPPONENT1",
  "IsOver": false,
  "CurrentAmount": 20,
  "Turn": true
  },
  {
    "GameID": "GAME2",
    "OpponentUsername": "OPPONENT2",
    "IsOver": true,
    "CurrentAmount": 130,
    "Turn": false
  }
]
```

### GET /userInfo/:token
```json
{
  "Username": "USERNAME",
  "BankAmount": 100
}
```

# Status codes

## /newUser
### 200
New user generation was successful

### 401
User with that username already exists

### 500
General exception on server

### 501
IOException on server

## /newGame
### 200
New game creation was successful

### 401
No user with that username exists

### 402
Could not find a random user

### 500
General exception on server

### 501
IOException on server

## /changeUsername
### 200
Change was successful

### 401
A user with that username already exists

### 500
General exception on server

### 501
IOException on server

## /playGame
### 200
Play was successful

### 401
No game with that ID

### 402
User with token is not in game

### 403
Not user of tokens turn

### 405
Did not double, cannot accept

### 406
Server.Game is already over

### 407
User does not have enough founds

### 500
General exception on server

### 501
IOException on server

## /newStartup
### 200
Notification token has been registered

### 201
Successful start, but no notification token was passed along

### 500
General exception on server

### 501
IOException on server

## /gameInfo/:id
### 200
Server.Game info has successfully been returned

### 401
No game with that id

### 500
General exception on server

## /games/:token
### 200
Games info has successfully been returned

### 401
No user with that token

### 500
General exception on server

## /userInfo/:token
### 200
User info was successfully returned

### 401
No user with that token

### 500
General exception on server
# Atlantis

Atlantis is a POC aiming at demonstrating the possible use of Spring Authorization as a replacement of Connect Auth.

## Authorization Server

Spring Authorization Server. It requires a local mysql database *atlantis*.

Port: 8080

Two clients are available. (uncomment code in schema.sql to add these clients in the database)

* gateway
  * code authorization with PKCS flow used by the gateway
  * secret is *gatewaysecret*
* bar
  * client credential flow. user by Bar Server.
  * secret is *barsecret*

Two users are available. (uncomment code in schema.sql to add these users in the database)

* user
  * secret is *password*
* admin
  * secret is *password*

## Resource Servers

Two Resource Servers are available.
These two servers require authentication and use the Atlantis Authorization Server as provider. 

### Foo Server

Foo Server handles Foo resource

Port: 8081

### Bar Server

Bar Server handles Bar resource.
Bar server is a client of Foo Server

Port: 8082

## Gateway

Spring Cloud Gateway. Used for BFF pattern.

BFF aims at hiding the OAuth2 tokens from the browser. In this pattern, rich applications (Angular, React, Vue, etc.) are secured with sessions on a middle-ware, the BFF, which is the only OAuth2 client and replaces session cookie with an access-token before forwarding a request to the resource-server.

When a browser application first tries to access REST resources:

* the BFF redirects the user to the authorization-server
* the user authenticates
* the authorization-server redirects back to the BFF with an authorization code
* the BFF fetches OAuth2 tokens from the authorization-server and stores it in session
* the BFF forwards the initial request to the resource-server with the access-token as Authorization header

https://github.com/spring-projects/spring-authorization-server/issues/297#issue-896744390

Port: 8083

## Front End

Front End application consumes Foo and Bar APIs through the gateway.

Port: 4200

Front-end application itself must be accessed through the gateway.

## Build

### Back-ends

```bash
mvn clean install
```

### Front-end

```bash
npm install
```

## Run

### Front-end

```bash
ng serve front-end
```

### Back-ends

Run all services starting with the authorization service

```bash
mvn spring-boot:run -f authorization-server
mvn spring-boot:run -f bar-resource-server
mvn spring-boot:run -f foo-resource-server
mvn spring-boot:run -f gateway
```

## Use

Access UI through the gateway. http://127.0.0.1:8083

Connect as *user* or *admin*

## Things to test

* FQDN filter
* Locked Account after X attempts
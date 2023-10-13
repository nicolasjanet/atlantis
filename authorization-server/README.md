# Authorization Service

http://localhost:8080/.well-known/openid-configuration

## Public clients

Public Client uses Authorization Code with PKCE flow (https://oauth.net/2/pkce/)

Helper: 
* https://oidcdebugger.com/
* https://developer.pingidentity.com/en/tools/pkce-code-generator.html

## Confidential clients

Confidential client uses Client Credential flow

```bash
curl -d "grant_type=client_credentials" -d "scope=openid" -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Basic $(echo -n "bar:barsecret" | base64 -w  0)" http://localhost:8080/oauth2/token | jq
```


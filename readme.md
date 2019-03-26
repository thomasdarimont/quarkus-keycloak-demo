# Quarkus Keycloak Demo

Simple example for a stateless JAX-RS service with JWT authentication. JWTs are issued by Keycloak and contain
claims with general user information as well as current user roles.

## Retrieve Tokens
```
KC_CLIENT_ID=quarkus-front
KC_ISSUER=http://localhost:8180/auth/realms/quarkus-quickstart

# Simple test user
KC_USERNAME=test
KC_PASSWORD=test

KC_RESPONSE=$( \
curl \
  -d "client_id=$KC_CLIENT_ID" \
  -d "username=$KC_USERNAME" \
  -d "password=$KC_PASSWORD" \
  -d "grant_type=password" \
  "$KC_ISSUER/protocol/openid-connect/token" \
)
echo $KC_RESPONSE | jq -C .

KC_ACCESS_TOKEN=$(echo $KC_RESPONSE | jq -r .access_token)

# Try to call endpoints - /data/user should work, /data/admin should fail
curl -v -H "Authorization: Bearer $KC_ACCESS_TOKEN" http://localhost:8082/data/user
curl -v -H "Authorization: Bearer $KC_ACCESS_TOKEN" http://localhost:8082/data/admin


# Simple admin user
KC_USERNAME=admin
KC_PASSWORD=test

KC_RESPONSE=$( \
curl \
  -d "client_id=$KC_CLIENT_ID" \
  -d "username=$KC_USERNAME" \
  -d "password=$KC_PASSWORD" \
  -d "grant_type=password" \
  "$KC_ISSUER/protocol/openid-connect/token" \
)
echo $KC_RESPONSE | jq -C .


KC_ACCESS_TOKEN=$(echo $KC_RESPONSE | jq -r .access_token)

# Try to call endpoints - /data/user and /data/admin should work both
curl -v -H "Authorization: Bearer $KC_ACCESS_TOKEN" http://localhost:8082/data/admin
curl -v -H "Authorization: Bearer $KC_ACCESS_TOKEN" http://localhost:8082/data/user
```
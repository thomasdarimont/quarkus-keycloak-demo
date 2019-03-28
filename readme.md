# Quarkus Keycloak Demo

Simple example for a stateless JAX-RS service with JWT authentication. JWTs are issued by Keycloak and contain
claims with general user information as well as current user roles.

# Build
```
mvn clean package -DskipTests
```

# Run

## Run Keycloak
```
docker run \
  -d \
  --name keycloak \
  -e KEYCLOAK_USER=admin \
  -e KEYCLOAK_PASSWORD=admin \
  -p 8180:8180 \
  -v `pwd`/quarkus-quickstart-realm.json:/config/quarkus-quickstart-realm.json \
  -it jboss/keycloak:5.0.0 \
  -b 0.0.0.0 \
  -Djboss.http.port=8180 \
  -Dkeycloak.migration.action=import \
  -Dkeycloak.migration.provider=singleFile \
  -Dkeycloak.migration.file=/config/quarkus-quickstart-realm.json \
  -Dkeycloak.migration.strategy=OVERWRITE_EXISTING
```

## Run with Remote Debug
```
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8787 \
     -jar target/quarkus-keycloak-demo-1.0.0-SNAPSHOT-runner.jar \
     ./target/classes \
     ./target/wiring-devmode \
     ./target/transformer-cache
```

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

# Demo

> Run Keycloak and the Quarkus App

> Make a request as user `test` and call the `http://localhost:8082/data/user` endpoint, (see Retrieve Tokens)
This will output
```
data for user "test"
```
> Calling the `http://localhost:8082/data/admin` endpoint correctly fails with:
```
Access forbidden: role not allowed%
```

> Make request as user `admin` and call the `http://localhost:8082/data/user` endpoint again
This will output:
```
data for user "admin"
```

> calling `http://localhost:8082/data/admin` endpoint succeeds now
```
data for admin "admin"
```

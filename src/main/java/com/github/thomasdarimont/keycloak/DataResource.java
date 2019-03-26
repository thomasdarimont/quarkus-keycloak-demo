package com.github.thomasdarimont.keycloak;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonString;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/data")
public class DataResource {

    private static final JsonString ANOYNMOUS = Json.createValue("anonymous");

    @Inject
    @Claim("raw_token")
    String rawToken;

    @Inject
    @Claim(standard = Claims.sub)
    Optional<JsonString> subject;

    @Inject
    @Claim(standard = Claims.preferred_username)
    Optional<JsonString> currentUsername;

    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"user"})
    public String userData() {
        return "data for user " + currentUsername.orElse(ANOYNMOUS);
    }

    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"admin"})
    public String adminData() {
        return "data for admin " + currentUsername.orElse(ANOYNMOUS);
    }
}
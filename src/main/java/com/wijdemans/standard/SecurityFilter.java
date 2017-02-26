package com.wijdemans.standard;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.regex.Pattern;


// http://howtodoinjava.com/jersey/jersey-rest-security/
// http://www.sixturtle.com/blog/2015/02/01/secure-api-with-jwt.html

@Priority(Priorities.AUTHENTICATION)
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    public static final String ROLE_USER = "USER";

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    private Pattern tokenPattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    @Context
    private ResourceInfo resourceInfo;
    private Key publicKey;
    private boolean disabled;

    @PostConstruct
    public void init() throws InvalidKeySpecException, NoSuchAlgorithmException {
        disabled = Boolean.parseBoolean(Config.get("security.disabled"));
        logger.info("Security is {}", !disabled);
        logger.debug("Registering public key ...");
        String jwtSignedKey = Config.get("security.jwt.signedKey");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(jwtSignedKey.getBytes(StandardCharsets.UTF_8)));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(pubKeySpec);
        logger.debug("... public key is configured.");

    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (disabled) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();
        //Access allowed for all
        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }
        if (method.isAnnotationPresent(DenyAll.class)) {
            throw new NotAuthorizedException(
                    "Unauthorized: Unable to extract claims from JWT",
                    Response.status(Response.Status.UNAUTHORIZED));
        }

        Optional<String> s = parseBearerToken(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
        if (!s.isPresent()) {
            throw new NotAuthorizedException(
                    "Unauthorized: Unable to extract claims from JWT",
                    Response.status(Response.Status.UNAUTHORIZED));
        }

        //Verify user access
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

            JwtClaims claims = parseClaims(s.get());

            // then we expect it is ok :-)
        }
    }

    private JwtClaims parseClaims(String token) {

        try {
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime() // the JWT must have an expiration time
                    .setMaxFutureValidityInMinutes(300) // but the  expiration time can't be too crazy
                    .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                    .setRequireSubject() // the JWT must have a subject claim
//                    .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
                    .setVerificationKey(publicKey) // verify the signature with the public key
                    .build(); // create the JwtConsumer instance

            JwtClaims c = jwtConsumer.processToClaims(token);

            return c;
            //OK, we can trust this JWT
        } catch (InvalidJwtException e) {
            logger.error("Error extracting claim", e);
            throw new NotAuthorizedException(
                    "Unauthorized: Unable to extract claims from JWT",
                    Response.status(Response.Status.UNAUTHORIZED));
        }

    }

    private Optional<String> parseBearerToken(final String bearerToken) {
        if (bearerToken == null) {
            return Optional.empty();
        }
        String[] parts = bearerToken.split(" ");
        if (parts.length == 2) {
            String scheme = parts[0];
            String credentials = parts[1];
            if (tokenPattern.matcher(scheme).matches()) {
                return Optional.of(credentials);
            }
        }
        return Optional.empty();
    }

}

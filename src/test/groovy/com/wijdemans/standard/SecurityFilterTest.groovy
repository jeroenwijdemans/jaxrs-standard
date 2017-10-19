package com.wijdemans.standard

import org.jose4j.jwk.PublicJsonWebKey
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import spock.lang.Specification

import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec

class SecurityFilterTest extends Specification {

    SecurityFilter securityFilter

    void setup() {
        securityFilter = new SecurityFilter()
    }

    def "when i send in a validate request i expect to find the username"() {
        given:

        PublicJsonWebKey keypair = RsaJwkGenerator.generateJwk(2048)
        keypair.setKeyId("k1")

        keypair.getRsaPublicKey().publicExponent
        KeyFactory f = KeyFactory.getInstance("RSA")
        BigInteger exp = keypair.getRsaPublicKey().publicExponent

        RSAPublicKeySpec spec = new RSAPublicKeySpec(keypair.getRsaPublicKey().modulus, exp)
        PublicKey pub = f.generatePublic(spec)
        byte[] data = pub.getEncoded()

        String base64encoded = new String(Base64.encoder.encode(data))

        Config.props.put("security.disabled", false)
        Config.props.put("security.jwt.signedKey", base64encoded)
        securityFilter.init()

        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims()
        claims.setIssuer("Issuer")
        claims.setExpirationTimeMinutesInTheFuture(10)
        claims.setGeneratedJwtId()
        claims.setIssuedAtToNow()
        claims.setNotBeforeMinutesInThePast(2)
        claims.setSubject("subject")
        claims.setClaim("email", "jeroenwijdemans@gmail.com")
        List<String> groups = Arrays.asList("group-one", "other-group", "group-three")
        claims.setStringListClaim("groups", groups)

        JsonWebSignature jws = new JsonWebSignature()
        jws.setPayload(claims.toJson())
        jws.setKey(keypair.getPrivateKey())
        jws.setKeyIdHeaderValue(keypair.getKeyId())
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256)

        String jwt = jws.getCompactSerialization()

        when:
        def returnedClaims = securityFilter.parseClaims(jwt)

        then:
        returnedClaims.flattenClaims() == claims.flattenClaims()
    }

    def "when i pass in the authorization header i expect the bearer"() {
        expect:
        "1234" == securityFilter.parseBearerToken("Bearer 1234").get()
    }

    def "when i pass in the an unknown authorization header i expect an empty result"() {
        expect:
        !securityFilter.parseBearerToken("Basic 1234").isPresent()
    }

}

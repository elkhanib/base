package com.bosch.inst.base.security.auth;

import java.time.Instant;
import java.util.Map;

/**
 * Provides API based access to the claims of a JSON Web Token which was issued by IM.
 *
 * @author Michael Ernst
 * @since 1.0
 */
public interface IJsonWebToken {
    /**
     * Returns the issuer of the token.
     *
     * @return the issuer of the token
     */
    String getIssuer();

    /**
     * Returns the audience of the token.
     *
     * @return the audience of the token
     */
    String getAudience();

    /**
     * Returns the expiration date of the token.
     *
     * @return the expiration date of the token
     */
    Instant getExpirationDate();

    /**
     * Returns the issuing date of the token.
     *
     * @return the issuing date of the token
     */
    Instant getIssuingDate();

    /**
     * Returns the ID of the public key which was used to sign the token.
     *
     * @return the ID of the public key which was used to sign the token
     */
    String getPublicKeyId();

    /**
     * Returns the signature algorithm which was used to sign the token.
     *
     * @return the signature algorithm which was used to sign the token
     */
    String getSignatureAlgorithm();

    /**
     * Returns the ID of the user for whom the token was created.
     *
     * @return the ID of the user for whom the token was created
     */
    String getUserId();

    /**
     * Returns the ID of the the token.
     *
     * @return the ID of token
     */
    String getJwtId();

    /**
     * Returns the plain JWT which was used to create this instance.
     *
     * @return the plain JWT
     */
    String getJwt();

    /**
     * Provides access to all claims of the token payload.
     *
     * @return immutable map with all claims of the token payload
     */
    Map<String, Object> getClaims();
}


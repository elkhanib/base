package com.bosch.inst.base.security.local.auth;


/**
 * Represents the {@code orig} claim in the Authorization Token, which can be queried via the Scope
 * {@link com.bosch.im.api2.Scope#orig}.
 *
 * @author Michael Ernst
 * @since 1.0
 */
public interface IAuthenticationInformation {
    /**
     * Returns the value of the {@code sub} claim of the ID token which was used for authentication.
     * <p>
     * This might be the ID of the user in IM, if authentication takes place via IM, or the identifier at the external
     * identity provider.
     * </p>
     *
     * @return the ID of the user which was used for authentication
     */
    String getSubject();

    /**
     * Returns the value of the {@code iss} claim of the ID token which was used for authentication.
     *
     * @return the issuer of the authentication token
     */
    String getIssuer();

    /**
     * Returns the value of the {@code aud} claim of the ID token which was used for authentication.
     * <p>
     * In case the authentication token contains more than one audience value this method returns the effective audience
     * as defined via the authorized party ({@code azp}) claim.
     * </p>
     *
     * @return the audience of the authentication token
     */
    String getAudience();
}

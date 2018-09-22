package com.bosch.inst.base.security.auth;

/**
 * Defines all possible claims which are defined by Service API 2 and which are not part of the specification which can
 * be returned when authorizing at IM.
 *
 * @author Peter Bugiel
 * @author Thomas Koch
 * @since 1.0
 */
@SuppressWarnings("all")
public enum AuthorizationClaim {
    /**
     * The ID of the tenant in whose context the groups, roles and permissions are valid.
     */
    tid,

    /**
     * Group IDs.
     */
    gid,

    /**
     * Tenant role IDs.
     */
    trid,

    /**
     * Map of instance role names per application name.
     */
    rn,

    /**
     * Map of permission names per application name.
     */
    pn,

    /**
     * Flag which indicates that the Authorization Token may be used to request "Identity Contexts" of Service API 1
     */
    ctx,

    /**
     * Original issuer and subject of the authentication token.
     */
    orig;

    /**
     * Returns the claim for the given name.
     *
     * @param name the name of the enum
     * @return the corresponding enum
     * @throws IllegalArgumentException if there is no matching enum for the specified name
     */
    public static AuthorizationClaim getByName(final String name) {
        return valueOf(name.toLowerCase());
    }
}

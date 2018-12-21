package com.bosch.inst.base.security.auth;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Provides API based access to the claims of an Authorization Token.
 *
 * @author Michael Ernst
 * @since 1.0
 */
public interface IAuthorizationToken extends IJsonWebToken {
    /**
     * Returns the ID of the tenant for which the user is authenticated.
     *
     * @return the ID of the tenant for which the user is authenticated
     * @see AuthorizationClaim#tid
     */
    String getTenantId();

    /**
     * Returns {@code true} if the token contains the given permission of the given application.
     *
     * @param applicationName the name of the application
     * @param permissionName  the name of the permission
     * @return {@code true} if the token contains the given permission of the given application
     */
    boolean hasPermission(String applicationName, String permissionName);

    /**
     * Returns {@code true} if the token contains the given application role of the given application.
     *
     * @param applicationName     the name of the application
     * @param applicationRoleName the name of the application role
     * @return {@code true} if the token contains the given application role of the given application
     */
    boolean hasApplicationRole(String applicationName, String applicationRoleName);

    /**
     * Returns {@code true} if the token contains the given tenant role.
     *
     * @param roleId the ID of the tenant role
     * @return {@code true} if the token contains the given tenant role
     */
    boolean hasTenantRole(String roleId);

    /**
     * Returns {@code true} if the token contains the given group.
     *
     * @param groupId the ID of the group
     * @return {@code true} if the token contains the given group
     */
    boolean hasGroup(String groupId);

    /**
     * Returns a set which contains all claims which are contained in this token which might be relevant for
     * auth checks.
     *
     * @return a set which contains all claims which are contained in this token
     */
    Set<AuthorizationClaim> getContainedClaims();

    /**
     * Returns a map with all application names as keys and the corresponding permission names as value.
     *
     * @return a map with all application names as keys and the corresponding permission names as value
     * @see AuthorizationClaim#pn
     */
    Map<String, Set<String>> getPermissionNames();

    /**
     * Returns a map with all application names as keys and the corresponding application role name as value.
     *
     * @return a map with all application names as keys and the corresponding application role name as value
     * @see AuthorizationClaim#rn
     */
    Map<String, Set<String>> getApplicationRoleNames();

    /**
     * Returns a set which contains all tenant role IDs.
     *
     * @return a set which contains all tenant role IDs
     * @see AuthorizationClaim#trid
     */
    Set<String> getTenantRoleIds();

    /**
     * Returns a set which contains all group IDs.
     *
     * @return a set which contains all group IDs
     * @see AuthorizationClaim#gid
     */
    Set<String> getGroupIds();

    /**
     * Determines whether the token allows to request a Service API 1 Identity Context.
     *
     * @return <code>true</code> if requesting an Identity Context is allowed
     * @see AuthorizationClaim#ctx
     */
    boolean isRequestIdentityContextAllowed();

    /**
     * Returns information which was provided in the ID Token used for creating this Authorization Token. The
     * {@link Optional} is empty if the claim {@link AuthorizationClaim#orig} is not provided by the token.
     *
     * @return information which was provided in the ID Token used for creating this Authorization Token
     * @see AuthorizationClaim#orig
     */
    Optional<IAuthenticationInformation> getAuthenticationInformation();
}

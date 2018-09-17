package com.bosch.inst.base.security.configuration;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Thrown if the token is expired.
 *
 * @author Michael Ernst
 * @since 1.0
 */
public class ExpiredTokenException extends RuntimeException {
    private static final long serialVersionUID = 3954597561385755025L;

    // don't need millis since JWT date fields are only second granularity:
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(ISO_8601_FORMAT).withZone(
            ZoneId.systemDefault());

    private Instant expirationDate;

    /**
     * Constructor.
     *
     * @param expirationDate the expiration date @
     */
    public ExpiredTokenException(final Instant expirationDate) {
        super("The JWT expired at " + FORMATTER.format(expirationDate) + ". Current time: "
                + FORMATTER.format(Instant.now()));
        this.expirationDate = expirationDate;
    }

    /**
     * Constructor.
     *
     * @param expirationDate the expiration date @
     */
    private ExpiredTokenException(final Instant expirationDate, final Throwable cause) {
        super("The JWT expired at " + FORMATTER.format(expirationDate) + ". Current time: "
                + FORMATTER.format(Instant.now()), cause);
        this.expirationDate = expirationDate;
    }
}


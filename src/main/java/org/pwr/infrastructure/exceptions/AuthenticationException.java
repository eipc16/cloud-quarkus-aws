package org.pwr.infrastructure.exceptions;

import org.pwr.infrastructure.identity.UserGroups;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;

public class AuthenticationException extends WebApplicationException {

    public AuthenticationException() {
        super("Authentication is required to access this resource!", Response.Status.UNAUTHORIZED);
    }

    public AuthenticationException(UserGroups requiredGroup) {
        super(MessageFormat.format(
                "Access Denied. Group: {0} is required to access this resource", requiredGroup.getGroupName()),
                Response.Status.UNAUTHORIZED);
    }
}

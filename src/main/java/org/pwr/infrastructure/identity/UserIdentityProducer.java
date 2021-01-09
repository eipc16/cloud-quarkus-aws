package org.pwr.infrastructure.identity;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.Claim;
import org.pwr.infrastructure.exceptions.AuthenticationException;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.Set;

@Dependent
public class UserIdentityProducer {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    @Claim("cognito:groups")
    Set<String> cognitoGroups;

    @Inject
    @Claim("username")
    String userName;

    @Produces
    @Default
    public UserIdentity getCurrentUser() {
        if (securityIdentity.isAnonymous()) {
            throw new AuthenticationException();
        }
        return UserIdentity.builder(userName)
                .withCognitoGroups(cognitoGroups)
                .build();
    }
}

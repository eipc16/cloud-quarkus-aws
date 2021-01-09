package org.pwr.infrastructure.identity;

import org.pwr.infrastructure.exceptions.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class AuthorizationService {

    public void checkPermissions(UserIdentity userIdentity, UserGroups requiredGroup) {
        if(isAuthorized(userIdentity, requiredGroup)) {
            return;
        }
        throw new AuthenticationException(requiredGroup);
    }

    private boolean isAuthorized(UserIdentity userIdentity, UserGroups requiredGroup) {
        return userIdentity.getCognitoGroups().stream()
                .map(UserGroups::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(group -> isAuthorized(group, requiredGroup));
    }

    private boolean isAuthorized(UserGroups userGroup, UserGroups requiredGroup) {
        return userGroup.getAllPossibleGroups().contains(requiredGroup);
    }
}

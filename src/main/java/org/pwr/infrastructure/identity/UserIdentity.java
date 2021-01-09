package org.pwr.infrastructure.identity;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserIdentity {

    private String userName;
    private Set<String> cognitoGroups;

    private UserIdentity(Builder builder) {
        userName = builder.userName;
        cognitoGroups = builder.cognitoGroups;
    }

    public String getUserName() {
        return userName;
    }

    public Set<String> getCognitoGroups() {
        return cognitoGroups;
    }

    public static Builder builder(@NotNull String userName) {
        return new Builder(userName);
    }

    public static class Builder {
        private String userName;
        private Set<String> cognitoGroups = new HashSet<>();

        private Builder(String userName) {
            this.userName = userName;
        }

        public Builder withCognitoGroups(Collection<String> cognitoGroups) {
            this.cognitoGroups.clear();
            this.cognitoGroups.addAll(cognitoGroups);
            return this;
        }

        public UserIdentity build() {
            return new UserIdentity(this);
        }
    }
}

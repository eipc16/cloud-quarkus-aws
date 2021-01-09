package org.pwr.infrastructure.identity;

import java.util.*;
import java.util.stream.Stream;

public enum UserGroups {
    MANAGER("manager"),
    CLIENT("client"),
    WORKER("worker", Collections.singleton(UserGroups.CLIENT));

    private String name;
    private Set<UserGroups> extendedGroups = new HashSet<>();

    UserGroups(String name) {
        this.name = name;
    }

    UserGroups(String name, Set<UserGroups> extendedGroups) {
        this(name);
        this.extendedGroups.addAll(extendedGroups);
    }

    public String getGroupName() {
        return name;
    }

    public Set<UserGroups> getAllPossibleGroups() {
        if(extendedGroups.isEmpty()) {
            return Collections.singleton(this);
        }
        HashSet<UserGroups> groups = new HashSet<>();
        groups.add(this);
        ArrayList<UserGroups> groupsToCheck = new ArrayList<>(extendedGroups);

        while(!groupsToCheck.isEmpty()) {
            UserGroups currentGroup = groupsToCheck.remove(0);
            if(!groups.contains(currentGroup)) {
                groups.add(currentGroup);
                groupsToCheck.addAll(currentGroup.extendedGroups);
            }
        }

        return Collections.unmodifiableSet(groups);
    }

    public static Optional<UserGroups> findByName(String name) {
        return Stream.of(values())
                .filter(group -> group.name.equals(name))
                .findAny();
    }
}

package org.pwr.domain.settings;

import org.pwr.infrastructure.identity.AuthorizationService;
import org.pwr.infrastructure.identity.UserGroups;
import org.pwr.infrastructure.identity.UserIdentity;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/settings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@RequestScoped
public class SettingsResource {

    @Inject
    SettingsMapper settingsMapper;

    @Inject
    SettingsService settingsService;

    @Inject
    UserIdentity userIdentity;

    @Inject
    AuthorizationService authorizationService;

    @GET
    public SettingsDTO getSettings() {
        authorizationService.checkPermissions(userIdentity, UserGroups.MANAGER);
        return settingsMapper.toDTO(settingsService.getSettings());
    }

    @PUT
    public SettingsDTO updateSettings(SettingsDTO settingsDTO) {
        authorizationService.checkPermissions(userIdentity, UserGroups.MANAGER);
        SettingsEntity settingsEntity = settingsMapper.toEntity(settingsDTO);
        return settingsMapper.toDTO(settingsService.save(settingsEntity));
    }
}

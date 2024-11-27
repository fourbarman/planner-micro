package ru.fourbarman.planner.micro.plannerusers.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import ru.fourbarman.planner.micro.plannerusers.dto.UserDTO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakUtil {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private static Keycloak keycloak; //единственный объект KC
    private static UsersResource usersResource;
    private static RealmResource realmResource;

    @PostConstruct
    public Keycloak keycloak() {
        if (keycloak == null) {

            keycloak = KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverUrl)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();

        }

        realmResource = keycloak.realm(realm);

        usersResource = realmResource.users();

        return keycloak;
    }

    //создание пользователя для KC
    public Response createKeycloakUser(UserDTO user) {

//        //доступ к API realm
//        RealmResource realmResource = keycloak().realm(realm);
//
//        //доступ к API для работы с пользователями
//        UsersResource usersResource = realmResource.users();

        // данные пароля - спец объект CredentialRepresentation
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        // данные пользователя
        // спец объект UserRepresentation
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setEmail(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));// коллекция из одного объекта
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(true);
        Response response = usersResource.create(kcUser);

        return response;
    }

    // данные пароля
    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }

    // добвление роли пользователю
    public void addRoles(String userId, List<String> roles) {

        // список доступных ролей в realm
        List<RoleRepresentation> kcRoles = new ArrayList<>();

        // преобразование текста в спец объекты RoleRepresentation, который использует keycloak
        for (String role : roles) {
            RoleRepresentation roleRep = realmResource.roles().get(role).toRepresentation();
            kcRoles.add(roleRep);
        }

        // получаем конкретного пользователя
        UserResource uniqueUserResource = usersResource.get(userId);

        // добавляем ему проли на уровен realm
        uniqueUserResource.roles().realmLevel().add(kcRoles);
    }
}

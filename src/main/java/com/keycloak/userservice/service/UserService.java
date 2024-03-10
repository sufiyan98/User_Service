package com.keycloak.userservice.service;

import com.keycloak.userservice.dto.UserRequestDto;
import com.keycloak.userservice.dto.UserResponseDto;
import com.keycloak.userservice.entity.User;
import com.keycloak.userservice.exception.RoleNotFoundException;
import com.keycloak.userservice.exception.UserNotFoundException;
import com.keycloak.userservice.exception.ValueAlreadyFoundException;
import com.keycloak.userservice.repo.UserRepo;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Value("${keycloak.realmName}")
    private String realmName;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    public UserService(ModelMapper modelMapper, Keycloak keycloak, @Value("${keycloak.realmName}") String realmName) {
        this.modelMapper = modelMapper;
        this.keycloak = keycloak;
        this.realmName = realmName;
    }

    // To get all roles from keycloak server
    public List<String> getRoles(String roleId) {
        return keycloak.realm(realmName).users().get(roleId).roles()
                .getAll().getRealmMappings().stream().map(RoleRepresentation::getName).toList();
    }

    // To fetch user by userId
    public ResponseEntity<UserResponseDto> getUserById(String userId) {
        try {
            UserRepresentation representation = keycloak.realm(realmName).users().get(userId).toRepresentation();
            UserResponseDto userResponseDto = mapToUserResponseDto(representation);
            return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new UserNotFoundException(e + "User not found with Id: " + userId);
        }
    }


    // To fetch user by Username
    public ResponseEntity<UserResponseDto> getUserByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realmName).users().search(username, true);
        if (users != null && !users.isEmpty()) {
            UserRepresentation representation = users.get(0);
            UserResponseDto userResponseDto = mapToUserResponseDto(representation);
            return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
        } else {
            throw new UserNotFoundException("User not found with username: " + username);
        }
    }

    // To fetch users by email
    public ResponseEntity<UserResponseDto> getUserByEmail(String email) {
        List<UserRepresentation> users = keycloak.realm(realmName).users().search(null, null, null, email, 0, 1);
        if (users != null && !users.isEmpty()) {
            UserRepresentation representation = users.get(0);
            UserResponseDto userResponse = mapToUserResponseDto(representation);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            throw new UserNotFoundException("User not found with email: " + email);
        }
    }

    // api to add users to keycloak db
    public ResponseEntity<UserResponseDto> addUser(UserRequestDto userRequest) {
        try {
            String username = userRequest.getUsername();
            logger.info("Received username: " + username);
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }

            if (isEmailExists(userRequest.getEmail())) {
                throw new ValueAlreadyFoundException("Email " + userRequest.getEmail() + " already exists!");
            }

            if (isUsernameExists(username)) {
                throw new ValueAlreadyFoundException("Username " + username + " already exists!");
            }

            RealmResource realmResource = keycloak.realm(realmName);
            RoleRepresentation roleRepresentation = realmResource.roles().get(userRequest.getRole()).toRepresentation();
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setUsername(username);
            userRepresentation.setFirstName(userRequest.getFirstname());
            userRepresentation.setLastName(userRequest.getLastname());
            userRepresentation.setEmail(userRequest.getEmail());
            userRepresentation.singleAttribute("address", userRequest.getAddress());
            userRepresentation.setEnabled(true);
            UsersResource usersResource = keycloak.realm(realmName).users();

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userRequest.getPassword());

            userRepresentation.setCredentials(List.of(passwordCred));

            usersResource.create(userRepresentation);
            List<UserRepresentation> users = keycloak.realm(realmName).users().search(null, null, null,
                    userRequest.getEmail(), 0, 1);

            userRepo.save(modelMapper.map(userRequest, User.class));

            UserResponseDto userResponse = null;

            if (users != null && !users.isEmpty()) {
                UserRepresentation r = users.get(0);
                userResponse = modelMapper.map(userRequest, UserResponseDto.class);
                // userResponse.setUserId(r.getId());
                r.setRealmRoles(List.of(userRequest.getRole()));
                keycloak.realm(realmName).users().get(r.getId()).roles().realmLevel().add(List.of(roleRepresentation));
            }
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (ValueAlreadyFoundException e) {
            // Handle ValueAlreadyFoundException
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RoleNotFoundException e) {
            // Handle RoleNotFoundException
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace(); // Log the exception for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // get all Users by Role
    public List<UserResponseDto> getAllUsersByRole(String role) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            RoleRepresentation representation = realmResource.roles().get(role).toRepresentation();
            Set<UserRepresentation> roleUserMembers = realmResource.roles().get(representation.getName()).getRoleUserMembers();
            return roleUserMembers.stream().map(this::mapToUserResponseDto).toList();
        } catch (Exception e) {
            throw new RoleNotFoundException("Role with name " + role + " not found!");
        }
    }

    // to delete user by userId
    public void deleteUserById(String userId) {
        try {
            UsersResource usersResource = keycloak.realm(realmName).users();
            UserResource userResource = usersResource.get(userId);

            if (userResource.toRepresentation() == null) {
                throw new UserNotFoundException("User not found with ID: " + userId);
            }

            userResource.remove();
            logger.info("User deleted successfully with ID: " + userId);
        } catch (UserNotFoundException e) {
            // Re-throw UserNotFoundException to propagate it up
            throw e;
        } catch (Exception e) {
            logger.warning("Failed to delete user with ID: " + userId + ". Reason: " + e.getMessage());
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }


    public boolean isEmailExists(String email) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        List<UserRepresentation> usersWithEmail = usersResource.search(null, null, null, email, 0, 1);
        return !usersWithEmail.isEmpty();
    }

    public boolean isUsernameExists(String username) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        List<UserRepresentation> usersWithuser = usersResource.search(username, true);
        return !usersWithuser.isEmpty();
    }

    public UserResponseDto mapToUserResponseDto(UserRepresentation representation) {
        return UserResponseDto.builder().userId(representation.getId()).username(representation.getUsername())
                .email(representation.getEmail()).firstname(representation.getFirstName()).lastname(representation.getLastName())
                .roles(getRoles(representation.getId()))
                .address(representation.getAttributes().get("address").get(0)).build();
    }

}
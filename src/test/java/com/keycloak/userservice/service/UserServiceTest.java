package com.keycloak.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keycloak.userservice.dto.UserRequestDto;
import com.keycloak.userservice.dto.UserResponseDto;
import com.keycloak.userservice.exception.RoleNotFoundException;
import com.keycloak.userservice.exception.UserNotFoundException;
import com.keycloak.userservice.repo.UserRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserServiceTest {
  @MockBean
  private Keycloak keycloak;

  @MockBean
  private ModelMapper modelMapper;

  @MockBean
  private UserRepo userRepo;

  @Autowired
  private UserService userService;

  /**
   * Method under test: {@link UserService#getRoles(String)}
   */
  @Test
  void testGetRoles() {
    MappingsRepresentation mappingsRepresentation = new MappingsRepresentation();
    mappingsRepresentation.setClientMappings(new HashMap<>());
    mappingsRepresentation.setRealmMappings(new ArrayList<>());
    RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
    when(roleMappingResource.getAll()).thenReturn(mappingsRepresentation);
    UserResource userResource = mock(UserResource.class);
    when(userResource.roles()).thenReturn(roleMappingResource);
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.get(Mockito.<String>any())).thenReturn(userResource);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    List<String> actualRoles = userService.getRoles("42");
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(roleMappingResource).getAll();
    verify(userResource).roles();
    verify(usersResource).get(Mockito.<String>any());
    assertTrue(actualRoles.isEmpty());
  }

  /**
   * Method under test: {@link UserService#getRoles(String)}
   */
  @Test
  void testGetRoles2() {
    RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
    when(roleMappingResource.getAll()).thenThrow(new UserNotFoundException("foo"));
    UserResource userResource = mock(UserResource.class);
    when(userResource.roles()).thenReturn(roleMappingResource);
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.get(Mockito.<String>any())).thenReturn(userResource);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getRoles("42"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(roleMappingResource).getAll();
    verify(userResource).roles();
    verify(usersResource).get(Mockito.<String>any());
  }

  /**
   * Method under test: {@link UserService#getUserById(String)}
   */
  @Test
  void testGetUserById() {
    assertThrows(UserNotFoundException.class, () -> userService.getUserById("42"));
  }

  /**
   * Method under test: {@link UserService#getUserByUsername(String)}
   */
  @Test
  void testGetUserByUsername() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<Boolean>any())).thenReturn(new ArrayList<>());
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("janedoe"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<Boolean>any());
  }

  /**
   * Method under test: {@link UserService#getUserByUsername(String)}
   */
  @Test
  void testGetUserByUsername2() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<Boolean>any()))
            .thenThrow(new UserNotFoundException("foo"));
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("janedoe"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<Boolean>any());
  }

  /**
   * Method under test: {@link UserService#getUserByUsername(String)}
   */
  @Test
  void testGetUserByUsername3() {
    ArrayList<UserRepresentation> userRepresentationList = new ArrayList<>();
    userRepresentationList.add(new UserRepresentation());
    RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
    when(roleMappingResource.getAll()).thenThrow(new UserNotFoundException("foo"));
    UserResource userResource = mock(UserResource.class);
    when(userResource.roles()).thenReturn(roleMappingResource);
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.get(Mockito.<String>any())).thenReturn(userResource);
    when(usersResource.search(Mockito.<String>any(), Mockito.<Boolean>any())).thenReturn(userRepresentationList);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("janedoe"));
    verify(keycloak, atLeast(1)).realm(Mockito.<String>any());
    verify(realmResource, atLeast(1)).users();
    verify(roleMappingResource).getAll();
    verify(userResource).roles();
    verify(usersResource).get(Mockito.<String>any());
    verify(usersResource).search(Mockito.<String>any(), Mockito.<Boolean>any());
  }

  /**
   * Method under test: {@link UserService#getUserByEmail(String)}
   */
  @Test
  void testGetUserByEmail() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any())).thenReturn(new ArrayList<>());
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("jane.doe@example.org"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any());
  }

  /**
   * Method under test: {@link UserService#getUserByEmail(String)}
   */
  @Test
  void testGetUserByEmail2() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any()))
            .thenThrow(new UserNotFoundException("foo"));
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("jane.doe@example.org"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any());
  }

  /**
   * Method under test: {@link UserService#getUserByEmail(String)}
   */
  @Test
  void testGetUserByEmail3() {
    ArrayList<UserRepresentation> userRepresentationList = new ArrayList<>();
    userRepresentationList.add(new UserRepresentation());
    RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
    when(roleMappingResource.getAll()).thenThrow(new UserNotFoundException("foo"));
    UserResource userResource = mock(UserResource.class);
    when(userResource.roles()).thenReturn(roleMappingResource);
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.get(Mockito.<String>any())).thenReturn(userResource);
    when(usersResource.search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any())).thenReturn(userRepresentationList);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("jane.doe@example.org"));
    verify(keycloak, atLeast(1)).realm(Mockito.<String>any());
    verify(realmResource, atLeast(1)).users();
    verify(roleMappingResource).getAll();
    verify(userResource).roles();
    verify(usersResource).get(Mockito.<String>any());
    verify(usersResource).search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any());
  }

  /**
   * Method under test: {@link UserService#addUser(UserRequestDto)}
   */
  @Test
       void testAddUser() {
    ResponseEntity<UserResponseDto> actualAddUserResult = userService.addUser(
            new UserRequestDto("janedoe", "iloveyou", "jane.doe@example.org", "Jane", "Doe", "42 Main St", "Role"));
    assertNull(actualAddUserResult.getBody());
    assertEquals(500, actualAddUserResult.getStatusCodeValue());
    assertTrue(actualAddUserResult.getHeaders().isEmpty());
  }

  /**
   * Method under test: {@link UserService#addUser(UserRequestDto)}
   */
  @Test
  void testAddUser2() {
    ResponseEntity<UserResponseDto> actualAddUserResult = userService
            .addUser(new UserRequestDto(null, "iloveyou", "jane.doe@example.org", "Jane", "Doe", "42 Main St", "Role"));
    assertNull(actualAddUserResult.getBody());
    assertEquals(500, actualAddUserResult.getStatusCodeValue());
    assertTrue(actualAddUserResult.getHeaders().isEmpty());
  }

  /**
   * Method under test: {@link UserService#addUser(UserRequestDto)}
   */
  @Test
  void testAddUser3() {
    ResponseEntity<UserResponseDto> actualAddUserResult = userService
            .addUser(new UserRequestDto("", "iloveyou", "jane.doe@example.org", "Jane", "Doe", "42 Main St", "Role"));
    assertNull(actualAddUserResult.getBody());
    assertEquals(500, actualAddUserResult.getStatusCodeValue());
    assertTrue(actualAddUserResult.getHeaders().isEmpty());
  }

  /**
   * Method under test: {@link UserService#addUser(UserRequestDto)}
   */
  @Test
  void testAddUser4() {
    ResponseEntity<UserResponseDto> actualAddUserResult = userService.addUser(null);
    assertNull(actualAddUserResult.getBody());
    assertEquals(500, actualAddUserResult.getStatusCodeValue());
    assertTrue(actualAddUserResult.getHeaders().isEmpty());
  }

  /**
   * Method under test: {@link UserService#getAllUsersByRole(String)}
   */
  @Test
  void testGetAllUsersByRole() {
    assertThrows(RoleNotFoundException.class, () -> userService.getAllUsersByRole("Role"));
  }

  /**
   * Method under test: {@link UserService#deleteUserById(String)}
   */
  @Test
  void testDeleteUserById() {
    assertThrows(UserNotFoundException.class, () -> userService.deleteUserById("42"));
  }

  /**
   * Method under test: {@link UserService#isEmailExists(String)}
   */
  @Test
  void testIsEmailExists() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any())).thenReturn(new ArrayList<>());
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    boolean actualIsEmailExistsResult = userService.isEmailExists("jane.doe@example.org");
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any());
    assertFalse(actualIsEmailExistsResult);
  }

  /**
   * Method under test: {@link UserService#isEmailExists(String)}
   */
  @Test
  void testIsEmailExists2() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any()))
            .thenThrow(new UserNotFoundException("foo"));
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.isEmailExists("jane.doe@example.org"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any());
  }

  /**
   * Method under test: {@link UserService#isEmailExists(String)}
   */
  @Test
  void testIsEmailExists3() {
    ArrayList<UserRepresentation> userRepresentationList = new ArrayList<>();
    userRepresentationList.add(new UserRepresentation());
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any())).thenReturn(userRepresentationList);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    boolean actualIsEmailExistsResult = userService.isEmailExists("jane.doe@example.org");
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any());
    assertTrue(actualIsEmailExistsResult);
  }

  /**
   * Method under test: {@link UserService#isUsernameExists(String)}
   */
  @Test
  void testIsUsernameExists() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<Boolean>any())).thenReturn(new ArrayList<>());
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    boolean actualIsUsernameExistsResult = userService.isUsernameExists("janedoe");
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<Boolean>any());
    assertFalse(actualIsUsernameExistsResult);
  }

  /**
   * Method under test: {@link UserService#isUsernameExists(String)}
   */
  @Test
  void testIsUsernameExists2() {
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<Boolean>any()))
            .thenThrow(new UserNotFoundException("foo"));
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.isUsernameExists("janedoe"));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<Boolean>any());
  }

  /**
   * Method under test: {@link UserService#isUsernameExists(String)}
   */
  @Test
  void testIsUsernameExists3() {
    ArrayList<UserRepresentation> userRepresentationList = new ArrayList<>();
    userRepresentationList.add(new UserRepresentation());
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.search(Mockito.<String>any(), Mockito.<Boolean>any())).thenReturn(userRepresentationList);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    boolean actualIsUsernameExistsResult = userService.isUsernameExists("janedoe");
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(usersResource).search(Mockito.<String>any(), Mockito.<Boolean>any());
    assertTrue(actualIsUsernameExistsResult);
  }

  /**
   * Method under test:
   * {@link UserService#mapToUserResponseDto(UserRepresentation)}
   */
  @Test
  void testMapToUserResponseDto() {
    RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
    when(roleMappingResource.getAll()).thenThrow(new UserNotFoundException("foo"));
    UserResource userResource = mock(UserResource.class);
    when(userResource.roles()).thenReturn(roleMappingResource);
    UsersResource usersResource = mock(UsersResource.class);
    when(usersResource.get(Mockito.<String>any())).thenReturn(userResource);
    RealmResource realmResource = mock(RealmResource.class);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm(Mockito.<String>any())).thenReturn(realmResource);
    assertThrows(UserNotFoundException.class, () -> userService.mapToUserResponseDto(new UserRepresentation()));
    verify(keycloak).realm(Mockito.<String>any());
    verify(realmResource).users();
    verify(roleMappingResource).getAll();
    verify(userResource).roles();
    verify(usersResource).get(Mockito.<String>any());
  }
}

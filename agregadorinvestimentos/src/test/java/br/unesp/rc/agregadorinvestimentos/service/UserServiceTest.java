package br.unesp.rc.agregadorinvestimentos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.unesp.rc.agregadorinvestimentos.controller.UpdateUserDTO;
import br.unesp.rc.agregadorinvestimentos.controller.UserDTO;
import br.unesp.rc.agregadorinvestimentos.entity.User;
import br.unesp.rc.agregadorinvestimentos.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> existsByIdCaptor;

    @Captor
    private ArgumentCaptor<UUID> deleteByIdCaptor;

    @Nested
    class createUser {

        @Test
        @DisplayName("Should create a user with success")
        void shouldCreateUserWithSuccess() {
            // arrange
            var user = new User("userName", "userEmail", "userPassword", Instant.now(), null);
            var expectedId = UUID.randomUUID();
            user.setId(expectedId);

            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            var input = new UserDTO("userName", "userEmail", "userPassword");

            // act
            var output = userService.createUser(input);

            // assert
            assertEquals(expectedId, output);

            var userCaptured = userArgumentCaptor.getValue();
            assertEquals(input.name(), userCaptured.getName());
            assertEquals(input.email(), userCaptured.getEmail());
            assertEquals(input.password(), userCaptured.getPassword());
        }

        @Test
        @DisplayName("Should throw exception when user already exists")
        void shouldThrowExceptionWhenUserAlreadyExists() {
            // arrange
            doThrow(new RuntimeException()).when(userRepository).save(any());

            var input = new UserDTO(
                    "userName",
                    "userEmail",
                    "userPassword");

            // act & assert
            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }

    @Nested
    class getUserById {

        @Test
        @DisplayName("Should return user by ID when optional is present")
        void shouldReturnUserByIdWhenOptionalIsPresent() {
            // arrange
            UUID mockId = UUID.randomUUID();
            var user = new User("userName", "userEmail", "userPassword", Instant.now(), null);
            user.setId(mockId);

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act
            var output = userService.getUserById(mockId.toString());

            // assert
            assertTrue(output.isPresent());
            assertEquals(mockId, output.get().getId());

            UUID capturedId = uuidArgumentCaptor.getValue();
            assertEquals(mockId, capturedId);
        }

        @Test
        @DisplayName("Should return user by ID when optional is empty")
        void shouldReturnUserByIdWhenOptionalIsEmpty() {
            // arrange
            UUID mockId = UUID.randomUUID();
            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act
            var output = userService.getUserById(mockId.toString());

            // assert
            assertTrue(output.isEmpty());

            UUID capturedId = uuidArgumentCaptor.getValue();
            assertEquals(mockId, capturedId);
        }
    }

    @Nested
    class getAllUsers {

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            // arrange
            var user1 = new User("user1", "email1", "password1", Instant.now(), null);
            var user2 = new User("user2", "email2", "password2", Instant.now(), null);
            doReturn(List.of(user1, user2)).when(userRepository).findAll();

            // act
            var output = userService.getAllUsers();

            // assert
            assertEquals(2, output.size());
            assertTrue(output.contains(user1));
            assertTrue(output.contains(user2));
        }
    }

    @Nested
    class deleteById {

        @Test
        @DisplayName("Should delete user by ID when user exists")
        void shouldDeleteUserByIdWhenUserExists() {
            // arrange
            doReturn(true).when(userRepository).existsById(uuidArgumentCaptor.capture());
            doNothing().when(userRepository).deleteById(uuidArgumentCaptor.capture());

            var id = UUID.randomUUID();
            // act
            userService.deleteById(id.toString());

            // assert 
            var idList = uuidArgumentCaptor.getAllValues();
            assertEquals(id, idList.get(0));
            assertEquals(id, idList.get(1));

            verify(userRepository, times(1)).existsById(idList.get(0));
            verify(userRepository, times(1)).deleteById(idList.get(1));
        }

        @Test
        @DisplayName("Should not delete user by ID when user does not exist")
        void shouldNotDeleteUserByIdWhenUserDoesNotExist() {
            // arrange
            doReturn(false).when(userRepository).existsById(uuidArgumentCaptor.capture());

            var id = UUID.randomUUID();
            // act
            userService.deleteById(id.toString());

            // assert 
            assertEquals(id, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1)).existsById(uuidArgumentCaptor.getValue());

            verify(userRepository, times(0)).deleteById(any());
        }
    }

    @Nested
    class updateUserById {
        @Test
        @DisplayName("Should update user by ID when optional is present and name and password are provided")
        void shouldUpdateUserByIdWhenOptionalIsPresentAndNameAndPasswordAreProvided() {
            // arrange
            UUID mockId = UUID.randomUUID();
            var user = new User("userName", "userEmail", "userPassword", Instant.now(), null);
            user.setId(mockId);

            var updateUserDTO = new UpdateUserDTO("newName", "newPassword");

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            // act
            userService.updateUserById(mockId.toString(), updateUserDTO);

            // assert
            assertEquals(mockId, uuidArgumentCaptor.getValue());

            var userCaptured = userArgumentCaptor.getValue();

            assertEquals(updateUserDTO.name(), userCaptured.getName());
            assertEquals(updateUserDTO.password(), userCaptured.getPassword());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should not update user by ID when user does not exist")
        void shouldNotUpdateUserByIdWhenUserDoesNotExist() {
            // arrange
            UUID mockId = UUID.randomUUID();

            var updateUserDTO = new UpdateUserDTO("newName", "newPassword");

            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act
            userService.updateUserById(mockId.toString(), updateUserDTO);

            // assert
            assertEquals(mockId, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).save(any());
        }
    }
}

package com.koalanoir.souscription.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("create should throw IllegalArgumentException when user with email already exists")
    void create_shouldThrowException_whenUserWithEmailAlreadyExists() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User("id", "John", "0600000000", email)));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                createUserUseCase.create("0600000000", email, "John Doe")
        );

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("create should save and return new user when email does not exist")
    void create_shouldSaveAndReturnUser_whenEmailDoesNotExist() {
        // Arrange
        String phone = "0600000000";
        String email = "john.doe@example.com";
        String name = "John Doe";

        User persistedUser = new User("generated-id", name, phone, email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        // Act
        User result = createUserUseCase.create(phone, email, name);

        // Assert
        assertNotNull(result);
        assertEquals(persistedUser, result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User createdUser = userCaptor.getValue();
        assertNotNull(createdUser.getId());
        assertEquals(name, createdUser.getName());
        assertEquals(phone, createdUser.getPhone());
        assertEquals(email, createdUser.getEmail());
    }

    @Test
    @DisplayName("handle should create user from application command data")
    void handle_shouldCreateUserFromCommand() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "John Doe",
                "0600000000",
                "john.doe@example.com");

        User persistedUser = new User("generated-id", command.name(), command.phone(), command.email());
        when(userRepository.findByEmail(command.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        // Act
        User result = createUserUseCase.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals(persistedUser, result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User createdUser = userCaptor.getValue();
        assertEquals(command.name(), createdUser.getName());
        assertEquals(command.phone(), createdUser.getPhone());
        assertEquals(command.email(), createdUser.getEmail());
    }
}

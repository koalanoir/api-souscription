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
import com.koalanoir.souscription.infrastructure.primary.dtos.CreateSubscriptionRequest;

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
    @DisplayName("handle should create user from CreateSubscriptionRequest data")
    void handle_shouldCreateUserFromRequest() {
        // Arrange
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(
                "OFFER_PREMIUM",
                "John Doe",
                "john.doe@example.com",
                "0600000000",
                "PREMIUM");

        User persistedUser = new User("generated-id", request.clientName(), request.phoneNumber(), request.email());
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        // Act
        User result = createUserUseCase.handle(request);

        // Assert
        assertNotNull(result);
        assertEquals(persistedUser, result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User createdUser = userCaptor.getValue();
        assertEquals(request.clientName(), createdUser.getName());
        assertEquals(request.phoneNumber(), createdUser.getPhone());
        assertEquals(request.email(), createdUser.getEmail());
    }
}

package com.koalanoir.souscription.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.domain.models.SubscriptionStatus;
import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.domain.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
class CreateSubscriptionUseCaseTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private CreateSubscriptionUseCase createSubscriptionUseCase;

    @Test
    @DisplayName("create should throw IllegalArgumentException when subscription already exists")
    void create_shouldThrowException_whenSubscriptionAlreadyExists() {
        // Arrange
        String userId = "user123";
        when(subscriptionRepository.findById(userId)).thenReturn(Optional.of(mock(Subscription.class)));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            createSubscriptionUseCase.create(userId, "offer123", SubscriptionStatus.ACTIVE)
        );
        assertEquals("Subscription already exists", exception.getMessage());
    }

    @Test
    @DisplayName("create should save and return subscription when it does not exist")
    void create_shouldSaveAndReturnSubscription_whenItDoesNotExist() {
        // Arrange
        String userId = "user123";
        String offerId = "offer123";
        Subscription savedSubscription = new Subscription("generated-id", userId, offerId, SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findById(userId)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSubscription);

        // Act
        Subscription result = createSubscriptionUseCase.create(userId, offerId, SubscriptionStatus.ACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(savedSubscription, result);

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository, times(1)).save(subscriptionCaptor.capture());

        Subscription createdSubscription = subscriptionCaptor.getValue();
        assertNotNull(createdSubscription.getId(), "Generated subscription id should not be null");
        assertEquals(userId, createdSubscription.getUserId());
        assertEquals(offerId, createdSubscription.getOfferId());
        assertEquals(SubscriptionStatus.ACTIVE, createdSubscription.getStatus());
    }

    @Test
    @DisplayName("handle should delegate to create method and return subscription")
    void handle_shouldDelegateToCreateMethod() {
        // Arrange
        User user = new User("user123", "John Doe","0749923789","koalanoir@sg.com");
        CreateSubscriptionCommand command = new CreateSubscriptionCommand(
            "offer123",
            "PREMIUM");

        Subscription savedSubscription = new Subscription("generated-id", user.getId(), command.offerId(), SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSubscription);

        // Act
        Subscription result = createSubscriptionUseCase.handle(command, user);

        // Assert
        assertNotNull(result);
        assertEquals(savedSubscription, result);

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository, times(1)).save(subscriptionCaptor.capture());

        Subscription createdSubscription = subscriptionCaptor.getValue();
        assertNotNull(createdSubscription.getId());
        assertEquals(user.getId(), createdSubscription.getUserId());
        assertEquals(command.offerId(), createdSubscription.getOfferId());
        assertEquals(SubscriptionStatus.ACTIVE, createdSubscription.getStatus());
    }
}
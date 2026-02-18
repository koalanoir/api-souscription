package com.koalanoir.souscription.infrastructure.primary.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koalanoir.souscription.application.usecases.CreateSubscriptionUseCase;
import com.koalanoir.souscription.application.usecases.CreateUserUseCase;
import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.infrastructure.secondary.persistence.models.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private CreateSubscriptionUseCase createSubscriptionUseCase;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("subscribe should create user and subscription then return 200 with response body")
    void subscribe_shouldCreateUserAndSubscription_whenRequestIsValid() throws Exception {
        // Arrange
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();

        String requestJson = "{" +
                "\"offerId\":\"OFFER_PREMIUM\"," +
                "\"clientName\":\"John Doe\"," +
                "\"email\":\"john.doe@example.com\"," +
                "\"phoneNumber\":\"0600000000\"," +
                "\"subscriptionType\":\"PREMIUM\"" +
                "}";

        User createdUser = new User("user-id", "John Doe", "0600000000", "john.doe@example.com");
        Subscription createdSubscription = new Subscription("sub-id", createdUser.getId(), "OFFER_PREMIUM", SubscriptionStatus.ACTIVE);

        when(createUserUseCase.handle(any())).thenReturn(createdUser);
        when(createSubscriptionUseCase.handle(any(), any(User.class))).thenReturn(createdSubscription);

        // Act & Assert
        mockMvc.perform(post("/api/v1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value("sub-id"))
                .andExpect(jsonPath("$.userId").value("user-id"));

        verify(createUserUseCase, times(1)).handle(any());
        verify(createSubscriptionUseCase, times(1)).handle(any(), any(User.class));
    }
}

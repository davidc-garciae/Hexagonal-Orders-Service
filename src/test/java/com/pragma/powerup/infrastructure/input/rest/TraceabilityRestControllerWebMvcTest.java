package com.pragma.powerup.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pragma.powerup.application.dto.response.RestaurantMetricsResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityEventResponseDto;
import com.pragma.powerup.application.handler.ITraceabilityHandler;
import com.pragma.powerup.infrastructure.security.JwtValidator;
import com.pragma.powerup.infrastructure.security.SecurityConfiguration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TraceabilityRestController.class)
@Import({ TraceabilityRestControllerWebMvcTest.TestConfig.class, SecurityConfiguration.class })
class TraceabilityRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITraceabilityHandler traceabilityHandler;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getOrderTraceability_WhenValidRequest_ShouldReturnTraceabilityEvents() throws Exception {
        // Given
        Long orderId = 1L;
        TraceabilityEventResponseDto event1 = new TraceabilityEventResponseDto();
        event1.setOrderId(orderId);
        event1.setPreviousStatus("PENDING");
        event1.setNewStatus("IN_PREPARATION");
        event1.setTimestamp(LocalDateTime.now().minusHours(2));

        TraceabilityEventResponseDto event2 = new TraceabilityEventResponseDto();
        event2.setOrderId(orderId);
        event2.setPreviousStatus("IN_PREPARATION");
        event2.setNewStatus("READY");
        event2.setTimestamp(LocalDateTime.now().minusHours(1));

        List<TraceabilityEventResponseDto> events = List.of(event1, event2);

        when(traceabilityHandler.getOrderTraceability(eq(orderId), any())).thenReturn(events);

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/orders/{id}/traceability", orderId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "1")
                                .header("X-User-Email", "customer@test.com")
                                .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(orderId))
                .andExpect(jsonPath("$[0].previousStatus").value("PENDING"))
                .andExpect(jsonPath("$[0].newStatus").value("IN_PREPARATION"))
                .andExpect(jsonPath("$[1].previousStatus").value("IN_PREPARATION"))
                .andExpect(jsonPath("$[1].newStatus").value("READY"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void getRestaurantMetrics_WhenValidRequest_ShouldReturnMetrics() throws Exception {
        // Given
        Long restaurantId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        RestaurantMetricsResponseDto metrics = new RestaurantMetricsResponseDto();
        metrics.setRestaurantId(restaurantId);
        metrics.setRestaurantName("Test Restaurant");
        metrics.setTotalOrdersProcessed(100);
        metrics.setAverageOrderPreparationTime("25:30");
        metrics.setAverageOrderDeliveryTime("15:12");
        metrics.setActiveEmployees(3);

        when(traceabilityHandler.getRestaurantMetrics(
                eq(restaurantId), eq(startDate), eq(endDate), any()))
                .thenReturn(metrics);

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/restaurants/{id}/metrics", restaurantId)
                                .param("startDate", "2024-01-01")
                                .param("endDate", "2024-12-31")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "2")
                                .header("X-User-Email", "owner@test.com")
                                .header("X-User-Role", "OWNER"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.restaurantName").value("Test Restaurant"))
                .andExpect(jsonPath("$.totalOrdersProcessed").value(100))
                .andExpect(jsonPath("$.averageOrderPreparationTime").value("25:30"))
                .andExpect(jsonPath("$.averageOrderDeliveryTime").value("15:12"))
                .andExpect(jsonPath("$.activeEmployees").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderTraceability_WhenAdmin_ShouldHaveAccess() throws Exception {
        // Given
        Long orderId = 1L;
        List<TraceabilityEventResponseDto> events = List.of();

        when(traceabilityHandler.getOrderTraceability(eq(orderId), any())).thenReturn(events);

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/orders/{id}/traceability", orderId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "3")
                                .header("X-User-Email", "admin@test.com")
                                .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getRestaurantMetrics_WhenEmployee_ShouldHaveAccess() throws Exception {
        // Given
        Long restaurantId = 1L;
        RestaurantMetricsResponseDto metrics = new RestaurantMetricsResponseDto();
        metrics.setRestaurantId(restaurantId);

        when(traceabilityHandler.getRestaurantMetrics(eq(restaurantId), isNull(), isNull(), any()))
                .thenReturn(metrics);

        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/restaurants/{id}/metrics", restaurantId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "4")
                                .header("X-User-Email", "employee@test.com")
                                .header("X-User-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderTraceability_WhenNoAuthentication_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/orders/{id}/traceability", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRestaurantMetrics_WhenNoAuthentication_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/restaurants/{id}/metrics", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getRestaurantMetrics_WhenCustomerRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/restaurants/{id}/metrics", 1L)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "5")
                                .header("X-User-Email", "customer@test.com")
                                .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getOrderTraceability_WhenInvalidOrderId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/orders/{id}/traceability", -1)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "1")
                                .header("X-User-Email", "customer@test.com")
                                .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void getRestaurantMetrics_WhenInvalidRestaurantId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc
                .perform(
                        get("/api/v1/restaurants/{id}/metrics", 0)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "2")
                                .header("X-User-Email", "owner@test.com")
                                .header("X-User-Role", "OWNER"))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        JwtValidator jwtValidator() {
            return Mockito.mock(JwtValidator.class);
        }
    }
}

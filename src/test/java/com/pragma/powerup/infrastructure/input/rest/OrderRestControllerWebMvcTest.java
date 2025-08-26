package com.pragma.powerup.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.request.OrderCreateRequestDto.OrderItemRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IOrderHandler orderHandler;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        IOrderHandler orderHandler() {
            return Mockito.mock(IOrderHandler.class);
        }
    }

    @Test
    @DisplayName("POST /api/v1/pedidos returns 201 when valid and user matches header")
    void createOrder_created() throws Exception {
        OrderCreateRequestDto req = new OrderCreateRequestDto();
        req.setCustomerId(5L);
        req.setRestaurantId(10L);
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setDishId(100L);
        item.setQuantity(2);
        req.setItems(List.of(item));

        OrderResponseDto resp = new OrderResponseDto();
        resp.setId(1L);
        Mockito.when(orderHandler.createOrder(Mockito.any())).thenReturn(resp);

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", "5")
                .header("X-User-Email", "c@a.com")
                .header("X-User-Role", "CUSTOMER")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/pedidos returns 403 when header user differs from body")
    void createOrder_forbiddenWhenUserMismatch() throws Exception {
        OrderCreateRequestDto req = new OrderCreateRequestDto();
        req.setCustomerId(6L);
        req.setRestaurantId(10L);
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setDishId(100L);
        item.setQuantity(2);
        req.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", "5")
                .header("X-User-Email", "c@a.com")
                .header("X-User-Role", "CUSTOMER")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}

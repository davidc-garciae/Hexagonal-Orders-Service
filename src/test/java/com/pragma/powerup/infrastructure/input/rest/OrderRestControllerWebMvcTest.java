package com.pragma.powerup.infrastructure.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.request.OrderCreateRequestDto.OrderItemRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IOrderHandler orderHandler;
    @Autowired
    private com.pragma.powerup.application.handler.IOrderQueryHandler orderQueryHandler;
    @Autowired
    private com.pragma.powerup.application.handler.IOrderAssignHandler orderAssignHandler;
    @Autowired
    private com.pragma.powerup.application.handler.IOrderReadyHandler orderReadyHandler;
    @Autowired
    private com.pragma.powerup.application.handler.IOrderCancelHandler orderCancelHandler;

    @Autowired
    private com.pragma.powerup.application.handler.IOrderDeliverHandler orderDeliverHandler;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        IOrderHandler orderHandler() {
            return Mockito.mock(IOrderHandler.class);
        }

        @Bean
        com.pragma.powerup.application.handler.IOrderQueryHandler orderQueryHandler() {
            return Mockito.mock(com.pragma.powerup.application.handler.IOrderQueryHandler.class);
        }

        @Bean
        com.pragma.powerup.application.handler.IOrderAssignHandler orderAssignHandler() {
            return Mockito.mock(com.pragma.powerup.application.handler.IOrderAssignHandler.class);
        }

        @Bean
        com.pragma.powerup.application.handler.IOrderReadyHandler orderReadyHandler() {
            return Mockito.mock(com.pragma.powerup.application.handler.IOrderReadyHandler.class);
        }

        @Bean
        com.pragma.powerup.application.handler.IOrderCancelHandler orderCancelHandler() {
            return Mockito.mock(com.pragma.powerup.application.handler.IOrderCancelHandler.class);
        }

        @Bean
        com.pragma.powerup.application.handler.IOrderDeliverHandler orderDeliverHandler() {
            return Mockito.mock(com.pragma.powerup.application.handler.IOrderDeliverHandler.class);
        }
    }

    @Test
    @DisplayName("PUT /api/v1/orders/{id}/cancel returns 200")
    void cancel_ok() throws Exception {
        var resp = new OrderResponseDto();
        resp.setId(1L);
        Mockito.when(orderCancelHandler.cancel(Mockito.anyLong(), Mockito.anyLong())).thenReturn(resp);

        mockMvc
                .perform(
                        put("/api/v1/orders/1/cancel")
                                .header("X-User-Id", "7")
                                .header("X-User-Email", "c@x.com")
                                .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/orders/{id}/deliver returns 200")
    void deliver_ok() throws Exception {
        var resp = new OrderResponseDto();
        resp.setId(1L);
        Mockito.when(orderDeliverHandler.deliver(Mockito.anyLong(), Mockito.any())).thenReturn(resp);

        mockMvc
                .perform(
                        put("/api/v1/orders/1/deliver")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"pin\":\"123456\"}")
                                .header("X-User-Id", "9")
                                .header("X-User-Email", "e@x.com")
                                .header("X-User-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/orders/{id}/ready returns 200")
    void ready_ok() throws Exception {
        var resp = new OrderResponseDto();
        resp.setId(1L);
        Mockito.when(orderReadyHandler.markReady(Mockito.anyLong())).thenReturn(resp);

        mockMvc
                .perform(
                        put("/api/v1/orders/1/ready")
                                .header("X-User-Id", "9")
                                .header("X-User-Email", "e@x.com")
                                .header("X-User-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/v1/orders/{id}/assign returns 200")
    void assign_ok() throws Exception {
        var resp = new OrderResponseDto();
        resp.setId(1L);
        Mockito.when(orderAssignHandler.assign(Mockito.anyLong(), Mockito.anyLong())).thenReturn(resp);

        mockMvc
                .perform(
                        put("/api/v1/orders/1/assign")
                                .header("X-User-Id", "9")
                                .header("X-User-Email", "e@x.com")
                                .header("X-User-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/orders returns 201 when valid and user matches header")
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

        mockMvc
                .perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "5")
                                .header("X-User-Email", "c@a.com")
                                .header("X-User-Role", "CUSTOMER")
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/orders returns 403 when header user differs from body")
    void createOrder_forbiddenWhenUserMismatch() throws Exception {
        OrderCreateRequestDto req = new OrderCreateRequestDto();
        req.setCustomerId(6L);
        req.setRestaurantId(10L);
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setDishId(100L);
        item.setQuantity(2);
        req.setItems(List.of(item));

        mockMvc
                .perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", "5")
                                .header("X-User-Email", "c@a.com")
                                .header("X-User-Role", "CUSTOMER")
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/orders returns 200 with paging")
    void listOrders_ok() throws Exception {
        com.pragma.powerup.application.dto.response.OrderPageResponseDto page = new com.pragma.powerup.application.dto.response.OrderPageResponseDto();
        page.setPage(0);
        page.setSize(10);
        page.setTotalElements(0);
        page.setTotalPages(0);
        Mockito.when(
                orderQueryHandler.listByStatusAndRestaurant(
                        Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(page);

        mockMvc
                .perform(
                        get("/api/v1/orders")
                                .param("status", "PENDIENTE")
                                .param("restaurantId", "10")
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", "5")
                                .header("X-User-Email", "e@x.com")
                                .header("X-User-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }
}

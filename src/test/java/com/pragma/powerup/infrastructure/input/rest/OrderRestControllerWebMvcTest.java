package com.pragma.powerup.infrastructure.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.request.OrderCreateRequestDto.OrderItemRequestDto;
import com.pragma.powerup.application.dto.request.OrderDeliverRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.infrastructure.security.JwtValidator;
import java.util.List;
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

@WebMvcTest(OrderRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(OrderRestControllerWebMvcTest.TestConfig.class)
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
    com.pragma.powerup.domain.spi.IUserServicePort userServicePort() {
      return Mockito.mock(com.pragma.powerup.domain.spi.IUserServicePort.class);
    }
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

    @Bean
    JwtValidator jwtValidator() {
      JwtValidator mock = Mockito.mock(JwtValidator.class);
      Mockito.when(mock.isValid(Mockito.anyString())).thenReturn(true);
      io.jsonwebtoken.Claims claims = Mockito.mock(io.jsonwebtoken.Claims.class);
      Mockito.when(claims.getSubject()).thenReturn("e@x.com");
      Mockito.when(claims.get("userId", String.class)).thenReturn("5");
      Mockito.when(claims.get("role", String.class)).thenReturn("EMPLOYEE");
      Mockito.when(mock.validateAndExtractClaims(Mockito.anyString())).thenReturn(claims);
      Mockito.when(mock.extractUserId(Mockito.anyString())).thenReturn("5");
      Mockito.when(mock.extractEmail(Mockito.anyString())).thenReturn("e@x.com");
      return mock;
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
    Mockito.when(orderDeliverHandler.deliver(
        Mockito.anyLong(),
        Mockito.<OrderDeliverRequestDto>any(),
        Mockito.anyLong())).thenReturn(resp);

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
    Mockito.when(orderReadyHandler.markReady(
        Mockito.anyLong(),
        Mockito.anyLong())).thenReturn(resp);

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

}

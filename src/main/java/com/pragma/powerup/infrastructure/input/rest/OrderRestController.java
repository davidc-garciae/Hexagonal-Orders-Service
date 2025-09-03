package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.request.OrderDeliverRequestDto;
import com.pragma.powerup.application.dto.response.OrderPageResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderAssignHandler;
import com.pragma.powerup.application.handler.IOrderCancelHandler;
import com.pragma.powerup.application.handler.IOrderDeliverHandler;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.handler.IOrderQueryHandler;
import com.pragma.powerup.application.handler.IOrderReadyHandler;
import com.pragma.powerup.infrastructure.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderRestController {

  private final IOrderHandler orderHandler;
  private final IOrderQueryHandler orderQueryHandler;
  private final IOrderAssignHandler orderAssignHandler;
  private final IOrderReadyHandler orderReadyHandler;
  private final IOrderCancelHandler orderCancelHandler;
  private final IOrderDeliverHandler orderDeliverHandler;

  // Service para validar empleados y owners
  private final com.pragma.powerup.domain.spi.IUserServicePort userServicePort;

  @Operation(summary = "Create order (CUSTOMER)")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Created"),
    @ApiResponse(responseCode = "400", description = "Bad Request"),
    @ApiResponse(responseCode = "409", description = "Conflict")
  })
  @PostMapping
  @PreAuthorize("hasRole('" + RoleConstants.CUSTOMER + "')")
  public ResponseEntity<OrderResponseDto> create(
      @Valid @RequestBody OrderCreateRequestDto requestDto, HttpServletRequest httpRequest) {

    Long userId = extractUserId();
    if (userId == null || !userId.equals(requestDto.getCustomerId())) {
      return ResponseEntity.status(403).build();
    }

    OrderResponseDto response = orderHandler.createOrder(requestDto);
    return ResponseEntity.status(201).body(response);
  }

  @Operation(summary = "List orders by status and restaurant (EMPLOYEE/OWNER)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Bad Request"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  @org.springframework.web.bind.annotation.GetMapping
  @org.springframework.security.access.prepost.PreAuthorize(
      "hasAnyRole('" + RoleConstants.EMPLOYEE + "','" + RoleConstants.OWNER + "')")
  public ResponseEntity<OrderPageResponseDto> list(
      @org.springframework.web.bind.annotation.RequestParam("status") String status,
      @org.springframework.web.bind.annotation.RequestParam("restaurantId") Long restaurantId,
      @org.springframework.web.bind.annotation.RequestParam(value = "page", defaultValue = "0")
          int page,
      @org.springframework.web.bind.annotation.RequestParam(value = "size", defaultValue = "10")
          int size,
      HttpServletRequest httpRequest) {

    Long userId = extractUserId();
    if (userId == null) {
      return ResponseEntity.status(401).build();
    }

    // Validar que el usuario tenga permisos para ver pedidos de este restaurante
    if (!userServicePort.isEmployeeOfRestaurant(userId, restaurantId)
        && !userServicePort.isOwnerOfRestaurant(userId, restaurantId)) {
      return ResponseEntity.status(403).build();
    }

    OrderPageResponseDto result =
        orderQueryHandler.listByStatusAndRestaurant(restaurantId, status, page, size);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "Get my orders (CUSTOMER)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  @org.springframework.web.bind.annotation.GetMapping("/my-orders")
  @org.springframework.security.access.prepost.PreAuthorize(
      "hasRole('" + RoleConstants.CUSTOMER + "')")
  public ResponseEntity<OrderPageResponseDto> getMyOrders(
      @org.springframework.web.bind.annotation.RequestParam(value = "status", required = false)
          String status,
      @org.springframework.web.bind.annotation.RequestParam(value = "page", defaultValue = "0")
          int page,
      @org.springframework.web.bind.annotation.RequestParam(value = "size", defaultValue = "10")
          int size,
      HttpServletRequest httpRequest) {

    Long customerId = extractUserId();
    if (customerId == null) {
      return ResponseEntity.status(401).build();
    }

    OrderPageResponseDto result = orderQueryHandler.listByCustomer(customerId, status, page, size);
    return ResponseEntity.ok(result);
  }

  private Long extractUserId() {
    // Extraer userId del JWT a través del SecurityContext
    var auth =
        org.springframework.security.core.context.SecurityContextHolder.getContext()
            .getAuthentication();

    if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
      // El userId está en AuthDetails, no en el principal
      Object details = auth.getDetails();
      if (details
          instanceof
          com.pragma.powerup.infrastructure.security.JwtAuthenticationFilter.AuthDetails
                  authDetails) {
        try {
          return Long.valueOf(authDetails.getUserId());
        } catch (NumberFormatException e) {
          return null;
        }
      }
    }
    return null;
  }

  @Operation(summary = "Assign order (EMPLOYEE)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Bad Request"),
    @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @org.springframework.web.bind.annotation.PutMapping("/{id}/assign")
  @org.springframework.security.access.prepost.PreAuthorize(
      "hasRole('" + RoleConstants.EMPLOYEE + "')")
  public ResponseEntity<com.pragma.powerup.application.dto.response.OrderResponseDto> assign(
      @org.springframework.web.bind.annotation.PathVariable("id") Long orderId,
      HttpServletRequest httpRequest) {
    Long employeeId = extractUserId();
    var resp = orderAssignHandler.assign(orderId, employeeId);
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "Mark order as ready (EMPLOYEE)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Bad Request"),
    @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @org.springframework.web.bind.annotation.PutMapping("/{id}/ready")
  @org.springframework.security.access.prepost.PreAuthorize(
      "hasRole('" + RoleConstants.EMPLOYEE + "')")
  public ResponseEntity<com.pragma.powerup.application.dto.response.OrderResponseDto> ready(
      @org.springframework.web.bind.annotation.PathVariable("id") Long orderId,
      HttpServletRequest httpRequest) {
    Long employeeId = extractUserId();
    var resp = orderReadyHandler.markReady(orderId, employeeId);
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "Cancel order (CUSTOMER)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Bad Request"),
    @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @org.springframework.web.bind.annotation.PutMapping("/{id}/cancel")
  @org.springframework.security.access.prepost.PreAuthorize(
      "hasRole('" + RoleConstants.CUSTOMER + "')")
  public ResponseEntity<com.pragma.powerup.application.dto.response.OrderResponseDto> cancel(
      @org.springframework.web.bind.annotation.PathVariable("id") Long orderId,
      HttpServletRequest httpRequest) {
    Long customerId = extractUserId();
    var resp = orderCancelHandler.cancel(orderId, customerId);
    return ResponseEntity.ok(resp);
  }

  @Operation(summary = "Deliver order (EMPLOYEE)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Bad Request"),
    @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @org.springframework.web.bind.annotation.PutMapping("/{id}/deliver")
  @org.springframework.security.access.prepost.PreAuthorize(
      "hasRole('" + RoleConstants.EMPLOYEE + "')")
  public ResponseEntity<com.pragma.powerup.application.dto.response.OrderResponseDto> deliver(
      @org.springframework.web.bind.annotation.PathVariable("id") Long orderId,
      @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid
          OrderDeliverRequestDto request,
      HttpServletRequest httpRequest) {
    Long employeeId = extractUserId();
    var resp = orderDeliverHandler.deliver(orderId, request, employeeId);
    return ResponseEntity.ok(resp);
  }
}

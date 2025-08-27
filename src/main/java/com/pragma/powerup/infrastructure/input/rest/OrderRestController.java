package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.response.OrderPageResponseDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.handler.IOrderQueryHandler;
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

        Long userId = extractUserId(httpRequest);
        if (!userId.equals(requestDto.getCustomerId())) {
            return ResponseEntity.status(403).build();
        }

        OrderResponseDto response = orderHandler.createOrder(requestDto);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "List orders by status and restaurant (EMPLOYEE/OWNER)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @org.springframework.web.bind.annotation.GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('" + RoleConstants.EMPLOYEE + "','"
            + RoleConstants.OWNER + "')")
    public ResponseEntity<OrderPageResponseDto> list(
            @org.springframework.web.bind.annotation.RequestParam("status") String status,
            @org.springframework.web.bind.annotation.RequestParam("restaurantId") Long restaurantId,
            @org.springframework.web.bind.annotation.RequestParam(value = "page", defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Long userId = extractUserId(httpRequest);
        // Nota: validación de pertenencia a restaurante se integrará con users-service
        // en HU posterior.
        OrderPageResponseDto result = orderQueryHandler.listByStatusAndRestaurant(restaurantId, status, page, size);
        return ResponseEntity.ok(result);
    }

    private Long extractUserId(HttpServletRequest request) {
        String id = request.getHeader("X-User-Id");
        return id == null ? null : Long.valueOf(id);
    }
}

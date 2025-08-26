package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @Operation(summary = "Create order (CUSTOMER)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PostMapping
    @PreAuthorize("hasRole('" + RoleConstants.CUSTOMER + "')")
    public ResponseEntity<OrderResponseDto> create(
            @Valid @RequestBody OrderCreateRequestDto requestDto,
            HttpServletRequest httpRequest) {

        Long userId = extractUserId(httpRequest);
        if (!userId.equals(requestDto.getCustomerId())) {
            return ResponseEntity.status(403).build();
        }

        OrderResponseDto response = orderHandler.createOrder(requestDto);
        return ResponseEntity.status(201).body(response);
    }

    private Long extractUserId(HttpServletRequest request) {
        String id = request.getHeader("X-User-Id");
        return id == null ? null : Long.valueOf(id);
    }
}

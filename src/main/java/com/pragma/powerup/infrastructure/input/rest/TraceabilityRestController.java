package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.response.RestaurantMetricsResponseDto;
import com.pragma.powerup.application.dto.response.TraceabilityEventResponseDto;
import com.pragma.powerup.application.handler.ITraceabilityHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Traceability", description = "Order traceability and restaurant metrics operations")
public class TraceabilityRestController {

    private final ITraceabilityHandler traceabilityHandler;

    @GetMapping("/orders/{id}/traceability")
    @Operation(summary = "Get order traceability", description = "Retrieve the complete traceability history for a specific order. Access is restricted based on user role: customers can only see their own orders, employees and owners can see orders from their restaurant, and admins can see any order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Traceability retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = TraceabilityEventResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('EMPLOYEE') or hasRole('CUSTOMER')")
    public ResponseEntity<List<TraceabilityEventResponseDto>> getOrderTraceability(
            @Parameter(description = "Order ID", required = true, example = "1") @PathVariable("id") @Positive Long orderId,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authToken) {

        List<TraceabilityEventResponseDto> traceability = traceabilityHandler.getOrderTraceability(orderId, authToken);
        return ResponseEntity.ok(traceability);
    }

    @GetMapping("/restaurants/{id}/metrics")
    @Operation(summary = "Get restaurant efficiency metrics (OWNER ONLY)", description = "Retrieve efficiency metrics for a restaurant including average preparation/delivery times and employee performance rankings. "
            + "According to Requirements.md HU-018: Only the restaurant owner can access this information. "
            + "Provides: 1) Metric per order: total time from start to delivery, "
            + "2) Employee ranking: average time per employee, "
            + "3) Calculations: time per state, total order time, employee comparisons. "
            + "TIME FORMAT: All time values are returned in MM:SS format (minutes:seconds) for better readability. "
            + "Example: '2:30' means 2 minutes and 30 seconds. Includes averageOrderPreparationTime, averageOrderDeliveryTime, averagePreparationTime, and averageDeliveryTime.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully - All time values in MM:SS format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantMetricsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Only restaurant owner can view metrics"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RestaurantMetricsResponseDto> getRestaurantMetrics(
            @Parameter(description = "Restaurant ID", required = true, example = "1") @PathVariable("id") @Positive Long restaurantId,
            @Parameter(description = "Start date for metrics calculation (ISO format: YYYY-MM-DD)", example = "2024-01-01") @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for metrics calculation (ISO format: YYYY-MM-DD)", example = "2024-12-31") @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authToken) {

        RestaurantMetricsResponseDto metrics = traceabilityHandler.getRestaurantMetrics(restaurantId, startDate,
                endDate, authToken);
        return ResponseEntity.ok(metrics);
    }
}

package eu.dreamix.rmi.migration.adapter.in.rest;

import eu.dreamix.rmi.migration.adapter.in.rest.dto.OrderDto;
import eu.dreamix.rmi.migration.domain.Order;
import eu.dreamix.rmi.migration.usecase.OrderService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for Order resources.
 */
@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Get an order by its ID.
     *
     * @param id The order ID
     * @return The order details
     */
    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Find order by ID",
        description = "Returns a single order based on the provided ID",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Order found",
                content = @Content(schema = @Schema(implementation = OrderDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @Timed(value = "api.orders.getById.time", description = "Time taken to retrieve an order by ID")
    public ResponseEntity<OrderDto> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable @Positive(message = "Order ID must be a positive number") Integer id) {
        
        logger.info("Retrieving order with ID: {}", id);
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                logger.warn("Order with ID {} not found", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }
            
            OrderDto orderDto = OrderDto.fromDomain(order);
            logger.debug("Successfully retrieved order: {}", orderDto);
            return ResponseEntity.ok(orderDto);
        } catch (Exception e) {
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            logger.error("Error retrieving order with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                                             "An error occurred while retrieving the order", e);
        }
    }
}

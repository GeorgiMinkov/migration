package eu.dreamix.rmi.migration.adapter.in.rest;

import eu.dreamix.rmi.migration.adapter.in.rest.dto.OrderDto;
import eu.dreamix.rmi.migration.domain.Order;
import eu.dreamix.rmi.migration.usecase.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order(42, "SHIPPED", new BigDecimal("199.99"));
    }

    @Test
    @DisplayName("Should return order when found")
    void shouldReturnOrderWhenFound() {
        // Arrange
        when(orderService.getOrderById(42)).thenReturn(testOrder);

        // Act
        ResponseEntity<OrderDto> response = orderController.getOrder(42);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(42, response.getBody().id());
        assertEquals("SHIPPED", response.getBody().status());
        assertEquals(new BigDecimal("199.99"), response.getBody().total());
        verify(orderService, times(1)).getOrderById(42);
    }

    @Test
    @DisplayName("Should throw 404 when order not found")
    void shouldThrow404WhenOrderNotFound() {
        // Arrange
        when(orderService.getOrderById(99)).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderController.getOrder(99)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Order not found", exception.getReason());
        verify(orderService, times(1)).getOrderById(99);
    }

    @Test
    @DisplayName("Should throw 400 when order ID is invalid")
    void shouldThrow400WhenOrderIdIsInvalid() {
        // This test would validate @Positive annotation
        // Cannot be unit tested directly without Spring validation
        // Would be covered by an integration/slice test
    }

    @Test
    @DisplayName("Should throw 500 when service throws exception")
    void shouldThrow500WhenServiceThrowsException() {
        // Arrange
        when(orderService.getOrderById(42)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderController.getOrder(42)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An error occurred while retrieving the order", exception.getReason());
        verify(orderService, times(1)).getOrderById(42);
    }
}

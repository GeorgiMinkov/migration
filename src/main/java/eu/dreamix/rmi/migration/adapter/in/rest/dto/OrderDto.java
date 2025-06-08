package eu.dreamix.rmi.migration.adapter.in.rest.dto;

import eu.dreamix.rmi.migration.domain.Order;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Order entities.
 */
public record OrderDto(
    Integer id,
    String status,
    BigDecimal total
) {
    /**
     * Factory method to create an OrderDto from a domain Order object.
     *
     * @param order The domain order entity
     * @return A new OrderDto instance
     */
    public static OrderDto fromDomain(Order order) {
        return new OrderDto(
            order.getId(),
            order.getStatus(),
            order.getTotal()
        );
    }
}

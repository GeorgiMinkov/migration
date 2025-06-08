
package eu.dreamix.rmi.migration.usecase;

import eu.dreamix.rmi.migration.adapter.out.rmi.OrderRmiService;
import eu.dreamix.rmi.migration.domain.Order;
import eu.dreamix.rmi.migration.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRmiService rmiService;
    private final OrderRepository repository;

    public OrderService(
            OrderRmiService rmiService,
            OrderRepository repository) {
        this.rmiService = rmiService;
        this.repository = repository;
    }

    public Order getOrderById(Integer id) {
        try {
            return rmiService.findOrderById(id);
        } catch (java.rmi.RemoteException e) {
            logger.error("Error calling RMI service for order ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve order via RMI", e);
        }
    }
}

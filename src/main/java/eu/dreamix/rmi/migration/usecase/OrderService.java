
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
    private final boolean useRestOrder;
    private final Counter rmiUsageCounter;

    public OrderService(
            OrderRmiService rmiService,
            OrderRepository repository,
            @Value("${feature.flag.useRest_order:false}") boolean useRestOrder,
            MeterRegistry meterRegistry) {
        this.rmiService = rmiService;
        this.repository = repository;
        this.useRestOrder = useRestOrder;
        this.rmiUsageCounter = meterRegistry.counter("rmi.usage.count", "method", "findOrderById");
        
        logger.info("OrderService initialized with useRest_order flag set to: {}", useRestOrder);
    }

    /**
     * Gets an order by its ID, using either the repository or RMI service based on feature flag.
     *
     * @param id The order ID to look up
     * @return The found Order object
     * @throws RuntimeException if there's an error accessing the RMI service
     */
    public Order getOrderById(Integer id) {
        if (useRestOrder) {
            logger.debug("Using repository to fetch order with ID: {}", id);
            return repository.findById(id);
        } else {
            logger.debug("Using RMI service to fetch order with ID: {}", id);
            rmiUsageCounter.increment();
            try {
                return rmiService.findOrderById(id);
            } catch (java.rmi.RemoteException e) {
                logger.error("Error calling RMI service for order ID: {}", id, e);
                throw new RuntimeException("Failed to retrieve order via RMI", e);
            }
        }
    }
}

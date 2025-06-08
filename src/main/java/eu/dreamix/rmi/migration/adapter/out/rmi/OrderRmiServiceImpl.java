package eu.dreamix.rmi.migration.adapter.out.rmi;

import eu.dreamix.rmi.migration.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.rmi.RemoteException;

@Service
public class OrderRmiServiceImpl implements OrderRmiService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderRmiServiceImpl.class);
    

    public Order findOrderById(Integer id) throws RemoteException {
        logger.info("RMI Service: Finding order with ID: {}", id);
        
        // In a real implementation, this would connect to the legacy RMI service
        // For demonstration purposes, we're simulating the response
        if (id == null || id <= 0) {
            logger.warn("Invalid order ID: {}", id);
            return null;
        }
        
        // Simulate successful order retrieval
        // Using proper constructor with all required parameters
        java.math.BigDecimal total = new java.math.BigDecimal(id * 10.0);
        Order order = new Order(id, "CONFIRMED", total);
        
        logger.debug("RMI Service: Retrieved order: {}", order);
        return order;
    }
}

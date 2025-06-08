
package eu.dreamix.rmi.migration.repository;

import eu.dreamix.rmi.migration.domain.Order;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    private static final Map<Integer, Order> orders = new HashMap<>();
    static {
        orders.put(1, new Order(1, "PENDING", new BigDecimal(100)));
    }
    
    public Order findById(Integer id) {
        return orders.get(id);
    }
}

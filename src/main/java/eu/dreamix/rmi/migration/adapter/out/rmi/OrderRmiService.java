
package eu.dreamix.rmi.migration.adapter.out.rmi;

import eu.dreamix.rmi.migration.domain.Order;
import java.rmi.Remote;
import java.rmi.RemoteException;

/** 
 * Legacy RMI interface to be strangled. 
 * <p>
 * This interface is being migrated to REST endpoints.
 * See migration register in docs/migration_map.adoc for details.
 */
public interface OrderRmiService extends Remote {
    
    /**
     * Find an order by its ID.
     * <p>
     * @deprecated This method is being replaced by REST endpoint GET /api/orders/{id}.
     * Use the REST API instead. Will be removed after 2025-08-07.
     * 
     * @param id The order ID
     * @return The order object
     * @throws RemoteException if there's an RMI communication error
     */
    @Deprecated(forRemoval = true, since = "2025-06")
    Order findOrderById(Integer id) throws RemoteException;
}

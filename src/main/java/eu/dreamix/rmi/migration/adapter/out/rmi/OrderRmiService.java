
package eu.dreamix.rmi.migration.adapter.out.rmi;

import eu.dreamix.rmi.migration.domain.Order;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OrderRmiService extends Remote {
    
    Order findOrderById(Integer id) throws RemoteException;
}


package eu.dreamix.rmi.migration.domain;

import java.math.BigDecimal;

public class Order {
    private Integer id;
    private String status;
    private BigDecimal total;

    public Order(Integer id, String status, BigDecimal total) {
        this.id = id;
        this.status = status;
        this.total = total;
    }

    public Integer getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
}

package model;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private Long id;

    private String customerName;

    public Customer() {
    }

    public Customer(Long id, String customerName) {
        this.id = id;
        this.customerName = customerName;
    }

}

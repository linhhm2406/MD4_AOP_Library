package service.customer;

import model.Customer;

public interface ICustomerService {
    Customer findById(Long id);
}

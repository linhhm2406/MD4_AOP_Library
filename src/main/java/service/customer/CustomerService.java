package service.customer;

import model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import repository.BookRepository;
import repository.CustomerRepository;

public class CustomerService implements ICustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer findById(Long id) {
        return customerRepository.findOne(id);
    }
}

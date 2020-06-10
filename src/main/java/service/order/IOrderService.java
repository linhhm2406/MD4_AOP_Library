package service.order;

import model.Order;
import java.util.List;

public interface IOrderService {
    List<Order> findAll();
    Order findById(Long id);
    void save(Order oder);
    void delete(Order order);
    Order findByOrderCode(int orderCode);
}

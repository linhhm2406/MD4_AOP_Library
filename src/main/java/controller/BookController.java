package controller;

import exception.NotFindOrderCodeException;
import exception.OutOfQuantityException;
import model.Book;
import model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import service.book.IBookService;
import service.customer.ICustomerService;
import service.order.IOrderService;

import java.util.List;

@Controller
@RequestMapping("/")
public class BookController {

    @Autowired
    IBookService bookService;

    @Autowired
    IOrderService orderService;

    @Autowired
    ICustomerService customerService;

    @GetMapping()
    public ModelAndView showAllBook(){
        List<Book> list = bookService.findAll();
        return new ModelAndView("book/bookList","bookList",list);
    }

    @GetMapping("/borrow/{id}")
    public ModelAndView showBorrowForm(@PathVariable Long id){
        return new ModelAndView("order/borrowForm", "book", bookService.findById(id));
    }

    @PostMapping("/borrow/{id}")
    public String borrow(@ModelAttribute Book book) throws OutOfQuantityException {
        decreaseQuantity(book);

        Order newOrder = makeNewOrderFollowBorrowBook(book);

        orderService.save(newOrder);

        return "redirect:/";
    }

    private Order makeNewOrderFollowBorrowBook(@ModelAttribute Book book) {
        Long newOrderId = (long) (orderService.findAll().size() + 1);
        int newOrderCode = ((int) (Math.random()*(99999 - 10000))) + 10000;
        return new Order(newOrderId,newOrderCode,customerService.findById((long) 1),book);
    }

    private void decreaseQuantity(@ModelAttribute Book book) throws OutOfQuantityException {
        Book borrowedBook = bookService.findById(book.getId());
        int quantity = borrowedBook.getQuantityInStock();
        if (quantity!=0) {
            borrowedBook.setQuantityInStock(quantity - 1);
        } else {
            throw new OutOfQuantityException();
        }
        bookService.save(borrowedBook);
    }

    @ExceptionHandler(OutOfQuantityException.class)
    public ModelAndView OutOfQuantityExceptionHandle(){
        return new ModelAndView("error/error","outOfQuantityMessage","Out of Stock Book, please select other book");
    }

    @GetMapping("/return")
    public ModelAndView showReturnForm(){
        return new ModelAndView("order/returnForm","order",new Order());
    }

    @PostMapping("return")
    public String returnBook(@ModelAttribute Order order) throws NotFindOrderCodeException {
        Order returnOrder = getOrderByOrderCode(order);

        increaseBookQuantity(returnOrder);

        orderService.delete(returnOrder);

        return "redirect:/";
    }

    private void increaseBookQuantity(Order returnOrder) {
        Book returnBook = returnOrder.getBook();
        returnBook.setQuantityInStock(returnBook.getQuantityInStock()+1);
        bookService.save(returnBook);
    }

    private Order getOrderByOrderCode(@ModelAttribute Order order) throws NotFindOrderCodeException {
        int returnOrderCode = order.getOrderCode();
        Order returnBook =orderService.findByOrderCode(returnOrderCode);
        if (returnBook == null){
            throw new NotFindOrderCodeException();
        }
        return returnBook;
    }

    @ExceptionHandler(NotFindOrderCodeException.class)
    public ModelAndView NotFindOrderCodeException(){
        return new ModelAndView("error/error","notFindOrderCodeException","Can not find Order Code");
    }
}

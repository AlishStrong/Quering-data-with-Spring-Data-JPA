package fi.tietoevry.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.tietoevry.backend.model.Order;
import fi.tietoevry.backend.repository.OrderRepository;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/reference/{id}")
    Order getOrderReferenceById(@PathVariable("id") Long orderId) {
        return orderRepository.getReferenceById(orderId);
    }

    @GetMapping("/{id}")
    Order findOrderById(@PathVariable("id") Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    @GetMapping("/sorted-desc")
    List<Order> getAllOrdersSortedDesc() {
        return orderRepository.findAll(Sort.by(Direction.DESC, "orderNumber"));
    }

    @GetMapping("/paged")
    Page<Order> getOrderPage(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") Integer perPage
    ) {
        return orderRepository.findAll(PageRequest.of(pageNumber, perPage));
    }

    @GetMapping("/status/{status}")
    List<Order> getOrdersByStatus(@PathVariable("status") String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Example: http://localhost:8080/orders/statuses/paged?statuses=disputed,shipped&pageNumber=1&perPage=20
     */
    @GetMapping("/statuses/paged")
    Page<Order> getOrdersByStatusesPaged(
      @RequestParam(name = "statuses", required = true) String[] statuses,
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
      @RequestParam(name = "perPage", required = false, defaultValue = "10") Integer perPage
    ) {
      return orderRepository.findByStatusIn(statuses, PageRequest.of(pageNumber, perPage));
    }

    /**
     * Example: http://localhost:8080/orders/statuses/customer/paged?statuses=disputed,shipped&customer=Enaco%20Distributors&pageNumber=1&perPage=20
     */
    @GetMapping("/statuses/customer/paged")
    Page<Order> getOrdersByCustomerNameAndStatusesPaged(
      @RequestParam(name = "customer", required = true) String customer,
      @RequestParam(name = "statuses", required = true) String[] statuses,
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
      @RequestParam(name = "perPage", required = false, defaultValue = "10") Integer perPage
    ) {
      return orderRepository.findByStatusInAndCustomerCustomerName(statuses, customer, PageRequest.of(pageNumber, perPage));
    }
}

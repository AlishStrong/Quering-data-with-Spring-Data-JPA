package fi.tietoevry.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.tietoevry.backend.model.Order;

@SpringBootTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void shouldGetOrderById() {
        Long id = 10100L;
        Order orderReference = orderRepository.getReferenceById(id);
        assertEquals(orderReference.getOrderNumber(), id);
    }

    @Test
    public void shouldGetAllOrders() {
        List<Order> orders = orderRepository.findAll();
        assertTrue(orders.size() > 0);
    }
}

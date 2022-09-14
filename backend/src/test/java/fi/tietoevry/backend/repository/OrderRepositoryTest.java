package fi.tietoevry.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.tietoevry.backend.model.Order;

@SpringBootTest
@Transactional
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

    @Test
    public void shouldGetDisputedOrders() {
        String statusFilter = "disputed";
        List<Order> disputedOrders = orderRepository.findByStatus(statusFilter);
        disputedOrders.forEach(order -> {
            assertEquals(statusFilter, order.getStatus().toLowerCase());
        });
    }

    @Test
    public void shouldGetDisputedOrShippedOrders() {
        String statusFilter = "disputed";
        List<Order> orders = orderRepository.findByStatusOrStatus(statusFilter, "shipped");
        orders.forEach(order -> {
            assertThat(order.getStatus().toLowerCase()).isIn("disputed", "shipped");
        });
    }

    @Test
    public void shouldFilterOrdersThroughRangeOfStatuses() {
        List<Order> orders = orderRepository.findByStatusIn("Disputed", "On Hold", "Cancelled");
        orders.forEach(order -> {
            assertThat(order.getStatus()).isIn("Disputed", "On Hold", "Cancelled");
        });
    }

    @Test
    public void shouldFilterOrdersByCustomerName() {
        List<Order> orders = orderRepository.findByCustomerCustomerName("Euro+ Shopping Channel");
        orders.forEach(order -> {
            assertEquals("Euro+ Shopping Channel", order.getCustomer().getCustomerName());
        });
    }

    @Test
    public void shouldFindByCustomerNameAndStatuses_one() {
        List<String> statuses = Arrays.asList("Disputed", "On Hold", "Cancelled");
        String customerName = "Euro+ Shopping Channel";
        List<Order> orders = orderRepository.findByStatusInAndCustomerCustomerName(statuses, customerName);
        orders.forEach(order -> {
            assertThat(order.getStatus()).isIn(statuses);
            assertEquals("Euro+ Shopping Channel", order.getCustomer().getCustomerName());
        });
    }

    @Test
    public void shouldFindByCustomerNameAndStatuses_two() {
        String customerName = "Euro+ Shopping Channel";
        List<Order> orders = orderRepository.findByCustomerCustomerNameAndStatusIn(customerName, "Disputed", "On Hold", "Cancelled");
        orders.forEach(order -> {
            assertThat(order.getStatus()).isIn("Disputed", "On Hold", "Cancelled");
            assertEquals("Euro+ Shopping Channel", order.getCustomer().getCustomerName());
        });
    }

    @Test
    public void shouldGetFirstOrderWithNeededStatus_using_First() {
        String status = "disputed";
        Order order = orderRepository.findFirstByStatus(status);
        assertEquals(status, order.getStatus().toLowerCase());
        assertEquals(10406L, order.getOrderNumber());
    }

    @Test
    public void shouldGetFirstOrderWithNeededStatus_using_Top() {
        String status = "disputed";
        Order order = orderRepository.findTopByStatus(status);
        assertEquals(status, order.getStatus().toLowerCase());
        assertEquals(10406L, order.getOrderNumber());
    }

    @Test
    public void shouldGetLastOrderWithNeededStatus() {
      String status = "disputed";
      Order order = orderRepository.findTopByStatusOrderByOrderNumberDesc(status);
      assertEquals(status, order.getStatus().toLowerCase());
      assertEquals(10417L, order.getOrderNumber());
    }

    @Test
    public void shouldGetFiveOrdersWithNeededStatus() {
      String status = "Cancelled";
      List<Order> allOrders = orderRepository.findByStatus(status);
      List<Order> fiveOrders = orderRepository.findTop5ByStatus(status);

      Stream.concat(allOrders.stream(), fiveOrders.stream()).forEach(order -> {
        assertEquals(status, order.getStatus());
      });

      assertThat(allOrders.size()).isGreaterThanOrEqualTo(5);
      assertThat(fiveOrders.size()).isLessThanOrEqualTo(5);
    }

    @Test
    public void shouldGetTenOrdersWithNeededStatus() {
      String status = "Shipped";
      List<Order> allOrders = orderRepository.findByStatus(status);
      List<Order> tenOrders = orderRepository.findFirst10ByStatus(status);

      Stream.concat(allOrders.stream(), tenOrders.stream()).forEach(order -> {
        assertEquals(status, order.getStatus());
      });

      assertThat(allOrders.size()).isGreaterThanOrEqualTo(10);
      assertThat(tenOrders.size()).isLessThanOrEqualTo(10);
    }
}

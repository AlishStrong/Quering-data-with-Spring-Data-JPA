package fi.tietoevry.backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fi.tietoevry.backend.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Gets all orders whose status is equal to the supplied value
     * 
     * @param status  status string value that you are interested
     * @return        List<Order> with statuses equal to the supplied value
     */
    List<Order> findByStatus(String status);

    /**
     * Gets all orders whose status is either equal to the value of statusOne or statusTwo
     * 
     * @param statusOne   first status value 
     * @param statusTwo   second status value
     * @return            List<Order> with statuses equal to either statusOne or statusTwo
     */
    List<Order> findByStatusOrStatus(String statusOne, String statusTwo);

    /**
     * Gets all orders whose status is either of the supplied status values
     * 
     * @param statuses   comma-delimited string values of statuses
     * @return           List<Order> with statuses found in the supplied comma-delimited string values
     */
    List<Order> findByStatusIn(String... statuses);

    /**
     * Finds and returns all orders, whose customer company name is equal to the supplied parameter
     * 
     * @param customerName  string value of the customer company name
     * @return              List<Order> related to the customer company
     */
    List<Order> findByCustomerCustomerName(String customerName);

    /**
     * Gets all orders, whose status value belongs to the supplied collection of statuses 
     * and whose customer company name is equal to the supplied customerName parameter
     * 
     * @param statuses      a collection of string values that represent statuses of your interest
     * @param customerName  string value of the customer company name
     * @return              List<Order> related to the customer company
     */
    List<Order> findByStatusInAndCustomerCustomerName(Collection<String> statuses, String customerName);

    /**
     * Gets all orders, whose customer company name is equal to the supplied customerName parameter 
     * and whose status is either of the supplied status values
     * 
     * @param customerName  string value of the customer company name
     * @param statuses      comma-delimited string values of statuses
     * @return              List<Order> related to the customer company and with statuses found in the supplied comma-delimited string values
     */
    List<Order> findByCustomerCustomerNameAndStatusIn(String customerName, String... statuses);

    /**
     * Gets orders in a Page whose status is either of the supplied status values
     * 
     * @param statuses   array of status values
     * @param page       Pageable object with information about the page number, elements per page and sorting
     * @return           Page<Order> with statuses found in the supplied comma-delimited string values
     */
    Page<Order> findByStatusIn(String[] statuses, Pageable page);

    /**
     * Gets orders in a Page whose status is either of the supplied status values
     * and whose customer company name is equal to the supplied customerName parameter 
     * 
     * @param statuses      array of status values
     * @param customerName  string value of the customer company name
     * @param page          Pageable object with information about the page number, elements per page and sorting
     * @return              Page<Order> with statuses found in the supplied comma-delimited string values
     */
    Page<Order> findByStatusInAndCustomerCustomerName(String[] statuses, String customerName, Pageable page);

    /**
     * Finds and returns the FIRST order record whose status equals to the supplied value
     * 
     * The default order is based on primary key (Order.orderNumber) in ascending order!
     * 
     * @param status  status value that order must have
     * @return        Order object of the found entity
     */
    Order findTopByStatus(String status);

    /**
     * Finds and returns the FIRST order record whose status equals to the supplied value
     * 
     * The default order is based on primary key (Order.orderNumber) in ascending order!
     * 
     * @param status  status value that order must have
     * @return        Order object of the found entity
     */
    Order findFirstByStatus(String status);

    /**
     * Finds and returns the LAST order record whose status equals to the supplied value
     * 
     * @param status  status value that order must have
     * @return        Order object of the found entity
     */
    Order findTopByStatusOrderByOrderNumberDesc(String status);

    /**
     * Finds and returns the first five order records whose status equals to the supplied value
     * 
     * The default order is based on primary key (Order.orderNumber) in ascending order!
     * 
     * @param status  status value that order must have
     * @return        List<Order> with maximum 5 Order object
     */
    List<Order> findTop5ByStatus(String status);

    /**
     * Finds and returns the first ten order records whose status equals to the supplied value
     * 
     * The default order is based on primary key (Order.orderNumber) in ascending order!
     * 
     * @param status  status value that order must have
     * @return        List<Order> with maximum 10 Order object
     */
    List<Order> findFirst10ByStatus(String status);
}

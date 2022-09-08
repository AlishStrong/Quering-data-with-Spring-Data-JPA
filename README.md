# Part 1: Using JPA repositories

**Spring Data JPA** significantly simplifies the data source interaction work for developers.
It provides `org.springframework.data.jpa.repository.JpaRepository`-interface, that has useful methods, like `findAll()`, and extends `PagingAndSortingRepository`, which itself extends `CrudRepository`.
To benefit from the repository, developer must create an **entity repository interface** that will **extend** `JpaRepository`, generics of which need to know the **entity class name** and **primary key type**. There is no need for any further configuration; repositories that extend JpaRepository will be automatically detected by Spring Data JPA and added to the context, so that they could be injected and used where needed 😀 

### *NB! in this session we will address only how to read (obtain) data from the data source*

## 1: Repository

1) Create a new package in the `backend` project called `repository`.
2) Inside the package create an interface `OrderRepository`:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

}
```
- `JpaRepository` is `org.springframework.data.jpa.repository.JpaRepository`;
- `Order` is `fi.tietoevry.backend.model.Order`;
- `Long` is used because `@Id` field of `Order`-entity class is of type `Long`;

And that's it! Your simple `JpaRepository` for `Order`-entity is ready to be injected and used. You can either create a `Controller`-class or write tests to interact with your repository.

## 2: Controller
1) Create a new package `controller`
2) Inside the package create a class `OrderController`:
```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;
}
```
- `@RestController` is `org.springframework.web.bind.annotation.RestController`; 
- `@RequestMapping` is `org.springframework.web.bind.annotation.RequestMapping`. The value **/orders** sets the root for all requests that will be handled by this controller specifically;
- `OrderRepository` is the newly-created `fi.tietoevry.backend.repository.OrderRepository`;
- `@Autowired` is `org.springframework.beans.factory.annotation.Autowired` and is needed to inject our `OrderRepository` for use in `OrderController`;

### 2.1: Getting all records 
`JpaRepository` has a simple method for retreiving all data at once - `findAll()`.
Add the following method to your `OrderController`:
```java
@GetMapping
List<Order> getAllOrders() {
    return orderRepository.findAll();
}
```
- `@GetMapping` is `org.springframework.web.bind.annotation.GetMapping`. Since no value was supplied, all **HTTP GET** requests to **/orders** will be handled by this mapping-method;
- `List` is `java.util.List`;
- `Order` is `fi.tietoevry.backend.model.Order`;

Now, all **HTTP GET** requests to **/orders** will return a list of all order records stored in the `orders`-table.

### 2.2: Getting a record by ID
For this purpose either `JpaRepository.getReferenceById(id)` or `CrudRepository.findById(id)` can be used:

#### 2.2.1 getReferenceById(id)
```java
@GetMapping("/reference/{id}")
Order getOrderReferenceById(@PathVariable("id") Long orderId) {
    return orderRepository.getReferenceById(orderId);
}
```
`getReferenceById(id)` will return a **proxy reference** to the found order record! To serialize the reference, we need to be either unproxy it using `org.hibernate.Hibernate.unproxy(T proxy, Class<T> entityClass)`:
```java
@GetMapping("/reference/{id}")
Order getOrderReferenceById(@PathVariable("id") Long orderId) {
    return Hibernate.unproxy(orderRepository.getReferenceById(orderId), Order.class);
}
```
**OR** 

`Order`-entity class can get an additional jackson annotation `com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})`:
```java
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
  ...
}
```
This approach was used in the project.

Another note is that the newly-created `getOrderReferenceById(@PathVariable("id") Long orderId)` method will listen to **HTTP GET /orders/reference/{id}** requests, e.g. **/orders/reference/10100**.

#### 2.2.2 findById(id)
`findById(id)` will return an `java.util.Optional`:
```java
@GetMapping("/{id}")
Order findOrderById(@PathVariable("id") Long orderId) {
    return orderRepository.findById(orderId).get();
}
```

### 2.1: Getting all records sorted
The query response can be returned sorted. For that developers can can use `findAll(Sort sort)` method.
`Sort`-instance through different `by()` methods of `org.springframework.data.domain.Sort`. Let's get all orders sorted in a reverse order by their primary keys - **orderNumber**:
```java
@GetMapping("/sorted-desc")
List<Order> getAllOrdersSortedDesc() {
    return orderRepository.findAll(Sort.by(Direction.DESC, "orderNumber"));
}
```
Where `Direction` comes from `org.springframework.data.domain.Sort.Direction`;

**Experiment with other `by()` methods and different fields! 😉**

### 2.2: Getting records in pages
Getting all records can be often inpractical. Instead it would be more convenient to obtain only part of all records and get the next part if needed. For such reason `org.springframework.data.domain.Page` interface exists.
As it was mentioned earlier, `org.springframework.data.jpa.repository.JpaRepository` extends `PagingAndSortingRepository` that gives the conveniens of getting paged data through `findAll(Pageable pageable)` method. Here `Pageable` is an interface, that can be implemented by `org.springframework.data.domain.PageRequest` (through `AbstractPageRequest`). `org.springframework.data.domain.PageRequest` has several useful static `of()` methods to create an instance of it. 
Let's use one of them that specifies what particular page we want to retrieve and how many records a page should contain:
```java
@GetMapping("/paged")
Page<Order> getOrderPage(
        @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") Integer perPage
) {
    return orderRepository.findAll(PageRequest.of(pageNumber, perPage));
}
```
- `Page` is `org.springframework.data.domain.Page`;
- `@RequestParam` is `org.springframework.web.bind.annotation.RequestParam` that is needed to specify **query paramameters**. If you make a request */orders/paged?pageNumber=3&perPage=20*, then the app will be instructed to get you the 4th page and it should contain maximum 20 records. If the **query/request parameters** are not given, then they will be defaulted to 0 and 10 in this example. 

**Data in pages can also be sorted! Experiment with other `PageRequest.of()` methods that also specify sorting! 😉**

## 3: Test
For interaction via tests, create `repository` package, but instead of *src/main/java/* go to *src/test/java*.
Then create there `OrderRepositoryTest`:
```java
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
```  
- `@SpringBootTest` is `org.springframework.boot.test.context.SpringBootTest`;
- `@Test` is `org.junit.jupiter.api.Test`;
- assertion methods are obtained from `org.junit.jupiter.api.Assertions`;

## Checkout branch **part-1-complete** to see the completed files for `Order`-entity
## Try practicing with other entities, like `Customer`, `Employees`, `Products`

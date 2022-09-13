# Part 2: Query DSL, aka Derived queries.

**Query DSL** or **Derived queries** significantly simplify the process of getting and interacting with data. Using a combination of special **subject** and **predicate** keywords in methods declarations, developers can perform complex queries without getting concerned about the implementation; `Spring Data JPA` will do that automatically.

## Finding by ONE parameter value

For instance, let's try to get all `Order` records, by a specific value of its `status` field, for instance **Disputed**.
The corresponding SQL query will be the following:
```SQL
SELECT * FROM orders WHERE status = 'Disputed'
```

To make a similar query with `Spring Data JPA` open the `OrderRepository` and declare there a new method:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status); 
}
```
And that's it 😀 The combination of **subject** keyword `findBy` and `Status` (**predicate** field of `Order`), plus the parameter submitted to the method will tell `Spring Data JPA` to make an implementation of the method that will correspond to the SQL query you saw before.

You can then either add a new mapping that will use this method in `OrderController` or write a new test:

### Controller
```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    ...

    @GetMapping("/status/{status}")
    List<Order> getOrdersByStatus(@PathVariable("status") String status) {
        return orderRepository.findByStatus(status);
    }
}
```
Requests to **/orders/status/Disputed** or **/orders/status/disputed** will return a list of `Order` records that have `status` equal to value **Disputed**.

### Test
```java
@SpringBootTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    ...

    @Test
    public void shouldGetDisputedOrders() {
        String statusFilter = "disputed";
        List<Order> disputedOrders = orderRepository.findByStatus(statusFilter);
        disputedOrders.forEach(order -> {
            assertEquals(statusFilter, order.getStatus().toLowerCase());
        });
    }
}
```

## Finding by MORE THAN ONE parameter value

We were able to filter finding `Order` records by a given `status`. But what if we need filter not by one status value; we could be interested in orders that were either **Shipped** or **Disputed**.

For such logic a SQL query would look like this:
```SQL
SELECT * FROM orders WHERE status = 'Disputed' OR status = 'Shipped';
```

`Spring Data JPA Derived queries` includes a keyword for that - `OR`:
 ```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    ...

    List<Order> findByStatusOrStatus(String statusOne, String statusTwo);
}
```

Of course if the number of `status` values for filtering `Order` records would increase further, then using `OR` for each value would not be efficient. **SQL** has `IN` for a range filtering:

```SQL
SELECT * FROM orders WHERE status IN ('Disputed', 'Shipped');
```
Likewise, there is such keyword in `Spring Data JPA Derived queries`:
 ```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    ...

    List<Order> findByStatusIn(String... statuses);
}
```
Important note about the type of the supplied argument with a range of values:
- it can be an `array` (or **varargs** as used in the example - `String...`);
- or it can be an `Iterable<String>` - `List<Order> findByStatusIn(Iterable<String> statuses)`;

## Parameter from a RELATED ENTITY (table)

Table `orders` has a relation to `customers` table through **foreign key** `orders_ibfk_1` - `orders.customerNumber` is `customers.customerNumber`.
It would be useful to obtain `order` records that are linked to a specific `customer`, and though we can filter by `orders.customerNumber`, it would be easier if we could supply the `customers.customerName` as a filtering parameter. `Derived queries` support that!

First of all, make sure that you have specified relations between your entities! This has been done already in the models of the project:
```java
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
    
    ...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerNumber")
    @JsonIgnore
    private Customer customer;
}
```

Then, we can chain the property of the related entity:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    ...

    List<Order> findByCustomerCustomerName(String customerName);
}
```
So, `Order`-entity has a field `customer`, which is actually a joined `Customer`-entity. That entity has field `customerName`. That is why, the chain of properties in the declaration went like this: `findBy` + `Customer` (i.e. `Order.customer`) + `CustomerName` (i.e. `Customer.customerName`)!

If you are interested in how a respective SQL query would look like, then here it is:
```SQL
SELECT orders.orderNumber, orders.orderDate, orders.requiredDate, orders.shippedDate, orders.status, orders.comments, orders.customerNumber FROM orders
JOIN customers ON (customers.customerNumber = orders.customerNumber)
WHERE customers.customerName = 'Euro+ Shopping Channel';
```

A good example can be a derived query that filters by both `status` and `customerName`:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    ...

    List<Order> findByStatusInAndCustomerCustomerName(Collection<String> statuses, String customerName);
}
```
Pay attention that in this example **varargs** were not used, so that parameter `customerName` would actually be treated as part of them. Also, you can use `String[]` instead of a `Collection`. However, if you still wish to use  **varargs**, then modify your method declaration like this:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    ...

    List<Order> findByCustomerCustomerNameAndStatusIn(String customerName, String... statuses);
}
```

Notably, the condition for filtering both customer name and statuses is ensured by `And` keyword!

## PAGING in Derived queries

From the previous part, it was shown how to paginate the result; well, `Derived queries` support that as well. To paginate your results, you need specify in the methods declaration that it returns a `Page` and supply a `Pageable` argument:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    ...
    
    Page<Order> findByStatusIn(String[] statuses, Pageable page);
    Page<Order> findByStatusInAndCustomerCustomerName(Collection<String> statuses, String customerName, Pageable page);
}
```
- `Page` and `Pageable` are from `org.springframework.data.domain` package;

Pay attention that naming of the methods does not need to be changed.

## Other keywords

### Subject keywords

`findBy` is not the only **subject** keyword in `Derived queries`. According to the [documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject) of `Spring Data JPA` there are the following **subjects**:
 1. `find…By`, `read…By`, `get…By`, `query…By`, `search…By`, `stream…By` - for READing data, where `read…By` and `stream…By` will return `Stream` of your entity result.
 2. `exists…By` - checks if records satisfying your requirements actually exist; returns `bolean`.
 3. `count…By` - equivalent of `COUNT()` in **SQL** and returns `long`.
 4. `delete…By`, `remove…By` - to perform a DELETE operation. Normally, they return `void`, but `delete…By` can return `long` - number of rows deleted.

Pay attention to `…` - those dots mean that you can either:
- skip them completely, like we used `findByStatus`;
- use filler words for better meaning but no actual effect on logic - `findOrdersBy`;
- use other special **subject** keywords `First`, `Top`, and `Distinct` (more detailed info about them below);

`…First<number>…` and `…Top<number>…` - according to the [documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject): **limit the query results to the first <number> of results. This keyword can occur in any place of the subject between `find` (and the other keywords) and `by`**.

If you use these **subject** keywords without a number, like `findTopBy` or `findFirstBy` then the result type would be the `Entity`-class of your `Repository`. However, if you provide `number`, then the result will be an `Iterable` (remember that `List` inherits from `Iterable`):
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findTopByStatus(String status);
    Order findFirstByStatus(String status);

    List<Order> findTop5ByStatus(String status);
    List<Order> findFirst10ByStatus(String status);

}
```

`…Distinct…` - equivalent of `DISTINCT` in **SQL**; [documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject) tells the following: **Use a distinct query to return only unique results. Consult the store-specific documentation whether that feature is supported. This keyword can occur in any place of the subject between `find` (and the other keywords) and `by`**.

### Streaming results

As it was mentioned before, `read…By` and `stream…By` will return `Stream`. However, there is another way to get `Stream` - you can append your method declaration with `AndStream` to get the streamed result:
```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    Stream<Order> findByStatusAndStream(String status);

}
```
[Documentation about streaming](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-streaming)

### Predicate keywords

In this part we got introduced to `In`, `And`, and `Or`, but there are more keywords: `LessThan`, `Containing`, `Between`, etc.

Mentioning them all here would be a tedious work 😅

Instead I will provide you with links to the documentation pages 😬:
1. **Table 3** with sample methods and respective **JPQL** snippets - [link to that section](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation);
2. **Table 9** and **Table 10** about keywords' logical implication and additional desctiption - [link to that section](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation);


### **And that is all, folks, for the second part of this session! 😀**
### **As usual, you can 👇**
## checkout branch **part-2-complete** to see the completed work

**And you are highly encouraged to experiment with other entities and keywords using `Derived queries`!**

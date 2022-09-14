# Part 3: @Query annotation.

`Derived queries` are useful but sometimes it is easier for the developers to write a **SQL** query and have it executed. Such scenario is possible using `@Query` annotation atop method declarations. This annotation allows developers to either write **JPQL** or **native SQL** queries straight away; naming of the methods will have no effect on the query execution, as in that case `Spring Data JPA` will look at the query that developer gave.

## JPQL queries

`JPQL` stands for `Java Persistence Query language` it is very similar to **SQL** with the main difference that instead of using **table** names in the query, developers must use **class** names of the entities. For instance:
- a **SQL** query to get all records from `orders` table will look like this:
```SQL
SELECT * FROM orders;
```
- **JPQL** will have the following form:
```java
SELECT o FROM Order o
```
where `Order` is `Entity`-class:
```java
@Entity
@Table(name = "orders")
public class Order {

  ...

}
```

`JPQL` itself is a big topic and is per se not the core part of our session. That is why we will not concentrate deeply on it. But if you are interested to study it more, then here is a good resource link - [JPA - JPQL by TutorialsPoint](https://www.tutorialspoint.com/jpa/jpa_jpql.htm)

## JPQL query examples

First thing first, let's switch from the `Order` entity to another one - for example `Customer` entity.

Inside the `repository` package create a new interface `CustomerRepository`: 
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
```

### Simple query to get all data

Let's create our first method that will return the list of all customers within the `customers` table. We will use `@Query` annotation atop of the method and we will supply to it the following value `SELECT c FROM Customer c`. Naming of the method will not be important, so let's name it `retreiveAllCustomersJPQL`:
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT c FROM Customer c")
    List<Customer> retreiveAllCustomersJPQL();
}
```
 You can create a controller or write a test to verify that the method works and indeed returns all `Customer` records.
 
```java
@RestController
@RequestMapping("/customers")
public class CustomerController {
    
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    List<Customer> getAllCustomers() {
        return customerRepository.retreiveAllCustomersJPQL();
    }
}
``` 

If you try out the simple query that we have made, you will see that there are over a hundred `Customer` records in the database. Again, it would have been useful to page our results. For this to happen we need to create a new method with `Page` return type and with `Pageable` parameter. 

However, we don't need to modify the value within the `@Query` annotation:
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    ...

    @Query(value = "SELECT c FROM Customer c")
    Page<Customer> retreiveAllCustomersPagedJPQL(Pageable page);
}
```
Let's add a new `GetMapping` controller method in our `CustomerController` class:

```java
@RestController
@RequestMapping("/customers")
public class CustomerController {
    
    @Autowired
    private CustomerRepository customerRepository;

    ...

    @GetMapping("/paged")
    Page<Customer> getCustomersPaged(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") Integer perPage
    ) {
        return customerRepository.retreiveAllCustomersPagedJPQL(PageRequest.of(pageNumber, perPage));
    }
}
``` 
Now to obtain `customers` table data in pages, users need to hit **/customers/paged** endpoint. In addition they can provide `pageNumber` and/or `perPage` URL query parameters like this: **/customers/paged?pageNumber=2&perPage=20**. However, that is not necessary; our controller method is set to default those parameters.

### Complex queries with filters

In the previous part, we saw how to retreive entity records filtered by their properties or properties of their related entities. Let's repeat that using `JPQL` queries for our `Customer` entity.

`Customer` entity has a property of `country`. So let's create a method that would get us records filtered by that property's value:
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    ...

    @Query(value = "SELECT c FROM Customer c WHERE c.country = ?1")
    Page<Customer> customersFromCountryPagedJPQL(String country, Pageable page);
}
```
Let's keep the results paged 😉 We can check wether our newly method works via a test this time. Create a new `CustomerRepositoryTest` class within the `fi.tietoevry.backend.repository` package of **test** directory:
```java
@SpringBootTest
@Transactional
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @ParameterizedTest
    @ValueSource(strings = { "USA", "France", "Finland" })
    public void shouldGetCustomersFromSpecificCountry(String country) {
        Page<Customer> customersPage = customerRepository.customersFromCountryPagedJPQL(country, PageRequest.of(0, 10));
        if (customersPage.hasContent()) {
            customersPage.get().forEach(customer -> {
                assertEquals(country, customer.getCountry());
            });
        }
    }
}
```
Here I opted for parameterized tests using `org.junit.jupiter.params.ParameterizedTest` and `org.junit.jupiter.params.provider.ValueSource`. Also, since the method returns a `Page` and it can be that there are no `Customer` records with the specified `country`, we check if the returned `Page` actually has any records, before making test verifications.


Pay attention that in the `JPQL` query we supply the `country` parameter via `?` followed by its order in method's parameters - in this case it is `1`, as the `country` method parameter comes first!

However, you can benefit from `named` parameters within your `@Query` values. 

`Customer` entity has a relation to the `Employee` entity. Let's create a new `@Query` method with `named` parameters, where we will try to find `Customer` records based on their `country` and `Employee` name, i.e. `Employee.firstName` and `Employee.lastName`:
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    ...

    @Query(value = "SELECT c FROM Customer c "
            + "JOIN c.salesRepEmployeeNumber e "
            + "WHERE c.country = :countryName "
            + "AND e.firstName = :firstName "
            + "AND e.lastName = :lastName")
    Page<Customer> customersFromCountryOfEmployeeNamedJPQL(
            @Param("countryName") String country,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable page);
}
```
That's a lot to handle, isn't it 😅 But let's explain the tricky parts:
1. We used `org.springframework.data.repository.query.Param` annotation for our method's parameters to be able to use custom parameter names within the `@Query` value, instead of `?<number>` format. Pay attention that our method has a parameter `country`, but within `@Param` we named it `countryName` instead - that name will be used inside the query, i.e. `... WHERE c.country = :countryName ...` part!
2. In order to filter customers by the name of employees we need to **join** `Customer` and `Employee` entities. The binding property in our definitions is `Customer.salesRepEmployeeNumber`. That is why the **join** is done like this: `... JOIN c.salesRepEmployeeNumber e ...`, where `e` is an **alias** for the joined `Employee` entity.

And we can test that everything works:
```java
@SpringBootTest
@Transactional
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    ...

    @Test
    public void shouldGetCustomerByCountryAndEmployeeName() {
        String country = "UK";
        String firstName = "Larry";
        String lastName = "Bott";

        Page<Customer> customersPage = customerRepository.customersFromCountryOfEmployeeNamedJPQL(country, firstName, lastName, PageRequest.of(0, 10));
        if (customersPage.hasContent()) {
            customersPage.get().forEach(customer -> {
                assertEquals(country, customer.getCountry());
                assertEquals(firstName, customer.getSalesRepEmployeeNumber().getFirstName());
                assertEquals(lastName, customer.getSalesRepEmployeeNumber().getLastName());
            });
        }
    }
}
```

## Native SQL query examples
The last example about `JQPL` queries was a bit complex in its syntax. The corresponding **SQL** query of it (with values from the test) would look like this:
```SQL
SELECT c.* FROM customers c 
JOIN employees e ON (e.employeeNumber = c.salesRepEmployeeNumber)
WHERE c.country = 'UK' AND e.firstName = 'Larry' AND e.lastName = 'Bott'
```

For some developers such syntax is way simplier to construct, especially if they are not familiar with `JPQL`. So, it would have been really nice if we could just write a **SQL** query, test that it works in a DBMS of our choice and then copy-paste it for the use in our Spring application. 😎

Well, that's where `nativeQuery` of `@Query` comes to resque! 
By default, this property is **false**, but you can set it to **true**, and then it will be expected that the query string inside the `value` of `@Query` is actually a native **SQL** query string. 

So, our `nativeQuery` will look like this:
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    ...

    @Query(value = "SELECT c.* FROM customers c "
            + "JOIN employees e ON (e.employeeNumber = c.salesRepEmployeeNumber)"
            + "WHERE c.country = :countryName "
            + "AND e.firstName = :firstName "
            + "AND e.lastName = :lastName",
            nativeQuery = true)
    Page<Customer> customersFromCountryOfEmployeeNamedNative(
            @Param("countryName") String country,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable page);
}
```

And the respective test will be:
```java
@SpringBootTest
@Transactional
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    ...

    @Test
    public void shouldGetCustomerByCountryAndEmployeeName_viaNativeQuery() {
        String country = "UK";
        String firstName = "Larry";
        String lastName = "Bott";

        Page<Customer> customersPage = customerRepository.customersFromCountryOfEmployeeNamedNative(country, firstName, lastName, PageRequest.of(0, 10));
        if (customersPage.hasContent()) {
            customersPage.get().forEach(customer -> {
                assertEquals(country, customer.getCountry());
                assertEquals(firstName, customer.getSalesRepEmployeeNumber().getFirstName());
                assertEquals(lastName, customer.getSalesRepEmployeeNumber().getLastName());
            });
        }
    }
}
```

## And that's it for PART 3 and for the whole session in general. 

### I hope you enjoyed it and found something new for yourself 😃
## As usual, checkout branch **part-3-complete** to see the completed work

**And you are highly encouraged to experiment with other entities and keywords using `@Query` annotation!**

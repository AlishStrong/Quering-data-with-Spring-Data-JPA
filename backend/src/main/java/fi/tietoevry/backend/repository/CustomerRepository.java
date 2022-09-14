package fi.tietoevry.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fi.tietoevry.backend.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT c FROM Customer c")
    List<Customer> retreiveAllCustomersJPQL();

    @Query(value = "SELECT c FROM Customer c")
    Page<Customer> retreiveAllCustomersPagedJPQL(Pageable page);

    @Query(value = "SELECT c FROM Customer c WHERE c.country = ?1")
    Page<Customer> customersFromCountryPagedJPQL(String country, Pageable page);

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

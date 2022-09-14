package fi.tietoevry.backend.repository;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import fi.tietoevry.backend.model.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

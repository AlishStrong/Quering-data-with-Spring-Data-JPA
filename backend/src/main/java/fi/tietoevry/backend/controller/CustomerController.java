package fi.tietoevry.backend.controller;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.tietoevry.backend.model.Customer;
import fi.tietoevry.backend.repository.CustomerRepository;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/paged")
    Page<Customer> getCustomersPaged(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") Integer perPage
    ) {
        return customerRepository.retreiveAllCustomersPagedJPQL(PageRequest.of(pageNumber, perPage));
    }
}

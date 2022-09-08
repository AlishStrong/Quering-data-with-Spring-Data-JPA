package fi.tietoevry.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.tietoevry.backend.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}

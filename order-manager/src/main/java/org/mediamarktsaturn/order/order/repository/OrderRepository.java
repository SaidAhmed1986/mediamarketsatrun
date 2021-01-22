package org.mediamarktsaturn.order.order.repository;

import org.mediamarktsaturn.order.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

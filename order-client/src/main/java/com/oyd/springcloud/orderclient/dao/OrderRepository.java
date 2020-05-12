package com.oyd.springcloud.orderclient.dao;

import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.orderclient.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 *
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOneByTitle(String title);

    @Override
    Order getOne(Long aLong);

    List<Order> findOneByCustomerId(Long customerId);
    List<Order> findAllByStatusAndCreateDateBefore(String status, ZonedDateTime checkTime);

    Order findOneByUuid(String uuid);
}

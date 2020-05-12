package com.oyd.springcloud.userclient.dao;

import com.oyd.springcloud.userclient.domain.Customer;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 *
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findOneByUsername(String username);

    @Override
    Customer getOne(Long customerId);

    @Modifying
    @Query("UPDATE customer set deposit = deposit-?2 where id=?1")
    int charge(Long customerId,int amount);
}

package com.oyd.springcloud.userclient.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.userclient.dao.CustomerRepository;
import com.oyd.springcloud.userclient.domain.Customer;
import com.oyd.springcloud.userclient.feign.OrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerResource {

    @PostConstruct
    public void init() {
        long count = customerRepository.count();
        if (count > 0) {
            return;
        }
        Customer customer = new Customer();
        customer.setUsername("oyd");
        customer.setPassword("111111");
        customer.setRole("User");
        customerRepository.save(customer);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderClient orderClient;

    @PostMapping("")
    public Customer create(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @GetMapping("")
    @HystrixCommand
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @GetMapping("/my")
    @HystrixCommand
    public Map getMyInfo() {
        Customer customer = customerRepository.findOneByUsername("oyd");
        OrderDTO order = orderClient.getMyOrder(1l);
        Map result = new HashMap();
        result.put("customer", customer);
        result.put("order", order);
        return result;
    }

}

package com.oyd.springcloud.orderclient.web;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.oyd.commom.IOrderService;
import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.orderclient.dao.OrderRepository;
import com.oyd.springcloud.orderclient.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/api/order")
public class OrderResource implements IOrderService {

//    @PostConstruct
//    public void init() {
//        long count = orderRepository.count();
//        if (count > 0) {
//            return;
//        }
//        Order order = new Order();
//        order.setAmount(100);
//        order.setTitle("MyOrder");
////        order.setDetail("Bought a book");
//        orderRepository.save(order);
//    }

    @Autowired
    private OrderRepository orderRepository;
    private TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    private JmsTemplate jmsTemplate;

    @RequestMapping("")
    public void create(@RequestBody OrderDTO order) {
        order.setUuid(uuidGenerator.generate().toString());
        jmsTemplate.convertAndSend("order:new",order);
    }


    @GetMapping("/{id}")
    public OrderDTO getMyOrder(@PathVariable Long id) {
        Order order = orderRepository.getOne(id);
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setAmount(order.getAmount());
        dto.setTitle(order.getTitle());
//        dto.setDetail(order.getDetail());
        return dto;
    }

//    @GetMapping("")
//    public List<Order> getAll() {
//        return orderRepository.findAll();
//    }

//    @GetMapping("/{id}")
//    public String getMyOrder(@PathVariable Long id){
//        Order order = orderRepository.getOne(id);
//        return order.getTitle();
//    }
}

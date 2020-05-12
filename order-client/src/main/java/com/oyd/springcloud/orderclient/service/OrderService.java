package com.oyd.springcloud.orderclient.service;

import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.orderclient.dao.OrderRepository;
import com.oyd.springcloud.orderclient.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    private final static Logger LOG = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = "order:locked",containerFactory = "msgFactory")
    public void handleOrderNew(OrderDTO dto){
        LOG.info("Got new order to create:{}",dto);
        if(orderRepository.findOneByUuid(dto.getUuid()) != null){
            LOG.info("Msg already processed:{}",dto);
        }else{
            Order order = createOrder(dto);
            orderRepository.save(order);
            dto.setId(order.getId());
        }
        dto.setStatus("NEW");
        jmsTemplate.convertAndSend("order:pay",dto);
    }

    @Transactional
    @JmsListener(destination = "order:finish",containerFactory = "msgFactory")
    public void handleOrderFinish(OrderDTO dto){
        LOG.info("Got order for finish:{}",dto);
        Order order = orderRepository.getOne(dto.getId());
        order.setStatus("FINISH");
        orderRepository.save(order);
    }

    @Transactional
    @JmsListener(destination = "order:fail",containerFactory = "msgFactory")
    public void handleOrderFail(OrderDTO dto){
        LOG.info("Got order for fail:{}",dto);
        Order order;
        if(dto.getId() == null){
            order = createOrder(dto);
            order.setReason("TICKET_LOCK_FAIL");
        }else{
            order = orderRepository.getOne(dto.getId());
            if(dto.getStatus().equals("NOT_ENOUGH_DEPOSIT")){
                order.setReason("NOT_ENOUGH_DEPOSIT");
            } else if(dto.getStatus().equals("TIMEOUT")){
                order.setReason("TIMEOUT");
            }
        }
        order.setStatus("FAIL");
        orderRepository.save(order);
    }

    private Order createOrder(OrderDTO dto){
        Order order = new Order();
        order.setUuid(dto.getUuid());
        order.setAmount(dto.getAmount());
        order.setTitle(dto.getTitle());
        order.setCustomerId(dto.getCustomerId());
        order.setTicketNum(dto.getTicketNum());
        order.setStatus("NEW");
        order.setCreateDate(ZonedDateTime.now());
        return order;
    }

    @Scheduled(fixedDelay = 10000L)
    public void checkTimeoutOrders(){
        ZonedDateTime checkTime = ZonedDateTime.now().minusMinutes(1L);
        List<Order> orders = orderRepository.findAllByStatusAndCreateDateBefore("NEW", checkTime);
        orders.forEach(order -> {
            LOG.error("Order timeout:{}",order);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setTicketNum(order.getTicketNum());
            orderDTO.setUuid(order.getUuid());
            orderDTO.setAmount(order.getAmount());
            orderDTO.setTitle(order.getTitle());
            orderDTO.setCustomerId(order.getCustomerId());
            orderDTO.setStatus("TIMEOUT");
            jmsTemplate.convertAndSend("order:fail",orderDTO);
        });
    }
}

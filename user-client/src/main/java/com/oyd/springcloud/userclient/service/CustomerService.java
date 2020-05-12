package com.oyd.springcloud.userclient.service;

import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.userclient.dao.CustomerRepository;
import com.oyd.springcloud.userclient.dao.PayInfoRepository;
import com.oyd.springcloud.userclient.domain.Customer;
import com.oyd.springcloud.userclient.domain.PayInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {
    private final static Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PayInfoRepository payInfoRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = "order:pay",containerFactory = "msgFactory")
    public void handleOrderPay(OrderDTO dto) {
        LOG.info("Got new order for pay:{}", dto);
        PayInfo payInfo = payInfoRepository.findOneByOrderId(dto.getId());
        Customer customer = customerRepository.getOne(dto.getCustomerId());
        if (payInfo != null) {
            LOG.warn("Order already paid:{}", dto);
            return;
        } else {
            if (customer.getDeposit() < dto.getAmount()) {
                LOG.warn("Not enough deposit");
                dto.setStatus("NOT_ENOUGH_DEPOSIT");
                jmsTemplate.convertAndSend("order:ticket_error", dto);
                return;
            }
            payInfo = new PayInfo();
            payInfo.setOrderId(dto.getId());
            payInfo.setAmount(dto.getAmount());
            payInfo.setStatus("PAID");
            payInfoRepository.save(payInfo);
            //        customer.setDeposit(customer.getDeposit()-dto.getAmount());
            customerRepository.charge(customer.getId(), dto.getAmount());
        }
        payInfo.setStatus("PAID");
        jmsTemplate.convertAndSend("order:ticket_move", dto);
    }
}

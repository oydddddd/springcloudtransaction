package com.oyd.springcloud.ticketclient.service;

import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.ticketclient.dao.TicketRepository;
import com.oyd.springcloud.ticketclient.domain.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final static Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = "order:new",containerFactory = "msgFactory")
    public void handleTicketLock(OrderDTO dto){
        LOG.info("Got new order for ticket lock:{}",dto);
        int lockCount = ticketRepository.lockTicket(dto.getCustomerId(),dto.getTicketNum());
        if(lockCount == 1){
            dto.setStatus("TICKET_LOCKED");
            jmsTemplate.convertAndSend("order:locked",dto);
        }else{
            dto.setStatus("TICKET_LOCKED_FAIL");
            jmsTemplate.convertAndSend("order:fail",dto);
        }
    }

    @Transactional
    @JmsListener(destination = "order:ticket_move",containerFactory = "msgFactory")
    public void handleTicketMove(OrderDTO dto){
        LOG.info("Got new order for ticket move:{}",dto);
        int count = ticketRepository.moveTicket(dto.getCustomerId(),dto.getTicketNum());
        if(count == 0){
            LOG.warn("Ticket already moved:{}",dto);
        }
        dto.setStatus("TICKET_MOVED");
        jmsTemplate.convertAndSend("order:finish",dto);
    }

    @Transactional
    @JmsListener(destination = "order:ticket_unlock",containerFactory = "msgFactory")
    public void handleTicketUnlock(OrderDTO dto){
        int count = ticketRepository.unLockTicket(dto.getCustomerId(), dto.getTicketNum());
        if(count == 0){
            LOG.info("Ticket already unlock:{}",dto);
        }
//        dto.setStatus("TICKET_UNLOCKED");
        jmsTemplate.convertAndSend("order:fail",dto);
    }

    @Transactional
    @JmsListener(destination = "order:ticket_error",containerFactory = "msgFactory")
    public void handleTicketError(OrderDTO dto){
        int count = ticketRepository.unLockTicket(dto.getCustomerId(), dto.getTicketNum());
        if(count == 0){
            LOG.info("Ticket already unlock:{}",dto);
        }
        count = ticketRepository.unMoveTicket(dto.getCustomerId(), dto.getTicketNum());
        if(count == 0){
            LOG.info("Ticket already unmoved,or not moved:{}",dto);
        }
    }

    @Transactional
    public Ticket ticketLock(OrderDTO dto){
        Ticket ticket = ticketRepository.findByTicketNum(dto.getTicketNum());
        ticket.setLockUser(dto.getCustomerId());
        ticket = ticketRepository.save(ticket);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ticket;
    }

    @Transactional
    public int ticketLock2(OrderDTO dto){
        int lockCount = ticketRepository.lockTicket(dto.getCustomerId(),dto.getTicketNum());
        LOG.info("update ticket lock count:{}",dto);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lockCount;
    }
}

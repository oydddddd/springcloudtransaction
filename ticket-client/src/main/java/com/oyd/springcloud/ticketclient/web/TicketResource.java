package com.oyd.springcloud.ticketclient.web;

import com.oyd.commom.dto.OrderDTO;
import com.oyd.springcloud.ticketclient.dao.TicketRepository;
import com.oyd.springcloud.ticketclient.domain.Ticket;
import com.oyd.springcloud.ticketclient.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 *
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketResource {

    @PostConstruct
    public void init() {
        long count = ticketRepository.count();
        if (count > 0) {
            return;
        }
        Ticket ticket = new Ticket();
        ticket.setName("Num.1");
        ticket.setTicketNum(100L);
        ticketRepository.save(ticket);
    }

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketService ticketService;

    @PostMapping("/lock")
    public Ticket lock(OrderDTO dto){
        Ticket ticket = ticketService.ticketLock(dto);
        return ticket;
    }

    @PostMapping("/lock2")
    public int lock2(OrderDTO dto){
        //TODO 如果是要操作多条sql更新的情况下，可以在此处使用分布式锁来实现
        int ticketLock2 = ticketService.ticketLock2(dto);
        return ticketLock2;
    }
}

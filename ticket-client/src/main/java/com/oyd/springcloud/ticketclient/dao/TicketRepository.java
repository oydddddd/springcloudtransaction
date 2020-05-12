package com.oyd.springcloud.ticketclient.dao;

import com.oyd.springcloud.ticketclient.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findOneByOwner(Long owner);

    Ticket findByTicketNum(Long ticketNum);

    Ticket findOneByTicketNumAndLockUser(Long ticketNum,Long lockUser);

    @Override
    @Modifying(clearAutomatically = true)
    Ticket save(Ticket ticket);

    @Modifying
    @Query("UPDATE ticket SET lockUser = ?1 where lockUser is NULL and ticketNum = ?2")
    int lockTicket(Long customerId,Long ticketNum);

    @Modifying
    @Query("UPDATE ticket SET owner = ?1,lockUser = null where lockUser = ?1 and ticketNum = ?2")
    int moveTicket(Long customerId,Long ticketNum);

    @Modifying
    @Query("UPDATE ticket SET lockUser = null where lockUser = ?1 and ticketNum = ?2")
    int unLockTicket(Long customerId,Long ticketNum);

    @Modifying
    @Query("UPDATE ticket SET owner = null where owner = ?1 and ticketNum = ?2")
    int unMoveTicket(Long customerId,Long ticketNum);
}

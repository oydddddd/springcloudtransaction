package com.oyd.commom;

import com.oyd.commom.dto.OrderDTO;

/**
 *
 */

public interface IOrderService {

//    OrderDTO create(OrderDTO dto);
    OrderDTO getMyOrder(Long id);
}

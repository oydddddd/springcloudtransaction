package com.oyd.springcloud.userclient.feign;

import com.oyd.commom.IOrderService;
import com.oyd.commom.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "order-client",path = "/api/order")
public interface OrderClient extends IOrderService {
    @GetMapping("/{id}")
    OrderDTO getMyOrder(@PathVariable(name = "id") Long id);
}

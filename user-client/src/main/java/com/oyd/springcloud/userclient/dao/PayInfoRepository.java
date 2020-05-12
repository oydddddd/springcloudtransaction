package com.oyd.springcloud.userclient.dao;

import com.oyd.springcloud.userclient.domain.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayInfoRepository extends JpaRepository<PayInfo,Long> {
    PayInfo findOneByOrderId(Long orderId);
}

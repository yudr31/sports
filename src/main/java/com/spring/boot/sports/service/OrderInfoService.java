package com.spring.boot.sports.service;

import com.spring.boot.common.bean.BaseBeanService;
import com.spring.boot.feign.pojo.sports.OrderInfo;
import com.spring.boot.sports.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author yuderen
 * version 2018/11/23 11:46
 */
@Service
public class OrderInfoService extends BaseBeanService<OrderInfoMapper, OrderInfo> {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    public List<OrderInfo> fetchRecordSortList(OrderInfo orderInfo){
        String sortStr = "book_date desc";
        orderInfo.setRecordStatus(ACTIVITY_STATUS);
        return orderInfoMapper.fetchRecordSortList(orderInfo,sortStr);
    }

}

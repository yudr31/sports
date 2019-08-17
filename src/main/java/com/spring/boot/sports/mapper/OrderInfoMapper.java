package com.spring.boot.sports.mapper;

import com.spring.boot.common.bean.BaseBeanMapper;
import com.spring.boot.feign.pojo.sports.OrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * author yuderen
 * version 2018/11/23 11:45
 */
public interface OrderInfoMapper extends BaseBeanMapper<OrderInfo> {

    List<OrderInfo> fetchRecordSortList(@Param("param") OrderInfo orderInfo, @Param("sortStr") String sortStr);

}

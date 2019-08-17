package com.spring.boot.sports.service;

import com.spring.boot.common.bean.BaseBeanService;
import com.spring.boot.feign.pojo.sports.CourtInfo;
import com.spring.boot.sports.mapper.CourtInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author yuderen
 * @version 2019-3-8 15:39:27
 */
@Service
public class CourtInfoService extends BaseBeanService<CourtInfoMapper, CourtInfo> {

    @Autowired
    private CourtInfoMapper courtInfoMapper;

    public Integer insertOrUpdateCourtInfo(List<CourtInfo> courtList){
        int count = 0;
        for (CourtInfo info : courtList){
            CourtInfo record = fetchRecordByGid(info.getGid());
            if (null != record){
                count += updateSelectiveByKey(info);
            } else {
                info.initCreateInfoNotGid(null);
                count += courtInfoMapper.insertSelective(info);
            }
        }
        return count;
    }

}
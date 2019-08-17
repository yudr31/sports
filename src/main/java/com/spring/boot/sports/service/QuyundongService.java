package com.spring.boot.sports.service;

import com.spring.boot.common.constant.MqConstant;
import com.spring.boot.common.http.DefaultHttpExecuter;
import com.spring.boot.common.http.common.HttpRequest;
import com.spring.boot.common.http.common.HttpResponse;
import com.spring.boot.common.util.JsonUtil;
import com.spring.boot.feign.pojo.sports.BookingInfo;
import com.spring.boot.feign.pojo.sports.CourtInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;

/**
 * author yuderen
 * version 2018/12/10 11:36
 */
@Slf4j
@Service
public class QuyundongService {

    @Value("${python_web_url}")
    private String pythonWebUrl;

    @Autowired
    private BookingInfoService bookingInfoService;
    @Autowired
    private CourtInfoService courtInfoService;
    @Autowired
    private DefaultHttpExecuter defaultHttpExecuter;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if(httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName("utf-8"));
                break;
            }
        }
        return restTemplate;
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void cronJob(){
        System.out.println("开始爬取球场信息");
        getData();
    }

    public void msgNotify(){
        String routingKey ="phone_message.phone_call";
        Map<String, String> map = new HashMap();
        map.put("phone", "");
        map.put("msg", "有可预订球场！");
        String msg = JsonUtil.toString(map);
        rabbitTemplate.convertAndSend(MqConstant.NOTIFY_EXCHANGE, routingKey, msg);
    }

    public void msgNotify(BookingInfo bookingInfo){
        String routingKey = 1 == bookingInfo.getNotifyType() ? "phone_message" : "phone_call";
        Map<String, String> map = new HashMap();
        map.put("phone", bookingInfo.getPhone());
        map.put("msg", "有可预订球场！");
        String msg = JsonUtil.toString(map);
        rabbitTemplate.convertAndSend(MqConstant.NOTIFY_EXCHANGE, routingKey, msg);
    }

    public List<CourtInfo> getData(){
        BookingInfo bookingInfo = bookingInfoService.fetchRecordByGid(15640232325338149L);
        if (!doNextGrab(bookingInfo)){
            return null;
        }
        List<CourtInfo> courtList = testGrabCourtList(bookingInfo);
        if (CollectionUtils.isEmpty(courtList)){
            return null;
        }
        String availableCourt = getAvailableCourt(courtList, bookingInfo);
        if (StringUtils.isNotEmpty(availableCourt)){
            msgNotify(bookingInfo);
            bookingInfo.setNotifyCount(bookingInfo.getNotifyCount() + 1);
            bookingInfo.setNotifyTime(LocalDateTime.now());
            if (bookingInfo.getOrderNow() == 1){
                doBookOrder(bookingInfo, availableCourt, courtList);
            }
        }
        bookingInfo.setGrabCount(bookingInfo.getGrabCount() + 1);
        bookingInfoService.updateSelectiveByKey(bookingInfo);
        return courtList;
    }

    private List<CourtInfo> testGrabCourtList(BookingInfo bookingInfo){
        String url = pythonWebUrl + "/craw_court";
        url += "?week_days=" + bookingInfo.getBookingDate();
        url += "&book_time=" + getBookTime(bookingInfo).trim();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        if (HttpStatus.OK.equals(result.getStatusCode())){
            String stringList = result.getBody().replaceAll("'","\"");
            List<CourtInfo> courtList = JsonUtil.toListObject(stringList, CourtInfo.class);
            courtInfoService.insertOrUpdateCourtInfo(courtList);
            return courtList;
        }
        return null;
    }

    /**
     * 调用爬虫项目爬取球场信息列表
     * @param bookingInfo
     * @return
     */
    private List<CourtInfo> grabCourtList(BookingInfo bookingInfo){
        String url = pythonWebUrl + "/craw_court";
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(url);
        httpRequest.addParam("week_days",bookingInfo.getBookingDate());
        httpRequest.addParam("book_time",getBookTime(bookingInfo).trim());
        HttpResponse httpResponse = defaultHttpExecuter.executeHttpRequest(httpRequest);
        if (httpResponse.getStatusCode() == 200){
            String result = httpResponse.getResponseMsg().replaceAll("'","\"");
            List<CourtInfo> courtList = JsonUtil.toListObject(result, CourtInfo.class);
            courtInfoService.insertOrUpdateCourtInfo(courtList);
            return courtList;
        }
        return null;
    }

    private void doBookOrder(BookingInfo bookingInfo, String courtName, List<CourtInfo> courtList){
        String courtIds = "";
        for (CourtInfo info : courtList){
            if (info.getCourt().equals(courtName)){
                courtIds += info.getGid();
            }
        }

        String url = pythonWebUrl + "/book_order";
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(url);
        httpRequest.addParam("court_id", courtIds);
        httpRequest.addParam("user_id", bookingInfo.getOrderUser());
        try {
            HttpResponse httpResponse = defaultHttpExecuter.executeHttpRequest(httpRequest);
            if (httpResponse.getStatusCode() == 200){
                System.out.println(httpResponse.getResponseMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否需要继续进行下一次爬虫
     * @param bookInfo
     * @return true-需要，false-不需要
     */
    private boolean doNextGrab(BookingInfo bookInfo){
        // 通知次数达到最大通知次数，无需进行继续爬虫
        if (bookInfo.getNotifyCount() == bookInfo.getMaxNotify()){
            return false;
        }
        LocalDateTime nextNotifyTime = bookInfo.getNotifyTime().plusMinutes(bookInfo.getTimeInterval());
        // 当前时间小于下次通知时间，即未到下次通知时间，无需进行继续爬虫
        if (LocalDateTime.now().compareTo(nextNotifyTime) < 0){
            return false;
        }
        return bookInfo.getBookingState() == 1; // 1-开启预订状态
    }

    private String getBookTime(BookingInfo bookingInfo){
        String bookingTime = "";
        Integer startTime = Integer.parseInt(bookingInfo.getBookingTime().substring(0,2));
        for (int i = 0; i < bookingInfo.getDuration(); i++){
            int entTime = startTime + 1;
            bookingTime = bookingTime + startTime + ":00" + "-" + entTime + ":00 ";
            startTime++;
        }
        return bookingTime;
    }

    /**
     * 是否有可预订球场
     * @param courtList
     * @param bookInfo
     * @return true-有，false-无
     */
    private String getAvailableCourt(List<CourtInfo> courtList, BookingInfo bookInfo){
        if (CollectionUtils.isEmpty(courtList)){
            return "";
        }
        String endStr = "号场".equals(bookInfo.getCourtType()) ? bookInfo.getCourtType() : "";
        Map<String, Integer> courtMap = new HashMap();
        courtList.stream().filter(courtInfo ->
                "available".equals(courtInfo.getBooking()) && courtInfo.getCourt().endsWith(endStr))
                .forEach(info -> courtMap.merge(info.getCourt(),1, Integer::sum));
        for (String court : courtMap.keySet()){
            if (courtMap.get(court).equals(bookInfo.getDuration())){
                return court;
            }
        }
        return "";
    }


}

package com.mmall.service;import com.github.pagehelper.PageInfo;import com.mmall.common.ServerResponse;import java.util.Map;/** * @author chenqiang * @create 2020-07-10 15:07 */public interface IOrderService {    ServerResponse pay(Long orderNo, Integer userId, String path);    ServerResponse aliCallback(Map<String, String> params);    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);    ServerResponse createOrder(Integer userId, Integer shippingId);    ServerResponse cancelOrder(Integer userId, Long orderNo);    ServerResponse getOrderCartProduct(Integer userId);    ServerResponse getOrderDetail(Integer userId, Long orderNo);    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);    //backend    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);    ServerResponse manageOrderDetail(Long orderNo);    ServerResponse<PageInfo> manageOrderSearch(Long orderNo, int pageNum, int pageSize);    ServerResponse manageSendGoods(Long orderNo);    void closeOrder(int hour);}
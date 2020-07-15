package com.mmall.service;import com.mmall.common.ServerResponse;import com.mmall.vo.CartVo;/** * @author chenqiang * @create 2020-07-03 11:59 */public interface ICartService {    ServerResponse<CartVo> addProduct(Integer userId, Integer productId, Integer count);    ServerResponse<CartVo> list(Integer userId);    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);    ServerResponse<CartVo> delete(Integer userId, String productIds);    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked);    ServerResponse<Integer> getCartProductCount(Integer userId);}
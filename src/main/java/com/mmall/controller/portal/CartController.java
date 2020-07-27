package com.mmall.controller.portal;import com.mmall.common.Const;import com.mmall.common.ResponseCode;import com.mmall.common.ServerResponse;import com.mmall.pojo.User;import com.mmall.service.ICartService;import com.mmall.util.CookieUtil;import com.mmall.util.JsonUtil;import com.mmall.util.RedisShardedPoolUtil;import com.mmall.vo.CartVo;import org.apache.commons.lang3.StringUtils;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;import javax.servlet.http.HttpServletRequest;/** * @author chenqiang * @create 2020-07-03 11:58 */@RestController@RequestMapping("/cart")public class CartController {    @Autowired    private ICartService iCartService;    @RequestMapping("add.do")    public ServerResponse<CartVo> addProduct(HttpServletRequest httpServletRequest, Integer productId, Integer count){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.addProduct(user.getId(),productId,count);    }    @RequestMapping("list.do")    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.list(user.getId());    }    @RequestMapping("update.do")    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest,Integer productId,Integer count){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.update(user.getId(),productId,count);    }    @RequestMapping("delete_product.do")    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest,String productIds){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.delete(user.getId(),productIds);    }    @RequestMapping("select_all.do")    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);    }    @RequestMapping("un_select_all.do")    public ServerResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);    }    @RequestMapping("select.do")    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest,Integer productId){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);    }    @RequestMapping("un_select.do")    public ServerResponse<CartVo> unSelect(HttpServletRequest httpServletRequest,Integer productId){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);    }    @RequestMapping("get_cart_product_count.do")    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){        String loginToken = CookieUtil.readLoginToken(httpServletRequest);        if (StringUtils.isEmpty(loginToken)) {            return ServerResponse.createByErrorMessage("用户没有登录，无法获取当前用户信息");        }        String userJsonStr = RedisShardedPoolUtil.get(loginToken);        User user = JsonUtil.string2Obj(userJsonStr, User.class);        if(user == null){            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.getCartProductCount(user.getId());    }}
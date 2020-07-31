package com.mmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author chenqiang
 * @create 2020-07-30 11:14
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");
        //请求中Controller的方法名
        HandlerMethod handlerMethod = (HandlerMethod) o;

        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数，具体的参数key以及value是什么，我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map parameterMap = httpServletRequest.getParameterMap();
        Iterator iterator = parameterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;

            //request这个参数的map，里面的value返回的是一个String[]
            Object value = entry.getValue();
            if (value instanceof String[]) {
                String[] strs = (String[]) value;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        log.info("权限拦截器拦截到请求：methodName:{},className:{},param:{}", methodName, className, requestParamBuffer);

        //如果是登录请求，不需要拦截
        if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")) {
            //登录请求，不打印参数，因为参数里有密码，全部会打印到日志中，防止日志泄露
            log.info("拦截器拦截到请求,className:{},methodName:{}", className, methodName);
            return true;
        }

        User user = null;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }

        if (user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)) {
            //返回false，既不会调用controller里的方法
            //这里要添加reset，否则报异常 getWriter() has already been called for this response.
            httpServletResponse.reset();
            //这里要设置编码，否则会乱码
            httpServletResponse.setCharacterEncoding("UTF-8");
            //这里要设置返回值的类型，因为全部是json接口。
            httpServletResponse.setContentType("application/json;charset=UTF-8");

            PrintWriter writer = httpServletResponse.getWriter();

            //上传由于富文本的控件要求，要特殊处理返回值，这里面区分是否登录以及是否有权限
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richTextImgUpload")) {
                    log.info("拦截器拦截到请求,className:{},methodName:{}", className, methodName);
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    writer.write(JsonUtil.obj2String(resultMap));
                } else {
                    writer.write(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }
            } else {
                //不是管理员
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richTextImgUpload")) {
                    log.info("拦截器拦截到请求,className:{},methodName:{}", className, methodName);
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    writer.write(JsonUtil.obj2String(resultMap));
                } else {
                    writer.write(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，不是管理员，无权限操作")));
                }
            }

            writer.flush();
            writer.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}

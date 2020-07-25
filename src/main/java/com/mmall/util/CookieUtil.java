package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenqiang
 * @create 2020-07-24 11:12
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMIAN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("read cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    //X:domain=".happymmall.com" 所有二级三级域名都可以访问到一级域名的cookie，同级的域名直接不能共享cookie
    //a:A.happymmall.com            cookie:domain=A.happymmall.com;path="/"
    //b:B.happymmall.com            cookie:domain=B.happymmall.com;path="/"
    //c:A.happymmall.com/test/cc    cookie:domain=A.happymmall.com;path="/test/cc"
    //d:A.happymmall.com/test/dd    cookie:domain=A.happymmall.com;path="/test/dd"
    //e:A.happymmall.com/test       cookie:domain=A.happymmall.com;path="/test"

    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMIAN);
        //代表设置在根目录
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //单位是秒
        //如果这个maxAge不设置的话，cookie就不会写入硬盘，而是写在内存，当前页面有效,退出了浏览器cookie就会删除
        //设置了maxAge，会写入硬盘，即使是关机再开机也还是会有的
        //如果是-1，代表永久。如果是0，表示删除cookie
        cookie.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setDomain(COOKIE_DOMIAN);
                cookie.setPath("/");
                //设置为0，表示删除此cookie
                cookie.setMaxAge(0);
                log.info("del cookieName:{},cookieVlaue:{}",cookie.getName(),cookie.getValue());
                response.addCookie(cookie);
            }
        }
    }
}

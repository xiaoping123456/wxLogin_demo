package com.laowang.wx_login_demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.laowang.wx_login_demo.utils.HttpClientUtil;
import com.laowang.wx_login_demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author 小王造轮子
 * @description 微信登录controller
 * @date 2022/4/17
 */
@RestController
public class WxLoginController {

    private String appId = "appId";
    private String appSecret = "appSecret";
    private String redirectUri = "redirectUri";

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 生成二维码
     * @return
     */
    @GetMapping("/getWechatQtCode")
    public Object getWechatQtCode(){
        JSONObject res = new JSONObject();
        try {
            // 生成随机数 作为二维码的key
            String key = UUID.randomUUID().toString().replace("-","");

            String redirectUri2 = URLEncoder.encode(redirectUri,"utf-8");

            String oauthUrl = " https://open.weixin.qq.com/connect/qrconnect" +
                    "?appid=" + appId +
                    "&redirect_uri=" + redirectUri2 +
                    "&response_type=code" +
                    "&scope=snsapi_login" +
                    "&state=" + key +
                    "#wechat_redirect";


            res.put("key",key);
            res.put("url",oauthUrl);
            return res;
        } catch (Exception e) {
            System.out.println("二维码生成失败");
            return null;
        }
    }

    /**
     * 微信认证/登录的回调方法（微信开放平台填写的回调域）
     * @return
     */
    @RequestMapping("/api/ucenter/wx/callback")
    public void wxCallBack(@RequestParam("code")String code,@RequestParam("state")String state){
        //把二维码url和key存入redis
        redisUtil.set(state,code,300);

//        //获取access_token
//        String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
//                "?appid=" + appId +
//                "&secret=" + appSecret +
//                "&code=" + code +
//                "&grant_type=authorization_code";
//        JSONObject resultObject = HttpClientUtil.httpGet(url);
//
//        //请求获取userInfo
//        String infoUrl = "https://api.weixin.qq.com/sns/userinfo" +
//                "?access_token=" + resultObject.getString("access_token") +
//                "&openid=" + resultObject.getString("openid") +
//                "&lang=zh_CN";
//        JSONObject resultInfo = HttpClientUtil.httpGet(infoUrl);
//
//        System.out.println(resultInfo.toJSONString());
    }

    @RequestMapping("/wxLogin/checkState")
    public Object checkState(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        JSONObject res = new JSONObject();
        String key = params.getString("key");
        Object obj = redisUtil.get(key);
        if (obj==null){
            res.put("code",1);
            // 未扫码
            res.put("state",0);
        }else{
            res.put("code",1);
            // 扫码并已确认
            res.put("state",2);
            //获取微信用户信息
            //获取access_token
            String code = (String) obj;
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=" + appId +
                    "&secret=" + appSecret +
                    "&code=" + code +
                    "&grant_type=authorization_code";
            JSONObject resultObject = HttpClientUtil.httpGet(url);

            //请求获取userInfo 并放入返回结果中
            String infoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=" + resultObject.getString("access_token") +
                    "&openid=" + resultObject.getString("openid") +
                    "&lang=zh_CN";
            JSONObject resultInfo = HttpClientUtil.httpGet(infoUrl);
            res.put("userInfo",resultInfo);
        }
        return res;
    }


    /**
     * 测试
     * @return
     */
    @GetMapping("/login")
    public String login(){
        String url = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=" + appId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=STATE" +
                "#wechat_redirect";
        return url;
    }

}

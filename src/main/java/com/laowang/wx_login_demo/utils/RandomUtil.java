package com.laowang.wx_login_demo.utils;

import java.util.Random;

/**
 * @author 小王造轮子
 * @description 操作随机数的工具类
 * @date 2022/4/17
 */
public class RandomUtil {

    public static String generateRandom(){
        Random random = new Random();
        String r = "";
        for(int i=0;i<3;i++){
            int x = random.nextInt(10);
            r = r+x;
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println(generateRandom());
    }

}

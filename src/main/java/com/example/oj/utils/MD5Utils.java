package com.example.oj.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }
}

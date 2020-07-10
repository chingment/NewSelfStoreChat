package com.hyphenate.chatuidemo.fanju.own;

import android.util.Base64;

import com.hyphenate.chatuidemo.BuildConfig;
import com.hyphenate.chatuidemo.fanju.utils.SHA256Encrypt;
import com.hyphenate.chatuidemo.fanju.utils.StringUtil;


public class Config {
    public static final boolean IS_BUILD_DEBUG = BuildConfig.DEBUG;//打包模式
    public static final boolean IS_APP_DEBUG = BuildConfig.ISAPPDEBUG;//调试模式
    public static String getSign(String key, String secret, String data, String currenttime) {
        // 待加密
        String queryStr = key + secret + currenttime + data;
//        LogUtil.e(TAG, "queryStr>>==>>" + queryStr);
        String sortedStr = StringUtil.sortString(queryStr);
//        LogUtil.e(TAG, "sortedStr>>==>>" + sortedStr);
        String sha256edStr = SHA256Encrypt.bin2hex(sortedStr).toLowerCase();
//        LogUtil.e(TAG, "sha256edStr>>==>>" + sha256edStr);
        String base64Str = Base64.encodeToString(sha256edStr.getBytes(), Base64.NO_WRAP);
//        String base64Str = StringUtils.replaceEnter(Base64.encodeToString(sha256edStr.getBytes(), Base64.NO_WRAP), "");
//        LogUtil.e(TAG, "加密后>>==>>" + base64Str);
        return base64Str;
    }

    public class URL {
        public static final String own_LoginByAccount= BuildConfig.ENVIRONMENT + "/api/Own/LoginByAccount";
        public static final String user_GetInfoByUserName= BuildConfig.ENVIRONMENT + "/api/User/GetInfoByUserName";
    }
}


package com.hyphenate.chatuidemo.fanju.utils;

import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.chatuidemo.fanju.own.Config;


/**
 * 项目名称：LGF_project
 * 类描述：
 * 创建人：tuchg
 * 创建时间：16/12/18 14:49
 */

public class LogUtil {

    public static String customTagPrefix = "lgf_log";

    private LogUtil() {
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.d(tag, content);
    }

    public static void d(String tag, String content) {
        if (!Config.IS_APP_DEBUG) return;
        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.d(tag, content, tr);
    }

    public static void e(String content) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.e(tag, content);
    }

    public static void eNeed(String content) {
        String tag = generateTag();
        Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.e(tag, content, tr);
    }

    public static void e(String tag, String content) {
        if (!Config.IS_APP_DEBUG) return;
        Log.e(tag, content);
    }

    public static void e(String tag, String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        Log.e(tag, content,tr);
    }

    public static void i(String content) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.i(tag, content);
    }

    public static void i(String tag, String content) {
        if (!Config.IS_APP_DEBUG) return;
        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.i(tag, content, tr);
    }

    public static void v(String content) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.v(tag, content, tr);
    }

    public static void v(String tag, String content) {
        if (!Config.IS_APP_DEBUG) return;
        Log.v(tag, content);
    }

    public static void w(String content) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.w(tag, tr);
    }

    public static void w(String tag, String content) {
        if (!Config.IS_APP_DEBUG) return;
        Log.w(tag, content);
    }

    public static void wtf(String content) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.wtf(tag, content, tr);
    }

    public static void wtf(String tag, String content) {
        if (!Config.IS_APP_DEBUG) return;
        Log.wtf(tag, content);
    }

    public static void wtf(Throwable tr) {
        if (!Config.IS_APP_DEBUG) return;
        String tag = generateTag();

        Log.wtf(tag, tr);
    }

}

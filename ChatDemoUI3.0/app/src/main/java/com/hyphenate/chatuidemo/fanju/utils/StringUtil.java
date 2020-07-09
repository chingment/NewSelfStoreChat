package com.hyphenate.chatuidemo.fanju.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chingment on 2017/12/18.
 */

public class StringUtil {

    public static boolean isEmpty(Object str) {
        return str == null || str.toString().length() == 0;
    }

    public static boolean isEmptyNotNull(String input) {
        if (input == null || "".equals(input) || "null".equals(input.toLowerCase()))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static Boolean isMobileNo(String str) {
        Boolean isMobileNo = false;
        try {
            Pattern p = Pattern.compile("^((1[2-9][0-9]))\\d{8}$");
            Matcher m = p.matcher(str);
            isMobileNo = m.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMobileNo;
    }

    public static String sortString(String str) {
        String result = "";
        if (!isEmpty(str)) {
            char[] arrayCh = str.toCharArray();
            Arrays.sort(arrayCh);
            result = String.valueOf(arrayCh);
        }
        return result;
    }
}

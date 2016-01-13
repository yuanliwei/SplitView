package com.ylw.split.splitview.adapter;

/**
 * Created by 袁立位 on 2016/1/13 13:57.
 */
public class Cb {
    public static String cbp(Object... params) {
        StringBuffer sb = new StringBuffer(" ");
        if (params != null) {
            for (Object obj : params) {
                sb.append(obj).append(" ");
            }
        }
        return sb.toString();
    }
}

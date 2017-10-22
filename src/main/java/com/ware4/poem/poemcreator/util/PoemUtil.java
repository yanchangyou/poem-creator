package com.ware4.poem.poemcreator.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * FileName: PoemUtil.java
 * Date:     2017/10/22 下午11:24
 */
public class PoemUtil {

    public static String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}

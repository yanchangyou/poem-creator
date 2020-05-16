package com.ware4.poem.poemcreator.controller;

/*
 * FileName: PoemCreatorController.java
 * Date:     2017/10/22 下午11:12
 *
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jdk.nashorn.internal.parser.JSONParser;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ware4.poem.poemcreator.util.HttpClientWrapper;

@Controller
@EnableAutoConfiguration
@RequestMapping("/poem")
public class PoemCreatorController {

    String MESSAGE_TEMPLAGE = "{\n" + //
            "  \"poemIdx\": 0, \n" + //
            "  \"poems\": [\n" + //
            "    [\n" + "<<MESSAGE>>" + "\n" + //
            "    ]]}";

    String DEFAULT_MESSAGE = "\"诗人太忙请稍后\", \"闭上眼睛歇歇手\",\"右上有角公众号\", \"联系客户聊感受\"";

    String message = MESSAGE_TEMPLAGE.replaceAll("<<MESSAGE>>", DEFAULT_MESSAGE);

    volatile boolean showMessageFlag;

    long preTime = System.currentTimeMillis();
    long timeDelay = 500L;// 毫秒
    int poemFrom = 1; // 1:IBM,2: 易源数据（https://www.showapi.com/api/lookPoint/950）

    Map<Integer, String> poemFromMap = new HashMap();

    {
        poemFromMap.put(1, "IBM偶得");
        poemFromMap.put(2, "易源数据");
        poemFromMap.put(3, "本地");
        poemFromMap.put(4, "百度");
    }

    String adContent = "更多的免费软件，请右上角关注公众号“进好店商户服务”";

    /**
     * 打开消息
     *
     * @return
     */
    @RequestMapping("/index798")
    @ResponseBody
    public String index() {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href='/poem/setTimeDelay?timeDelay=500'>设置时间延迟</a><br>");
        builder.append("<a href='/poem/setMessageOn'>打开消息</a><br>");
        builder.append("<a href='/poem/setMessageOff'>关闭消息</a><br>");
        builder.append("<a href='/poem/showDefaultMessage'>显示默认消息</a><br>");
        builder.append("<a href='/poem/getMessage'>获取当前设置的消息</a><br>");
        builder.append("<a href='/poem/showMessage?message=&second='>显示消息（URL输入参数）</a><br>");
        builder.append("<a href='/poem/setAdContent?adContent='>设置广告词</a><br>");
        builder.append("<a href='/poem/getAdContent'>查看广告词</a><br>");
        builder.append("<a href='/poem/setPoemFrom?poemFrom=1'>从IBM偶得获取诗</a><br>");
        builder.append("<a href='/poem/setPoemFrom?poemFrom=2'>从易源获取诗</a><br>");

        return builder.toString();
    }

    /**
     * @param seed 作诗关键词
     * @param type 类型： 五言、七言
     * @param uuid uuid，避免重复
     * @return
     */
    @RequestMapping("/getpoems")
    @ResponseBody
    public String getpoems(String seed, String type, String uuid, String flag) {
        String result = "";

        if (showMessageFlag || "true".equals(flag) || isTpsControll()) {
            result = this.message;
        } else {
//            result = getPoemFromBaidu(seed);
            result = converToMyPoem(seed);
            // switch (poemFrom) {
            // case 3:
            // result = converToMyPoem(getPoemFromMy(seed));
            // break;
            // case 2:
            // int yiyuanNum = "3".equals(type) ? 5 : 7;
            // result = converToIBMPoem(getPoemFromYiyuan(yiyuanNum, seed));
            // break;
            // default:
            // result = getPoemFromIBM(seed, type, uuid, flag);
            // }
        }

        return result;
    }

    /**
     * 转换为my诗的格式：格式应该独立
     *
     * @param key
     * @return
     */
    String converToMyPoem(String key) {

        StringBuilder poemBuf = new StringBuilder();

        for (int i = 0; i < 1; i++) {
            String poem = getPoemFromMy(key);

            String[] poemList = poem.split("，|。");
            String poemArray = "";
            for (String one : poemList) {
                poemArray += "\"" + one + "\",";
            }
            poemArray = poemArray.substring(0, poemArray.length() - 1);
            poemBuf.append("[").append(poemArray).append("],");
        }

        return "{\n" + //
                "  \"poemIdx\": 0, \n" + //
                "  \"poems\": [\n" + //
                "    " + poemBuf.substring(0, poemBuf.length() - 1) + "\n" + //
                "    ]}";
    }

    /**
     * 调用IBM偶得系统获取诗
     *
     * @param seed
     * @param type
     * @param uuid
     * @param flag
     * @return
     */
    private String getPoemFromIBM(String seed, String type, String uuid, String flag) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://crl.ptopenlab.com:8800/poem/getpoems?");
        urlBuilder.append("&seed=").append(seed);
        urlBuilder.append("&type=").append(type);
        urlBuilder.append("&uuid=").append(uuid);
        String result = "";
        try {
            result = HttpClientWrapper.httpGet(urlBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            result = this.message;
        }
        return result;
    }

    /**
     * 调用IBM偶得系统获取诗
     *
     * @param keywords
     * @return
     */
    private String getPoemFromBaidu(String keywords) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://aip.baidubce.com/rpc/2.0/creation/v1/poem?");
        urlBuilder.append("&access_token=").append("24.958c5a4f2a95907b672ba837cb025625.2592000.1577077412.282335-17799252");
//        BaiduPoem baiduPoem = null;
        String poemString = null;
        try {
            String json = "{\"text\":\"" + keywords + "\",\"index\":" + new Random().nextInt(100) + "}";
            String result = HttpClientWrapper.httpPostJson(urlBuilder.toString(), json);
            JSONObject object = JSON.parseObject(result);
            System.out.println(object);
            poemString = (String) ((JSONObject) ((JSONArray) object.get("poem")).get(0)).get("content");
        } catch (IOException e) {
            e.printStackTrace();
            poemString = this.message;
            return this.message;
        }
        Map map = new HashMap();
        map.put("poemIdx", 0);
        map.put("poems", new String[][]{poemString.split("\t")});
        return JSON.toJSONString(map);
   }

    /**
     * 调用易源接口获取诗
     *
     * @param num
     * @param key
     * @return
     */
    @RequestMapping("/getPoemFromYiyuan")
    @ResponseBody
    public String getPoemFromYiyuan(int num, String key) {
        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder
                .append("http://route.showapi.com/950-1?showapi_appid=58017&type=1&yayuntype=3&showapi_sign=4a00826925ea47c1878d2b3bb96b4e78");
        urlBuilder.append("&num=").append(num);
        urlBuilder.append("&key=").append(key);
        String result = "";
        try {
            result = HttpClientWrapper.httpGet(urlBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            result = this.message;
        }
        return result;
    }

    /**
     * 调用腾讯云接口获取诗
     *
     * @param key
     * @return
     */
    @RequestMapping("/getPoemFromMy")
    @ResponseBody
    public String getPoemFromMy(String key) {
        StringBuilder urlBuilder = new StringBuilder();

        //urlBuilder.append("http://118.24.108.154:5000/poem?style=3");
        urlBuilder.append("http://127.0.0.1:5000/poem?style=3");
        urlBuilder.append("&start=").append(key);
        String result = "";
        try {
            result = HttpClientWrapper.httpGet(urlBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            result = this.message;
        }
        return result;
    }

    /**
     * 转换为IBM诗的格式：格式应该独立
     *
     * @param poem
     * @return
     */
    String converToIBMPoem(String poem) {
        String result = poem;

        // 提取诗
        String poemList = poem.substring(poem.indexOf("\"list\":[\"") + 8, poem.length() - 3);
        // 变数字
        String poemArray = poemList;
        poemArray = poemArray.replaceAll("。\",\"", "\"\\],\\[\"");
        poemArray = poemArray.replaceAll("，", "\",\"");
        poemArray = poemArray.replaceAll("。", "\",\"");
        return "{\n" + //
                "  \"poemIdx\": 0, \n" + //
                "  \"poems\": [[\n" + //
                "    " + poemArray + "\n" + //
                "    ]]}";
    }

    /**
     * 两次时间间隔小于500，触发流控
     *
     * @return
     */
    public boolean isTpsControll() {
        boolean result = System.currentTimeMillis() - preTime < timeDelay;
        preTime = System.currentTimeMillis();
        return result;
    }

    /**
     * 设置时间延迟
     *
     * @return
     */
    @RequestMapping("/setTimeDelay")
    @ResponseBody
    public String setTimeDelay(long timeDelay) {
        this.timeDelay = timeDelay;
        return "setTimeDelay OK!";
    }

    /**
     * 打开消息
     *
     * @return
     */
    @RequestMapping("/setMessageOn")
    @ResponseBody
    public String setMessageOn() {
        this.showMessageFlag = true;
        return "setMessageOn OK!";
    }

    /**
     * 关闭消息
     *
     * @return
     */
    @RequestMapping("/setMessageOff")
    @ResponseBody
    public String setMessageOff() {
        this.showMessageFlag = false;
        return "setMessageOff OK!";
    }

    /**
     * 获取当前设置的消息
     *
     * @return
     */
    @RequestMapping("/getMessage")
    @ResponseBody
    public String getMessage() {
        return message;
    }

    /**
     * 显示消息
     *
     * @param message
     * @return
     */
    @RequestMapping("/showMessage")
    @ResponseBody
    public String showMessage(String message, Integer second) {

        if (message == null || "".equals(message.trim())) {
            return "bad param!";
        }
        String[] messages = message.split(",|，");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messages.length; i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append("\"").append(messages[i]).append("\"");
        }

        this.message = MESSAGE_TEMPLAGE.replaceAll("<<MESSAGE>>", builder.toString());
        showMessageFlag = true;

        sleep(second);

        return "showMessage OK!";
    }

    /**
     * 显示消息
     *
     * @return
     */
    @RequestMapping("/showDefaultMessage")
    @ResponseBody
    public String showDefaultMessage(final Integer second) {

        this.message = MESSAGE_TEMPLAGE.replaceAll("<<MESSAGE>>", DEFAULT_MESSAGE);
        showMessageFlag = true;

        sleep(second);

        return "OK!";
    }

    /**
     * 设置广告
     *
     * @return
     */
    @RequestMapping("/setAdContent")
    @ResponseBody
    public String setAdContent(String adContent) {

        if (adContent == null || adContent.trim().equals("")) {
            return "no param!";
        }

        this.adContent = adContent;

        return "OK!" + adContent;
    }

    /**
     * 获取广告
     *
     * @return
     */
    @RequestMapping("/getAdContent")
    @ResponseBody
    public String getAdContent() {

        return adContent;
    }

    @RequestMapping("/getPoemFrom")
    @ResponseBody
    public int getPoemFrom() {
        return poemFrom;
    }

    @RequestMapping("/setPoemFrom")
    @ResponseBody
    public void setPoemFrom(int poemFrom) {
        this.poemFrom = poemFrom;
    }

    private void sleep(Integer second) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    int theSecond = (second == null || 0 == second) ? 60 : second;
                    Thread.sleep(theSecond * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showMessageFlag = false;
            }

        }.start();

    }
}

class BaiduPoem {
    String content;
    String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

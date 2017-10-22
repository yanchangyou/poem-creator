package com.ware4.poem.poemcreator.controller;

/*
 * FileName: PoemCreatorController.java
 * Date:     2017/10/22 下午11:12
 *
 */

import java.io.IOException;

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

    String DEFAULT_MESSAGE = "\"诗人太忙请稍后\", \"喝杯凉茶歇歇手\",\"闭上眼睛养养眼\", \"联系客户聊感受\"";

    String message = MESSAGE_TEMPLAGE.replaceAll("<<MESSAGE>>", DEFAULT_MESSAGE);

    volatile boolean showMessageFlag;

    /**
     *
     * @param seed 作诗关键词
     * @param type 类型： 五言、七言
     * @param uuid uuid，避免重复
     * @return
     */
    @RequestMapping("/getpoems")
    @ResponseBody
    public String getpoems(String seed, String type, String uuid, String flag) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://crl.ptopenlab.com:8800/poem/getpoems?");
        urlBuilder.append("&seed=").append(seed);
        urlBuilder.append("&type=").append(type);
        urlBuilder.append("&uuid=").append(uuid);
        String result = "";
        if (showMessageFlag || "true".equals(flag)) {
            result = this.message;
        } else {
            try {
                result = HttpClientWrapper.httpGet(urlBuilder.toString());
            } catch (IOException e) {
                result = this.message;
            }
        }

        return result;
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
     * 显示消息
     *
     * @param message
     * @return
     */
    @RequestMapping("/showMessage")
    @ResponseBody
    public String showMessage(String message, Integer second) {

        if (message == null) {
            return "bad param!";
        }
        this.message = MESSAGE_TEMPLAGE.replaceAll("<<MESSAGE>>", message);
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

    private void sleep(Integer second) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    int theSecond = (second == null) ? 60 : second;
                    Thread.sleep(theSecond * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showMessageFlag = false;
            }

        }.start();

    }
}

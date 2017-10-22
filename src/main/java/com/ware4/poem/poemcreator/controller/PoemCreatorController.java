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

    String errorMessage = "{\n"
            + "  \"poemIdx\": 0, \n"
            + "  \"poems\": [\n"
            + "    [\n"
            + "      \"诗人太忙请稍后\", \n"
            + "      \"喝杯凉茶歇歇手\", \n"
            + "      \"闭上眼睛养养眼\", \n"
            + "      \"联系客户聊感受\"\n"
            + "    ]]}";

    @RequestMapping("/getpoems")
    @ResponseBody
    public String create(String seed, String type, String uuid, String isError) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://crl.ptopenlab.com:8800/poem/getpoems?");
        urlBuilder.append("&seed=").append(seed);
        urlBuilder.append("&type=").append(type);
        urlBuilder.append("&uuid=").append(uuid);
        String result = "";
        if ("true".equals(isError)) {
            result = errorMessage;
        } else {
            try {
                result = HttpClientWrapper.httpGet(urlBuilder.toString());
            } catch (IOException e) {
                result = errorMessage;
            }
        }

        return result;
    }
}

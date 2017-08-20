package ru.ereke.appsalem;

import java.util.HashMap;

/**
 * Created by Erasyl on 01.02.2017.
 */

public class UrlMaker {
    // делаем URL для парсера
    String getUrl(String url, HashMap<String, String> params) {
        boolean first = true;
        StringBuilder sbUrl = new StringBuilder(url);
        sbUrl.append("?");
        for (String key : params.keySet()) {
            if (first) first = false;
            else sbUrl.append("&");
            sbUrl.append(key).append("=").append(params.get(key));
        }
        return sbUrl.toString();
    }
}

package cn.thinkjoy.sms.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP工具箱
 * <p/>
 * Created by gryang on 15-04-15.
 */
public final class HttpTookit {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTookit.class);

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url    请求的URL地址
     * @param params 请求的查询参数,可以为null
     * @return 返回请求响应的数据
     */
    public static String doPost(String url, Map<String, String> params) {

        List<NameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {

            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

        }
        try {
            HttpResponse response = Request.Post(url).connectTimeout(60000).socketTimeout(60000)
                    .bodyForm(list).execute().returnResponse();

            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            LOGGER.error("执行HTTP Post请求" + url + "时，发生异常！", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url 请求的URL地址
     * @return 返回请求响应的数据
     */
    public static String doPost(String url) {

        try {
            HttpResponse response = Request.Post(url).connectTimeout(60000).socketTimeout(60000)
                    .execute().returnResponse();

            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            LOGGER.error("执行HTTP Post请求" + url + "时，发生异常！", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url 请求的URL地址
     * @return 返回请求响应的数据
     */
    public static String doPostForJson(String url, String json) {

        try {
            BasicNameValuePair nameValuePair = new BasicNameValuePair("Authorization",
                    "Basic " + "c9e1be2768b238d835679f6ab1d97a");
            HttpResponse response = Request.Post(url).bodyForm(nameValuePair)
                    .bodyString(json, ContentType.APPLICATION_JSON).connectTimeout(60000)
                    .socketTimeout(60000).execute().returnResponse();

            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            LOGGER.error("执行HTTP Post请求" + url + "时，发生异常！", e);
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

    }
}

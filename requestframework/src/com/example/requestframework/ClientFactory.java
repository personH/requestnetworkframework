package com.example.requestframework;

import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * @author hcz
 * @version 1.0
 * @createtime 2015/03/06
 *
 * 工厂模式,产生DefaultHttpClient对象实例
 */
public class ClientFactory {

    private static final Integer SOCKET_TIMEOUT = 10000;
    private static final Integer CONNECTION_TIMEOUT = 10000;

    public static DefaultHttpClient createClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        return new DefaultHttpClient(params);
    }

}

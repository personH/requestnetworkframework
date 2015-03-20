package com.example.requestframework;

import android.os.Handler;
import android.os.Message;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * @author hcz
 * @version 1.0
 * @createtime 2015/03/06
 *
 * 真正的http请求
 */
public class HttpRequest {

    private UrlEncodedFormEntity urlEncodedFormEntity;
    private HashMap<String, String> header;
    private int requestType;
    private HttpRequestBase requestBase;
    private URI requestUri;
    private Handler handler;

    /**
     * Http请求GET类型
     */
    public static final int REQUEST_TYPE_GET = 0;
    /**
     * Http请求POST类型
     */
    public static final int REQUEST_TYPE_POST = 1;

    public HttpRequest(int requestType, URI reauestUri, List<NameValuePair> nameValuePairs, HashMap<String, String> header) {
        this.requestType = requestType;
        this.requestUri = reauestUri;
        this.urlEncodedFormEntity = getRequestParams(nameValuePairs);
        this.header = header;
    }

    //请求报文的参数,以及参数的编码格式
    private UrlEncodedFormEntity getRequestParams(List<NameValuePair> nameValuePairs) {
        try {
            UrlEncodedFormEntity requestContent = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            return requestContent;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行远程请求
     */
    public void startNetwork() {

        if (requestType == REQUEST_TYPE_POST) {
            requestBase = new HttpPost();
            ((HttpPost) requestBase).setEntity(urlEncodedFormEntity);
        } else if (requestType == REQUEST_TYPE_GET) {
            requestBase = new HttpGet();
        }
        requestBase.setURI(requestUri);
        addRequestHeader(this.header);

        try {
            HttpResponse response = ClientFactory.createClient().execute(requestBase);

            Header[] receiveHeaders = response.getAllHeaders();
            analyticHeaders(receiveHeaders);

            Object receiveBody = getBody(response);
            int resultCode = response.getStatusLine().getStatusCode();
            if (resultCode != 200) {//请求失败的情况,返回错误代码
                if (receiveBody != null) {
                    String content = new String((byte[]) receiveBody);
                    JSONObject jsonObject = new JSONObject(content);
                    Message msg = handler.obtainMessage(resultCode, jsonObject);
                    handler.sendMessage(msg);
                }
            } else {//请求返回成功
                Message msg = handler.obtainMessage(ConstantPool.HANDLER_SUC, receiveBody);
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            Message msg = handler.obtainMessage(ConstantPool.HANDLER_UNKOWN_ERROR, null);
            handler.sendMessage(msg);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析返回报文
     *
     * @param response
     * @return
     * @throws IOException
     */
    protected Object getBody(HttpResponse response) throws IOException {
        byte[] data = null;
        InputStream inputStream = response.getEntity().getContent();
        if (inputStream != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                byte[] tmpData = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(tmpData)) > -1) {
                    bos.write(tmpData, 0, length);
                }
                data = bos.toByteArray();
            } catch (IOException ioException) {
                throw ioException;
            } finally {
                if (bos != null) {
                    bos.close();
                    bos = null;
                }
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            }
        }
        return data;
    }

    /**
     * 分解head取出cookie
     *
     * @param receiveHeaders
     */
    protected final void analyticHeaders(Header[] receiveHeaders) {

        if ("".equals(MyActivity.sessionid)) {
            for (int i = 0; i < receiveHeaders.length; i++) {
                Header header = receiveHeaders[i];
                if (header.getName().equals("Set-Cookie")) {
                    String cookieval = header.getValue();
                    if (cookieval != null) {
                        MyActivity.sessionid = cookieval.substring(0, cookieval.indexOf(";"));
                        break;
                    }
                }
            }
        } else {
            //sessionid 不为空
        }
    }

    /**
     * 添加请求报文头部信息
     * @param header
     */
    private void addRequestHeader(HashMap<String, String> header) {
        if (header != null) {
            for (String key : header.keySet()) {
                requestBase.addHeader(key, header.get(key));
            }
        }
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}

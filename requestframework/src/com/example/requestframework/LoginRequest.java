package com.example.requestframework;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hcz
 * @version 1.0
 * @createtime 2015/03/06
 *
 * BaseRequest的子类,实现登陆的请求
 * 成员变量即请求需要包含的参数
 */
public class LoginRequest extends BaseRequest {

    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        requestType = HttpRequest.REQUEST_TYPE_GET;//固定的
        requestUri = URI.create(ConstantPool.SessionId);//固定的
    }

    @Override
    public void request() {

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", username));
        formparams.add(new BasicNameValuePair("password", password));



        HttpRequest httpRequest = new HttpRequest(requestType, requestUri, formparams, requestHeader);
        httpRequest.setHandler(this.handler);
        HttpRequestManager httpRequestManager = HttpRequestManager.getInstance();
        httpRequestManager.addRequest(httpRequest);
    }

    @Override
    public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
        this.onCallbackListener = onCallbackListener;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

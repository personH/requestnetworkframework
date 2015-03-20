package com.example.requestframework;

import android.os.Handler;
import android.os.Message;

import java.net.URI;
import java.util.HashMap;

/**
 * @author hcz
 * @version 1.0
 * @createtime 2015/03/06
 * <p/>
 * 请求基类
 */
public abstract class BaseRequest {

    protected int requestType;
    protected URI requestUri;
    protected OnCallbackListener onCallbackListener;
    protected HashMap<String, String> requestHeader;

    public BaseRequest() {

        //每次请求,携带的cookie参数,让服务器判断当前的用户是否在session的生命周期里面
        if (MyActivity.sessionid != null && !"".equals(MyActivity.sessionid)) {
            requestHeader = new HashMap<String, String>();
            requestHeader.put("Cookie", "JSESSIONID=" + MyActivity.sessionid + "; Path=/; HttpOnly");
        }

    }


    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (onCallbackListener != null) {
                onCallbackListener.onCallbakListener(msg.what, msg.obj);
            }
        }
    };


    protected abstract void request();

    protected abstract void setOnCallbackListener(OnCallbackListener onCallbackListener);


    protected URI getRequestUri() {
        return requestUri;
    }

    protected void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }


    protected interface OnCallbackListener {

        public void onCallbakListener(int resultCode, Object resultObj);

    }

}

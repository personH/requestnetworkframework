package com.example.requestframework;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static String sessionid = new String();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView textView = (TextView) findViewById(R.id.tv);

        LoginRequest loginRequest = new LoginRequest("user", "123456");
        loginRequest.setOnCallbackListener(new BaseRequest.OnCallbackListener() {
            @Override
            public void onCallbakListener(int resultCode, Object resultObject) {

                if (resultCode == ConstantPool.HANDLER_SUC) {

                    String content = new String((byte[]) resultObject);
                    textView.setText(content);
                    System.out.println(content);

                } else if (resultCode == ConstantPool.HANDLER_UNKOWN_ERROR) {
                    //未知错误
                } else {
                    //错误代码
                }

                System.out.println("---------");

            }
        });
        loginRequest.request();
    }
}

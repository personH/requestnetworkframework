package com.example.requestframework;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author hcz
 * @version 1.0
 * @createtime 2015-03-06
 *
 * http请求的管理类
 *
 * 设置一个blockingqueue,用来存放所有的请求(这样多个请求是排队执行的)
 * 这个类是单例的,全局只有一个对象,要同步控制
 */
public class HttpRequestManager implements Runnable {

    private static HttpRequestManager httpRequestManager;
    private BlockingQueue<HttpRequest> baseRequestBlockingQueue;
    private ExecutorService executorService;

    /**
     *
     */
    public HttpRequestManager() {

        baseRequestBlockingQueue = new LinkedBlockingDeque<HttpRequest>();
        executorService = Executors.newSingleThreadExecutor();//网络请求单一线程 线程池.
        executorService.execute(this);
        
    }

    /**
     * @param httpRequest
     * @des 添加新的网络请求
     */
    public void addRequest(HttpRequest httpRequest) {
        this.baseRequestBlockingQueue.add(httpRequest);//add put offer 有什么区别吗?
    }

    public static HttpRequestManager getInstance() {
        if (httpRequestManager == null) {
            synchronized (HttpRequestManager.class) {//同步,控制单例
                if (httpRequestManager == null) {
                    httpRequestManager = new HttpRequestManager();
                }
            }
        }
        return httpRequestManager;
    }


    @Override
    public void run() {

        while (!baseRequestBlockingQueue.isEmpty()) {
            try {

                HttpRequest request = baseRequestBlockingQueue.take();
                request.startNetwork();//真正请求网络

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopTask();
    }

    /**
     * @param
     * @return void
     * @description 没有更多请求, 停止任务
     */
    public void stopTask() {
        executorService.shutdown();
        httpRequestManager = null;
    }
}

package ui;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IHttpService;
import burp.IRequestInfo;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/02/17/17:15
 * @Description:保存请求信息、敏感路径信息、请求次数等
 */
public class DetailData {

    //记录请求信息
    private final IHttpRequestResponse messageInfo;

    //记录敏感路径信息
    private final List<String> resultString;
    //记录请求次数
    private final int requestNum;

    public int getRequestNum() {
        return requestNum;
    }

    public IHttpRequestResponse getMessageInfo() {
        return messageInfo;
    }

    public List<String> getResultString() {
        return resultString;
    }

    public DetailData(int requestNum, IHttpRequestResponse messageInfo, List<String> resultString) {
        this.requestNum = requestNum;
        this.messageInfo = messageInfo;
        this.resultString = resultString;
    }

    //获取host信息
    public String getHost(){
        IHttpService httpService = messageInfo.getHttpService();
        return httpService.getHost();
    }

    //获取url信息
    public URL getUrl(){
        IRequestInfo requestInfo = BurpExtender.getHelpers().analyzeRequest(messageInfo);
        return requestInfo.getUrl();
    }
}

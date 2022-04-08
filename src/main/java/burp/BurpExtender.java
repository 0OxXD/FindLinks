package burp;

import matcher.PathMatcher;
import ui.DetailData;
import ui.DetailModel;
import ui.GUI;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/02/11/10:56
 * @Description:burp插件初始化
 */
public class BurpExtender implements IBurpExtender,ITab, IHttpListener{
    private static IBurpExtenderCallbacks callbacks;

    public static void setCallbacks(IBurpExtenderCallbacks callbacks) {
        BurpExtender.callbacks = callbacks;
    }

    //记录行号
    private int rowNum = 0;
    //请求次数
    private int requestNum = 1;

    //过滤图片等二进制响应包
    private List<String> filterResponse = Arrays.asList("PNG","JPEG","GIF","SVG","CSS","image");
    private static IExtensionHelpers helpers;

    public static IExtensionHelpers getHelpers() {
        return helpers;
    }

    private String extensionName = "FindLinks";
    private String version ="v0.1";
    public PrintWriter stdout;
    public PrintWriter stderr;
    public static GUI gui;
    private String banner =
                  "******************* " + extensionName + " *******************\n"
                + "********************* " + version + " **********************\n"
                + "-------------------------------------------------\n"
                + "anthor: kjsx\n"
                + "The extension loaded successfully!";

    public static IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        // keep a reference to our callbacks object
        setCallbacks(callbacks);

        // obtain an extension helpers object  获取helpers对象
        helpers = callbacks.getHelpers();

        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);
        // set our extension name 设置插件名称
        callbacks.setExtensionName(String.format("%s %s",extensionName,version));
        //gui初始化
        gui = new GUI();

        //注册，添加自定义tab,显示应用gui
        //导致 doRun.run（）在AWT事件分派线程上异步执行。
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                callbacks.addSuiteTab(BurpExtender.this);
            }
        });
        //如果没有注册，下面的processHttpMessage方法是不会生效的。处理请求和响应包的插件，这个应该是必要的
        callbacks.registerHttpListener(this);
        //输出banner信息
        stdout.println(banner);
    }

    @Override
    public String getTabCaption() {
        return extensionName;
    }

    @Override
    public Component getUiComponent() {
        return gui.getComponent(0);
    }


    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        //代理流量
        if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY){
            //对响应包进行处理
            if (!messageIsRequest){
                //getResponse获得的是字节序列
                IResponseInfo analyzedResponse = helpers.analyzeResponse(messageInfo.getResponse());
//                stdout.println("过滤前:" + analyzedResponse.getStatedMimeType());
                //不要图片等二进制信息
                if(!filterResponse.contains(analyzedResponse.getStatedMimeType())){
//                    stdout.println("过滤后:" + analyzedResponse.getStatedMimeType());
                    //获取响应包完整字符串
                    String resp = new String(messageInfo.getResponse());
                    //响应包是没有参数的概念的，大多需要修改的内容都在body中
                    int bodyOffset = analyzedResponse.getBodyOffset();
                    //截取响应包body字符串
                    String body = resp.substring(bodyOffset);
                    PathMatcher pathMatcher = new PathMatcher(body);
                    List<String> resultString = pathMatcher.getResultString();
                    //判读是否匹配有敏感字符串，有的话将请求响应放入map，没有则略过
                    if (!resultString.isEmpty()){
                        DetailData detailData = new DetailData(requestNum,messageInfo,resultString);
                        //将请求信息放入map里面
                        DetailModel.addDetailDataInfo(rowNum,detailData);
                        requestNum++;
                        rowNum++;
                    }
                }
            }
        }
    }


}

package ui;

import burp.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/02/14/14:57
 * @Description:
 */
public class DetailModel extends AbstractTableModel implements IMessageEditorController {
    //设置表格列名
    private static final String[] requestsInfoName = new String[] {"序号", "域名","URL"};
    /**
     * 字符串数组转列表
     */
    private static List<String> requestsInfoNameList = new ArrayList<>(Arrays.asList(requestsInfoName));

    //记录详细信息map
    private static LinkedHashMap<Integer,DetailData> detailDatas = new LinkedHashMap<>();

    public static LinkedHashMap<Integer, DetailData> getDetailDatas() {
        return detailDatas;
    }

    //当前详细信息
    private DetailData currentlyDisplayedDetailData;

    public DetailData getCurrentlyDisplayedDetailData() {
        return currentlyDisplayedDetailData;
    }

    public void setCurrentlyDisplayedDetailData(DetailData currentlyDisplayedDetailData) {
        this.currentlyDisplayedDetailData = currentlyDisplayedDetailData;
    }


    PrintWriter stdout;
    PrintWriter stderr;


    public IHttpRequestResponse getReqAndResInfosByRow(int row) {
        return getDetailDatas().get(row+1).getMessageInfo();
    }

    public List<String> getResultSensitiveInfosByRow(int row) {
        return getDetailDatas().get(row+1).getResultString();
    }



    public DetailModel() {
        try{
            stdout = new PrintWriter(BurpExtender.getCallbacks().getStdout(), true);
            stderr = new PrintWriter(BurpExtender.getCallbacks().getStderr(), true);
        }catch (Exception e){
            stdout = new PrintWriter(System.out, true);
            stderr = new PrintWriter(System.out, true);
        }
    }

    //将请求信息写入map
    public static void addDetailDataInfo(int requestNum,DetailData detailData) {
        detailDatas.put(requestNum,detailData);
    }

    @Override
    public IHttpService getHttpService() {
        DetailData displayedDetailData = getCurrentlyDisplayedDetailData();
        if(displayedDetailData==null) {
            return null;
        }
        return displayedDetailData.getMessageInfo().getHttpService();
    }

    @Override
    public byte[] getRequest() {
        DetailData displayedDetailData= getCurrentlyDisplayedDetailData();
        if(displayedDetailData==null) {
            return "".getBytes();
        }
        return displayedDetailData.getMessageInfo().getRequest();
    }

    @Override
    public byte[] getResponse() {
        DetailData displayedDetailData= getCurrentlyDisplayedDetailData();
        if(displayedDetailData==null) {
            return "".getBytes();
        }
        return displayedDetailData.getMessageInfo().getResponse();
    }

    @Override
    public int getRowCount() {
        return detailDatas.size();
    }

    @Override
    public int getColumnCount() {
        return requestsInfoNameList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DetailData detailData = detailDatas.get(rowIndex+1);
        if (columnIndex == requestsInfoNameList.indexOf("序号")) {
            return rowIndex+1;
        }
        if (columnIndex == requestsInfoNameList.indexOf("域名")) {
            return detailData.getHost();
        }
        if (columnIndex == requestsInfoNameList.indexOf("URL")) {
            return detailData.getUrl().toString();
        }
        return "";
    }

    /**
     * 返回列名称（表头名称），AbstractTableModel 中对该方法的实现默认是以
     * 大写字母 A 开始作为列名显示，所以这里需要重写该方法返回我们需要的列名。
     */
    @Override
    public String getColumnName(int column) {
        return requestsInfoName[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == requestsInfoNameList.indexOf("序号")) {
            return Integer.class;
        }
        if (columnIndex == requestsInfoNameList.indexOf("域名")) {
            return String.class;
        }
        if (columnIndex == requestsInfoNameList.indexOf("URL")) {
            return String.class;
        }
        return String.class;
    }
}

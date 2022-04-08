package ui;

import burp.*;
import misc.IndexedLinkedHashMap;
import misc.RowsToConsecutiveRows;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static IndexedLinkedHashMap<String,DetailData> detailDatas = new IndexedLinkedHashMap<>();

    public static IndexedLinkedHashMap<String, DetailData> getDetailDatas() {
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
        return getDetailDatas().get(row).getMessageInfo();
    }

    public List<String> getResultSensitiveInfosByRow(int row) {
        return getDetailDatas().get(row).getResultString();
    }



    public DetailModel() {
        try{
            stdout = new PrintWriter(BurpExtender.getCallbacks().getStdout(), true);
            stderr = new PrintWriter(BurpExtender.getCallbacks().getStderr(), true);
        }catch (Exception e){
            stdout = new PrintWriter(System.out, true);
            stderr = new PrintWriter(System.out, true);
        }
        /*
        		关于这个listener，主要的目标的是当数据发生改变时，更新到数据库。通过fireTableRowsxxxx来触发。
        * */
        /*this.addTableModelListener(new TableModelListener() {//表格模型监听
            @Override
            public void tableChanged(TableModelEvent e) {
                int type = e.getType();//获取事件类型(增、删、改等)
                int rowstart = e.getFirstRow();//获取触发事件的行索引，即是fireTableRowxxx中的2个参数。
                int rowend = e.getLastRow();
                stdout.println("ssssssss");
                if (type == TableModelEvent.DELETE) {//可以批量操作
                    //必须从高位index进行删除，否则删除的对象会和预期不一致！！！
                    for (int i = rowend; i >= rowstart; i--) {
                        detailDatas.remove(i);//删除tableModel中的元素。
                        stdout.println("deleted"+i);
                    }
                }
            }
        });*/
    }

    //将请求信息写入map
    public static void addDetailDataInfo(int rowNum,DetailData detailData) {
        detailDatas.put(String.valueOf(rowNum),detailData);
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
        DetailData detailData = detailDatas.get(rowIndex);
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

    /*
	//如果使用了tableModelListener,就需要注意：在监听事件中去执行具体动作，这里只是起通知作用！！！！
	尤其是改变了lineEntries数量的操作！index将发生改变。
	 */
    public void removeRows(int[] rows) {
        fireDeleted(rows);
    }

    //为了同时fire多个不连续的行，自行实现这个方法。
    public void fireDeleted(int[] rows) {
        List<int[]> slice = RowsToConsecutiveRows.slice(rows);
        //必须逆序，从高位index开始删除，否则删除的对象和预期不一致！！！
        //上面得到的顺序就是从高位开始的
        for(int[] sli:slice) {
//            stdout.println(Arrays.toString(sli));
            for (int i = sli[0]; i >= sli[sli.length-1]; i--) {
                detailDatas.remove(i);//删除tableModel中的元素。
//                stdout.println("deleted"+i);
            }
            //这里传入的值必须是低位数在前面，高位数在后面
            this.fireTableRowsDeleted(sli[sli.length-1],sli[0]);
        }
    }
}

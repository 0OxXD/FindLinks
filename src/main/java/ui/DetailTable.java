package ui;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IMessageEditor;
import matcher.PathMatcher;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/02/14/16:06
 * @Description:
 */
public class DetailTable extends JTable {
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private DetailModel detailModel;
    private JTextArea resultTextArea;

    public void setResultTextArea(JTextArea resultTextArea) {
        this.resultTextArea = resultTextArea;
    }

    public void setDetailModel(DetailModel detailModel) {
        this.detailModel = detailModel;
    }

    private JSplitPane detailSplitPane;

    public JSplitPane getDetailSplitPane() {
        return detailSplitPane;
    }

    public DetailModel getDetailModel() {
        return detailModel;
    }


    public DetailTable(DetailModel detailModel,JTextArea resultTextArea) {
        //设置表格数据模型
        setDetailModel(detailModel);
        //设置结果显示文本框
        setResultTextArea(resultTextArea);
        this.setModel(detailModel);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setBorder(new LineBorder(new Color(0, 0, 0)));
        RowSorter<DetailModel> sorter = new TableRowSorter<>(detailModel);
        this.setRowSorter(sorter);
        //允许选择行
        this.setRowSelectionAllowed(true);

        //第一列列宽
        this.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.getColumnModel().getColumn(1).setPreferredWidth(150);
        this.getColumnModel().getColumn(2).setPreferredWidth(700);
        //将表格自动调整的状态给关闭掉
//        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        this.setPreferredScrollableViewportSize(new Dimension(1500,320));
//        this.setColumnSelectionAllowed(true);
//        this.setCellSelectionEnabled(true);
        this.setSurrendersFocusOnKeystroke(true);
        //在table的空白区域显示右键菜单
        this.setFillsViewportHeight(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
//        FitTableColumns();
        detailSplitPane = getDetailPane();
    }

    @Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend){
        //根据行号获取请求响应包
        //convertRowIndexToModel转换为Model的索引，否则排序后索引不对应
        IHttpRequestResponse httpRequestResponse = this.getDetailModel().getReqAndResInfosByRow(convertRowIndexToModel(row));
        //根据行号获取敏感信息目录
        //convertRowIndexToModel转换为Model的索引，否则排序后索引不对应
        List<String> resultInfos = this.getDetailModel().getResultSensitiveInfosByRow(convertRowIndexToModel(row));
        requestViewer.setMessage(httpRequestResponse.getRequest(),true);
        responseViewer.setMessage(httpRequestResponse.getResponse(),false);
        this.getDetailModel().setCurrentlyDisplayedDetailData(DetailModel.getDetailDatas().get(convertRowIndexToModel(row)+1));
        showResultInTextArea(resultInfos);
        super.changeSelection(row, col, toggle, extend);
    }

    private JSplitPane getDetailPane() {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        JTabbedPane requestPanel = new JTabbedPane();
        splitPane.setLeftComponent(requestPanel);

        JTabbedPane responsePanel = new JTabbedPane();
        splitPane.setRightComponent(responsePanel);

        requestViewer = BurpExtender.getCallbacks().createMessageEditor(this.getDetailModel(), false);
        responseViewer = BurpExtender.getCallbacks().createMessageEditor(this.getDetailModel(), false);
        requestPanel.addTab("Request", requestViewer.getComponent());
        responsePanel.addTab("Response", responseViewer.getComponent());

        return splitPane;
    }

    //在文本框显示结果
    public void showResultInTextArea(List<String> resultInfos){
        //先清空内容再显示
        resultTextArea.setText("");
        for (String s:resultInfos){
            //每个路径换行
            resultTextArea.append(s+"\n");
        }
    }
}

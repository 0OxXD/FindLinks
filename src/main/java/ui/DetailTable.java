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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.Arrays;
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
        setModel(detailModel);
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
//        this.setModel(detailModel);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        this.setBorder(new LineBorder(new Color(0, 0, 0)));
        //排序不会搞，有很多bug，暂时不使用
//        RowSorter<DetailModel> sorter = new TableRowSorter<>(detailModel);
//        this.setRowSorter(sorter);
        //允许选择行
        this.setRowSelectionAllowed(true);

        //第一列列宽
        this.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.getColumnModel().getColumn(1).setPreferredWidth(150);
        this.getColumnModel().getColumn(2).setPreferredWidth(700);


        this.setSurrendersFocusOnKeystroke(true);
        //在table的空白区域显示右键菜单
        this.setFillsViewportHeight(true);
//        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        this.addMouseListener( new MouseAdapter()
        {
            @Override//title表格中的鼠标右键菜单
            public void mouseReleased( MouseEvent e ){//在windows中触发,因为isPopupTrigger在windows中是在鼠标释放是触发的，而在mac中，是鼠标点击时触发的。
                //https://stackoverflow.com/questions/5736872/java-popup-trigger-in-linux
                if ( SwingUtilities.isRightMouseButton( e )){
                    if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                        //getSelectionModel().setSelectionInterval(rows[0], rows[1]);
                        int[] rows = getSelectedRows();
                        int col = ((DetailTable) e.getSource()).columnAtPoint(e.getPoint()); // 获得列位置
                        int modelCol = DetailTable.this.convertColumnIndexToModel(col);
                        if (rows.length>0){
                            int[] modelRows = SelectedRowsToModelRows(rows);
                            new DetailTableMenu(DetailTable.this, modelRows).show(e.getComponent(), e.getX(), e.getY());
                        }else{//在table的空白处显示右键菜单
                            //https://stackoverflow.com/questions/8903040/right-click-mouselistener-on-whole-jtable-component
                            //new LineEntryMenu(_this).show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) { //在mac中触发
                mouseReleased(e);
            }
        });
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
        this.getDetailModel().setCurrentlyDisplayedDetailData(DetailModel.getDetailDatas().get(convertRowIndexToModel(row)));
        showResultInTextArea(resultInfos);
        super.changeSelection(row, col, toggle, extend);
    }
    //下面显示请求响应信息的面板
    private JSplitPane getDetailPane() {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        JTabbedPane requestPanel = new JTabbedPane();
        JTabbedPane responsePanel = new JTabbedPane();

        requestViewer = BurpExtender.getCallbacks().createMessageEditor(this.getDetailModel(), false);
        responseViewer = BurpExtender.getCallbacks().createMessageEditor(this.getDetailModel(), false);
        requestPanel.addTab("Request", requestViewer.getComponent());
        responsePanel.addTab("Response", responseViewer.getComponent());
        splitPane.setLeftComponent(requestPanel);
        splitPane.setRightComponent(responsePanel);

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

    //将选中的行（图形界面的行）转换为Model中的行数（数据队列中的index）.因为图形界面排序等操作会导致图像和数据队列的index不是线性对应的。
    public int[] SelectedRowsToModelRows(int[] SelectedRows) {

        int[] rows = SelectedRows;
        for (int i=0; i < rows.length; i++){
            rows[i] = convertRowIndexToModel(rows[i]);//转换为Model的索引，否则排序后索引不对应〿
        }
        Arrays.sort(rows);//升序

        return rows;
    }
}

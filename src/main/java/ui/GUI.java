package ui;

import burp.BurpExtender;
import burp.IHttpService;
import burp.IMessageEditor;
import burp.IMessageEditorController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/02/11/11:21
 * @Description:
 */
public class GUI extends JFrame {
    private JPanel jPanel;
    private DetailTable detailTable;
    private DetailModel detailModel;
    private JScrollPane targetScrollPane;
    private JScrollPane resultScrollPane;
    private JSplitPane jSplitPane;
    private JSplitPane detailPane;
    private JSplitPane tableAndTextAreaPane;
    private JTextArea jTextArea;
    private JTabbedPane resultTabbedPane;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;

    public GUI(){
        //创建面板
        jPanel=new JPanel();
        //添加面板到容器
        setContentPane(jPanel);
        //设置边界
        jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        //设置布局
        jPanel.setLayout(new BorderLayout(0, 0));

        //存放目标敏感目录
        targetScrollPane = new JScrollPane();
//        targetScrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));

        //结果文本框
        jTextArea=new JTextArea();
        resultScrollPane=new JScrollPane();
        resultScrollPane.setViewportView(jTextArea);

        //表格
        detailModel = new DetailModel();
        detailTable = new DetailTable(detailModel,jTextArea);
        targetScrollPane.setViewportView(detailTable);
        targetScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        resultTabbedPane=new JTabbedPane();
        resultTabbedPane.addTab("敏感信息：",resultScrollPane);
        //禁止拖动面板
//        resultTabbedPane.setEnabled(false);

        //中间的大模块，一分为二
        jSplitPane = new JSplitPane();
        jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //表格和文本框
        tableAndTextAreaPane = new JSplitPane();
        tableAndTextAreaPane.setResizeWeight(0.4);
        tableAndTextAreaPane.setLeftComponent(targetScrollPane);
        tableAndTextAreaPane.setRightComponent(resultTabbedPane);
        jSplitPane.setTopComponent(tableAndTextAreaPane);
        //增加请求包返回包显示
        detailPane=detailTable.getDetailSplitPane();
        jSplitPane.setBottomComponent(detailPane);
        jPanel.add(jSplitPane, BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //自定义符合burp的ui风格
                BurpExtender.getCallbacks().customizeUiComponent(jPanel);
            }
        });
    }

}

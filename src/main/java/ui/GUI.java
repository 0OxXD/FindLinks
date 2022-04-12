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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
    private JSplitPane checkboxAndTextAreaPane;
    private JCheckBox switchCheckBox;
    private JTextArea jTextArea;
    private JTabbedPane resultTabbedPane;
    //默认开启
    private boolean onoff = true;

    public boolean isOnoff() {
        return onoff;
    }

    public GUI(){
        //创建面板
        jPanel=new JPanel();
        //添加面板到容器
        setContentPane(jPanel);
        //设置边界
        jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        //设置布局
        jPanel.setLayout(new BorderLayout(0, 0));


//        targetScrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));

        //结果文本框
        jTextArea=new JTextArea();
        resultScrollPane=new JScrollPane();
        resultScrollPane.setViewportView(jTextArea);

        //表格
        detailModel = new DetailModel();
        detailTable = new DetailTable(detailModel,jTextArea);
        //存放目标敏感目录
        targetScrollPane = new JScrollPane();
        targetScrollPane.setViewportView(detailTable);
//        targetScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


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


        //复选框和文本框
        checkboxAndTextAreaPane = new JSplitPane();
        checkboxAndTextAreaPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //是否开启插件，默认开启
        switchCheckBox = new JCheckBox("启用插件",true);
        switchCheckBox.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(switchCheckBox.isSelected()){
                    onoff = true;
                }else{
                    onoff = false;
                }
            }
        });
        checkboxAndTextAreaPane.setTopComponent(switchCheckBox);
        checkboxAndTextAreaPane.setBottomComponent(resultTabbedPane);

        tableAndTextAreaPane.setRightComponent(checkboxAndTextAreaPane);

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

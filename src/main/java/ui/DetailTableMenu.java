package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/03/10/19:49
 * @Description:
 */
public class DetailTableMenu extends JPopupMenu {
    private static DetailTable detailTable;

    public static DetailTable getDetailTable() {
        return detailTable;
    }

    public static void setDetailTable(DetailTable detailTable) {
        DetailTableMenu.detailTable = detailTable;
    }

    public DetailTableMenu(final DetailTable detailTable, final int[] modelRows){
        setDetailTable(detailTable);
        /**
         * 单纯删除记录,不做其他修改
         */
        JMenuItem removeItem = new JMenuItem(new AbstractAction("删除记录") {//need to show dialog to confirm
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int result = JOptionPane.showConfirmDialog(null,"确定删除?");
                if (result == JOptionPane.YES_OPTION) {
                    detailTable.getDetailModel().removeRows(modelRows);
                }else {
                    return;
                }
            }
        });
        removeItem.setToolTipText("删除记录");

        this.add(removeItem);
    }
}

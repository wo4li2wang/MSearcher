package com.madao.mSearcher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListShow extends JFrame
{
    JTable   jTable;
    DefaultTableModel tmd;
    JPopupMenu popupMenu=new JPopupMenu();
    final JScrollPane scrollPane = new JScrollPane();
    static int selected=0;

    ListShow(String str)
    {
        super(str);
        setSize(650,250);
        setLayout(new BorderLayout());
        addComponent();
        setVisible(true);
        setLocationRelativeTo(null);
    }
    void addElement(Object [] o){
        tmd.addRow(o);
    }

    //组件add生成……
    public void addComponent()
    {
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        String[] column={"名字","路径","大小","修改时间","匹配"};
        String[][] row=new String[0][column.length];
        tmd=new DefaultTableModel(row,column);
        jTable=new JTable(tmd);
        RowSorter sorter = new TableRowSorter(tmd);
        jTable.setRowSorter(sorter);
        scrollPane.setViewportView(jTable);
        JMenuItem item1=new JMenuItem("定位");
        popupMenu.add(item1);

        item1.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e) {
                try {
                Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL "+ "Explorer.exe /select," + jTable.getValueAt(selected,1));
                }catch (Exception e2){
                }
            }
        });
        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int row = jTable.rowAtPoint(e.getPoint());
                    jTable.clearSelection();
                    jTable.setRowSelectionInterval(row,row);
                    selected=row;
                    popupMenu.show(getContentPane(),e.getX(), e.getY());
                }
            }
        });
    }
    }



package com.madao.mSearcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Gui extends JFrame implements ActionListener
{
    JLabel folderText=new JLabel("搜索的目录:");
    JLabel filenameText=new JLabel("搜索的文件名:");
    JLabel keywordText=new JLabel("搜索的关键字:");

    JTextField folder=new JTextField(40);
    JTextField filename=new JTextField(40);
    JTextField keyword=new JTextField(40);
    JTextField encoding=new JTextField(40);
    JCheckBox regex=new JCheckBox("正则式");
    JButton click=new JButton("搜");
    JButton choose=new JButton("浏览");
    JCheckBox ignore=new JCheckBox("无视大小写");
    JCheckBox showFolder=new JCheckBox("匹配文件名");
    JLabel encodingText=new JLabel("使用的编码:");
    JFileChooser fc=new JFileChooser();
    File f=null;
    GridBagLayout g=new GridBagLayout();
    GridBagConstraints c=new GridBagConstraints();
    Gui(String str)
    {
        super(str);
        setSize(650,250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(g);
        addComponent();
        setVisible(true);
        setLocationRelativeTo(null);
    }
    public void addComponent()
    {
        add(g,c,folderText,0,0,1,1);
        add(g,c,filenameText,0,1,1,1);
        add(g,c,keywordText,0,2,1,1);
        add(g,c,encodingText,0,3,1,1);
        add(g,c,folder,1,0,1,1);
        add(g,c,choose,2,0,1,1);
       choose.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               int temp=fc.showOpenDialog(null);
               if(temp==JFileChooser.APPROVE_OPTION){
                   f=fc.getSelectedFile();
                   String temp2=f.getPath();
                   String getter=folder.getText();
                   if (!getter.contains(temp2))
                   folder.setText(getter+"&"+temp2);
               }
           }
       });
        add(g, c, filename, 1, 1, 1, 1);
        add(g, c, keyword, 1, 2, 1, 1);
        add(g, c, encoding, 1, 3, 1, 1);
        encoding.setText("UTF8&GBK");
        add(g,c,regex,0,4,1,1);
        add(g, c, showFolder, 0, 5, 1, 1);
        add(g,c,ignore,1,4,1,1);
        add(g,c,click,1,6,1,1);
        click.addActionListener(this);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
        try {
            File confFile=new File("search.conf");
            if(!confFile.exists())throw new Exception();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(confFile));
            String line;
            line=bufferedReader.readLine();
            if (line!=null)filename.setText(unicode2String(line));
            line=bufferedReader.readLine();
            if (line!=null)folder.setText(unicode2String(line));
            line=bufferedReader.readLine();
            if (line!=null)keyword.setText(unicode2String(line));
            line=bufferedReader.readLine();
            if (line!=null)encoding.setText(unicode2String(line));
            line=bufferedReader.readLine();
            if (line!=null&&line.equals("true"))regex.setSelected(true);
            line=bufferedReader.readLine();
            if (line!=null&&line.equals("true"))ignore.setSelected(true);
            line=bufferedReader.readLine();
            if (line!=null&&line.equals("true"))showFolder.setSelected(true);
            bufferedReader.close();
        }catch (Exception e){
        }
    }
    public void add(GridBagLayout g,GridBagConstraints c,JComponent jc,int x ,int y,int gw,int gh)
    {
        c.gridx=x;
        c.gridy=y;
        c.anchor=GridBagConstraints.WEST;
        c.gridwidth=gw;
        c.gridheight=gh;
        g.setConstraints(jc,c);
        add(jc);
    }
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        StringBuffer command=new StringBuffer();
        boolean getRegex=regex.isSelected();
        command.append("-f");
        command.append(notRegex(getRegex,folder.getText()));
        command.append("-n");
        command.append(notRegex(getRegex,filename.getText()));
        command.append("-k");
        command.append(notRegex(getRegex, keyword.getText()));
        if (ignore.isSelected())command.append("-i");
        if(showFolder.isSelected())command.append("-s");
        Main.listShow=new ListShow("结果");
        try {
            File confFile=new File("search.conf");
            if(!confFile.exists())confFile.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(confFile));
            bufferedWriter.write(string2Unicode(filename.getText()));
            bufferedWriter.newLine();
            bufferedWriter.write(string2Unicode(folder.getText()));
            bufferedWriter.newLine();
            bufferedWriter.write(string2Unicode(keyword.getText()));
            bufferedWriter.newLine();
            bufferedWriter.write(string2Unicode(encoding.getText()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(regex.isSelected()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(ignore.isSelected()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(showFolder.isSelected()));
            bufferedWriter.close();
        }
        catch(Exception e){
        }
            Main.run(command.toString());
        //run console
    }
    private String notRegex(boolean regex,String input){
        input=input.replaceAll("\\.","\\\\.");
        if(!regex)
            return input.replaceAll("\\*",".\\*?");
        return input;
    }
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }
    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }

}

package com.madao.mSearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO 新的计划，必须有的用|连接好了，开始想用参数，但考虑到 文件名、路径、关键字不能同时用一个--must限制成必须把？所以
//a&b 表示a或者有b
//a|b 表示必须有b
//当然也可以直接 |b|c^d^e ,就是说第一个默认当成 & 如果第一个项为空
// 比如 |b
// 就无视第一个.不放到and组里

/**
 * Created by Administrator on 2014/11/5.
 * 2014-11-6 10:14:13
 * English search pass
 * regex limit pass
 * keyword with '^'(not) limit pass
 * 2014-11-6 10:45:53
 * support Chinese(GBK)
 * 2014-11-6 10:50:45
 * support Chinese(UTF-8),don't need which encoding should be selected
 * 2014-11-6 11:17:23
 * can list the folder if name has the key word
 *
 *
 * -f folder to search,without this param,start gui
 * -k keyword,without this param will match the filename
 * -n filename,support regex, default value is .*(match any file)
 * -s show directory or file name which accord with the keyword
 * -i ignore case
 * -e limit the encoding
 */

public class Main {
    static ArrayList<String> outputFileList = new ArrayList<String>();//output files to search
    static ArrayList<String> outputFolderList = new ArrayList<String>();//output folders
    static ArrayList<String> outputFileNameList = new ArrayList<String>();//output file name
    static ArrayList<String> encoding = new ArrayList<String>();//output file name
    static int cast=Pattern.UNICODE_CASE;// case sensitive or not
    static  String keyword="";

    static {
    encoding.add("GBK");
    encoding.add("UTF8");
    }//default support encoding


    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++)
            sb.append(args[i]);
        String args2 = sb.toString();

        if(!(args2.contains("-f"))){

        //gui
        }
        else
        {

        String folder = processUtil("-f[^-]*", args2);

            String name;
            if (args2.contains("-n"))
                name = processUtil("-n[^-]*", args2);
            else
                name = ".*";

        if (args2.contains("-k"))
            keyword = processUtil("-k[^-]*", args2);
        else
            keyword = "";

         if (args2.contains("-i"))  cast=Pattern.CASE_INSENSITIVE;

        //process the args

        ConfigFile cf = new ConfigFile(name, folder, keyword);
        cf.process();
        //get the rules in ConfigFile to filter the file

         if(args2.contains("-e")){
             String  coding = processUtil("-e[^-]*", args2);//TODO
             if(coding!="") {
                 encoding.clear();
                 encoding.addAll(cf.processUtil("^[^&^\\|^\\^]*", coding, false));
                 encoding.addAll(cf.processUtil("&[^&^\\|^\\^]*", coding, true));
             }
         }
        Iterator<String> iterator = cf.getPathSplit().getAndItem().iterator();//get folders to search

        while (iterator.hasNext()) {
            File f;
            f=new File(iterator.next());
            if(f==null)break;
            ergodic(f, cf);
        }

        if (outputFileList.size() != 0) {
            System.out.println("the file accord with you require:");
            for (String e : outputFileList) {
                System.out.println('\t' + e);
            }
        } else {
            System.out.println("no file matches");
        }
        System.out.println();

        if (args2.contains("-s")) {
            if (outputFolderList.size() != 0) {
                System.out.println("the directory name which accord with the key word you want");
                for (String e : outputFolderList) {
                    System.out.println('\t' + e);
                }
                System.out.println();
                System.out.println("the file name which accord with the key word you want");
                for (String e : outputFileNameList) {
                    System.out.println('\t' + e);
                }
            } else System.out.println("no file name or directory name accord with the key word you want");
        }
    }

}

    /**
     * just a util to analysis args ,please ignore
     * */
    private static String processUtil(String regex,String input){

        if(input==null||input=="")return "";

        Pattern p = Pattern.compile(regex, cast);
        Matcher m = p.matcher(input);

        String temp;

        try {
            m.find();
            temp=m.group();
            if(temp.contains("-")&&temp.length()>2)
            temp=temp.substring(2);
            else
               temp="";
        }catch (IllegalStateException e){
            e.printStackTrace();
            System.out.println();
            System.out.println("提示：windows的终端里特殊符号需要用逃逸字符表示 ，比如 '&' 用 '^&' ,'|'用 '^|'，'^'用 '^^',linux下需要用'\\&'来表示'&' ");
            temp="";
        }
        return temp;
    }

    /**
     * if input contains the content of the regex
     * */
    private static boolean contains(String regex,String input){
        Pattern p = Pattern.compile(regex, cast);
        Matcher m = p.matcher(input);
        return m.find();
    }

    /**
     * Ergodic the specificed folder
     * very important method
     * */
    private static void ergodic(File file,ConfigFile cf) {

        Iterator<String> iterator=cf.getPathSplit().getNotItem().iterator();
        boolean b=true;
        while (iterator.hasNext())
            b&=match(iterator.next(),file.getAbsolutePath());
        //if the path is excluded ,return this ergodic
        if (!b)return;

        //'or' in folder is a reserve word,perhaps exclude the file but continue ergodic
/*
        iterator=cf.getPathSplit().getOrItem().iterator();
        b=true;
        while (iterator.hasNext())
            b&=match(iterator.next(),file.getAbsolutePath());
*/

        File[] files = file.listFiles();

        if (files != null&&files.length>0) {
            for (File f : files) {
                if(f.isFile()) {
                    if (hasKeyWord(f.getName(), cf.getKeyWordSplit().getAndItem(), cf.getKeyWordSplit().getNotItem()))
                        outputFileNameList.add(f.getAbsolutePath());

                    iterator = cf.getFileNameSplit().getAndItem().iterator();
                    b = false;// name is matcher?

                    //'not' is prior than 'and','or' is a reserve word
                    while (iterator.hasNext()) {
                        if (match(iterator.next(), f.getName())) {
                            b = true;
                            break;//if any matches
                        }
                    }

                    iterator = cf.getFileNameSplit().getNotItem().iterator();
                    while (iterator.hasNext()) {
                        if (match(iterator.next(), f.getName())) {
                            b = false;
                            break;//if any matches
                        }
                    }
                    if (b){
                        if (isBinary(f)&&keyword!="") continue;//ignore binary file if the

                        try {
                            b = hasKeyWord(f, cf.getKeyWordSplit().getAndItem(), cf.getKeyWordSplit().getNotItem());
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;//Unknown Exception?IO Exception?
                        }

                        if (b) outputFileList.add(f.getAbsolutePath());

                    }
                }
                   else if(f.isDirectory()) {//some maybe neither file nor directory
                        if (hasKeyWord(f.getName(), cf.getKeyWordSplit().getAndItem(), cf.getKeyWordSplit().getNotItem()))
                            outputFolderList.add(f.getAbsolutePath());
                        //if file name match the keyword,also record
                    String[] list=null;
                    int len=0;
                    try {

                        list=f.list();
                        len=list.length;
                    }catch (Exception e){
                        System.err.println(f.getPath() + " access denied ......");
                        e.printStackTrace();
                        //case of Access denied
                    }
                        if (len != 0)//ergodic the directory
                            ergodic(f, cf);
                    }
                }
            }
        }


/**
 * check the regex
 * @param rule the regex
 * @param input input text
 * */
    private static boolean match(String rule,String input){
        Pattern p = Pattern.compile(rule,cast);
        Matcher m = p.matcher(input);
        return m.matches();
    }


    /**
     * judge the file whether has keyword,support GBK UTF-8
     * @param f the file to search key word
     * @param keyword keyword to search
     * @param ignoreWord if matches any of this word,the file is illegal
     */
    private static boolean hasKeyWord (File f, ArrayList<String> keyword, ArrayList<String> ignoreWord) throws Exception {

        if (ignoreWord.size() == 0&&keyword.size() == 0) return true;

        FileInputStream fileInputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        String line;
        boolean include=false;

        for (String coding : encoding) {
            //test each encoding
            fileInputStream=new FileInputStream(f);
            inputStreamReader=new InputStreamReader(fileInputStream,coding);//is GBK?
            bufferedReader=new BufferedReader(inputStreamReader);
            while ((line=bufferedReader.readLine())!=null) {
                //not array
                if (ignoreWord.size() != 0) {
                    for (String e : ignoreWord) {
                        if (contains(e, line)) {
                            bufferedReader.close();
                            fileInputStream.close();
                            inputStreamReader.close();
                            return false;//exclude key word in file,this is prior than key word
                        }
                    }
                }
                //or array
                int [] orArray=new int[1];//TODO



                //and array
                if (keyword.size() != 0) {
                    for (String e : keyword) {
                        if (contains(e, line)) {//match any key word
                            include = true;// don't stop continue search,don't return,because perhaps has other ignore key word maybe.
                            break;//already has one key word
                        }
                    }
                }

            }
            if (include) {
                bufferedReader.close();
                fileInputStream.close();
                inputStreamReader.close();
                return true;
            }
        }

        if (bufferedReader!=null)bufferedReader.close();
        if (fileInputStream!=null)fileInputStream.close();
        if (inputStreamReader!=null)inputStreamReader.close();
        return false;
    }
    /**
     * judge the folder name whether has keyword
     * @param f the folder name
     * @param keyword keyword to search
     * @param ignoreWord if matches any of this word,the file is illegal
     */
    private static boolean hasKeyWord (String f, ArrayList<String> keyword, ArrayList<String> ignoreWord){

        if (ignoreWord.size() == 0&&keyword.size() == 0) return true;

            if(ignoreWord.size()!=0) {
                for (String e : ignoreWord) {
                    if (contains(e, f)) {
                        return false;//exclude key word in file,this is prior than key word
                    }
                }
            }

        if(keyword.size()!=0) {
            for (String e : keyword) {
                if (contains(e, f)) {//match any key word
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * judge if the file is binary file
     * */
    private static boolean isBinary(File file) {
        boolean isBinary = false;
        try {
            FileInputStream fin = new FileInputStream(file);
            long len = file.length();
            for (int j = 0; j < (int) len; j++) {
                int t = fin.read();
                if (t < 32 && t != 9 && t != 10 && t != 13) {
                    isBinary = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBinary;
    }

}


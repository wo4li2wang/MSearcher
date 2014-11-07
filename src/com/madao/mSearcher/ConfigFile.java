package com.madao.mSearcher;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2014/11/5.
 *
 * accept the config and return the conclusion
 * include the path filename and keyword
 *
 * use RegEx
 *
 * 2014-11-5 15:48:35 can return the split strings
 */
public class ConfigFile {

    private String fileNameAccept;// accept file name serials ,support Rregex by boolean fileBoolean,input
    private TypeDo fileNameSplit;// Split the fileNameAccept.output.

    private String keyWordsAccept;// key word , as former,input
    private TypeDo keyWordSplit;// Split the keyWordsAccept.output

    private String pathAccept;// search path , as former,input
    private TypeDo pathSplit;// Split the pathAccept.output
/**
 * @param fileNameAccept the file name,use '&','|','^' to divide. support Regex
 * @param pathAccept  the path to search the file ,use '&','|','^' to divide. support Regex
 * @param keyWordsAccept  the key word to search ,use '&','|','^' to divide. support Regex,support GBK UTF-8.,
 * */

     public ConfigFile(String fileNameAccept,String pathAccept, String keyWordsAccept) {
        this.fileNameAccept = fileNameAccept;
        this.keyWordsAccept = keyWordsAccept;
        this.pathAccept = pathAccept;
        fileNameSplit=new TypeDo(new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
        keyWordSplit=new TypeDo(new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
        pathSplit=new TypeDo(new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
    }

/**
 * just a util ,please ignore
 * */
    ArrayList<String> processUtil(String regex,String input,boolean b){
        ArrayList<String> temp=new ArrayList<String>();
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        while (m.find()) {
            String temp2=m.group();
            if(b)
                temp2 = temp2.substring(1);
            if(temp2.length()>1)
                temp.add(temp2);
        }
        return temp;
    }

    /**
     * split the key words
     * */
    public void process()
    {
        fileNameSplit.getAndItem().addAll(processUtil("^[^&^\\|^\\^]*",fileNameAccept,false));
        fileNameSplit.getAndItem().addAll(processUtil("&[^&^\\|^\\^]*", fileNameAccept, true));
// above append the and item of fileNameAccept
        fileNameSplit.getOrItem().addAll(processUtil("\\|[^&^\\|^\\^]*",fileNameAccept,true));
        fileNameSplit.getNotItem().addAll(processUtil("\\^[^&^\\|^\\^]*", fileNameAccept, true));

        keyWordSplit.getAndItem().addAll(processUtil("^[^&^\\|^\\^]*", keyWordsAccept, false));
        keyWordSplit.getAndItem().addAll(processUtil("&[^&^\\|^\\^]*", keyWordsAccept, true));
        keyWordSplit.getOrItem().addAll(processUtil("\\|[^&^\\|^\\^]*",keyWordsAccept,true));
        keyWordSplit.getNotItem().addAll(processUtil("\\^[^&^\\|^\\^]*", keyWordsAccept,true));


        pathSplit.getAndItem().addAll(processUtil("^[^&^\\|^\\^]*",pathAccept,false));
        pathSplit.getAndItem().addAll(processUtil("&[^&^\\|^\\^]*",pathAccept,true));
        pathSplit.getOrItem().addAll(processUtil("\\|[^&^\\|^\\^]*",pathAccept,true));
        pathSplit.getNotItem().addAll(processUtil("\\^[^&^\\|^\\^]*", pathAccept,true));

    }

    public TypeDo getFileNameSplit() {
        return fileNameSplit;
    }

    public TypeDo getKeyWordSplit() {
        return keyWordSplit;
    }

    public TypeDo getPathSplit() {
        return pathSplit;
    }

    /**
 * a test , for example
 * */
    public static void main(String[] args) {
        ConfigFile cf=new ConfigFile("asd&fg|hjk^l&ddd|werg^eg&egw&ge","D:\\文档 (K)\\Documents\\软件|K:\\","asd&fg|hjk^l");
        cf.process();
        System.out.println();
    }

}

package com.madao.mSearcher;

import java.io.File;

/**
 * Created by Administrator on 2014/11/7.
 */
public class demo {

    public static void main(String[] args) {
        File f=new File("D:\\Don't Starve");
        File[] files = f.listFiles();
        for ( File e : files) {
        System.out.println(e.getAbsolutePath());
        }

    }

}

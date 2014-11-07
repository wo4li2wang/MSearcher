package com.madao.mSearcher;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/11/5.
 *
 * save the keyword and filename Arrays
 *
 */
public class TypeDo {
    private ArrayList<String> andItem;//each item is a regex
    private ArrayList<String> orItem;
    private ArrayList<String> notItem;

    public TypeDo(ArrayList<String> andItem, ArrayList<String> orItem, ArrayList<String> notItem) {
        this.andItem = andItem;
        this.orItem = orItem;
        this.notItem = notItem;
    }

    public ArrayList<String> getAndItem() {
        return andItem;
    }

    public ArrayList<String> getOrItem() {
        return orItem;
    }

    public ArrayList<String> getNotItem() {
        return notItem;
    }
}

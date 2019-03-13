package com.zxh.dormMG.dto;

import java.util.List;

public class DataTableDto<T> {
//    //当前页
//    private int sEcho;
//    //总数
//    private int iTotalRecords;
//    //筛选后总数
//    private int iTotal;
//    //返回的集合
    private List<T> aaData;

    public DataTableDto() {
    }

    public DataTableDto(List<T> aaData) {
        this.aaData = aaData;
    }

//    public int getsEcho() {
//        return sEcho;
//    }
//
//    public void setsEcho(int sEcho) {
//        this.sEcho = sEcho;
//    }
//
//    public int getiTotalRecords() {
//        return iTotalRecords;
//    }
//
//    public void setiTotalRecords(int iTotalRecords) {
//        this.iTotalRecords = iTotalRecords;
//    }
//
//    public int getiTotal() {
//        return iTotal;
//    }
//
//    public void setiTotal(int iTotal) {
//        this.iTotal = iTotal;
//    }

    public List<T> getAaData() {
        return aaData;
    }

    public void setAaData(List<T> aaData) {
        this.aaData = aaData;
    }
}

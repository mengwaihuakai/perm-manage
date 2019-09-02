package com.solid.subscribe.web.perm.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageView<T> implements Serializable{
    /*数据结果*/
    private List<T> result = Collections.emptyList();
    /*总记录数*/
    private long totalSize;
    /*图标数据*/
    private String chartData;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getChartData() {
        return chartData;
    }

    public void setChartData(String chartData) {
        this.chartData = chartData;
    }
}

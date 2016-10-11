/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.featurepoints;

/**
 *
 * @author xferkova
 */
public class TableData {
    private String[] header;
    private String[][] tableData;

    public TableData(String[] header, String[][] tableData) {
        this.header = header;
        this.tableData = tableData;
    }
    
    public TableData(){}

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    public String[][] getTableData() {
        return tableData;
    }

    public void setTableData(String[][] tableData) {
        this.tableData = tableData;
    }
    
    
}

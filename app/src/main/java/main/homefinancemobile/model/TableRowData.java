package main.homefinancemobile.model;

public class TableRowData {
    private String column;
    private String value;

    public TableRowData(String column, String val) {
        this.column = column;
        this.value = val;
    }

    public void setColumn(String column) {
        this.column = column;
    }
    public void setValue(String val) {
        this.value = val;
    }
    public String getColumn() {
        return this.column;
    }
    public String getValue() {
        return this.value;
    }
}

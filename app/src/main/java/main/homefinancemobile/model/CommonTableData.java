package main.homefinancemobile.model;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.homefinancemobile.common.SimpleIdNameObj;


public class CommonTableData {
    private String id;
    private String name;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CommonTableData() {}

    public CommonTableData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<TableRowData> convertToTableRowData() {
        List<TableRowData> tableRowData = new ArrayList<>();
        return tableRowData;
    }

    public SimpleIdNameObj convertToSimpleIdNameObj() {
        return new SimpleIdNameObj(this.getId(), this.getName());
    }
}

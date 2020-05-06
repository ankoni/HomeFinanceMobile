package main.homefinancemobile.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.homefinancemobile.utils.ParseDate;

public class RecordFormData {
    private String id;
    private Float amount;
    private String category;
    private String account;
    private Date date;

    public RecordFormData(String id, Float amount, String category, String account, Date date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.account = account;
        this.date = date;
    }

    public List<TableRowData> convertToTableRowData() {
        List<TableRowData> tableRowData = new ArrayList<>();
        tableRowData.add(new TableRowData("amount", String.valueOf(this.getAmount())));
        tableRowData.add(new TableRowData("category", this.getCategory()));
        tableRowData.add(new TableRowData("account", this.getAccount()));
        tableRowData.add(new TableRowData("date", ParseDate.parseDateToString(this.getDate())));
        return tableRowData;
    }

    public String getId() {
        return this.id;
    }

    public Float getAmount() {
        return this.amount;
    }

    public String getAccount() {
        return this.account;
    }

    public String getCategory() {
        return this.category;
    }

    public Date getDate() {
        return this.date;
    }
}

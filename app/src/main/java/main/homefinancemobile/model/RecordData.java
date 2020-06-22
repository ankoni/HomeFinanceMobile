package main.homefinancemobile.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.utils.ParseDate;

public class RecordData extends CommonTableData {
    private Float amount;
    private SimpleIdNameObj category;
    private SimpleIdNameObj account;
    private Date date;

    public RecordData(String id, Float amount, SimpleIdNameObj category, SimpleIdNameObj account, Date date) {
        super(id, null);
        this.amount = amount;
        this.category = category;
        this.account = account;
        this.date = date;
    }

    public List<TableRowData> convertToTableRowData() {
        List<TableRowData> tableRowData = new ArrayList<>();
        tableRowData.add(new TableRowData("amount", String.valueOf(this.getAmount())));
        tableRowData.add(new TableRowData("category", this.getCategory().getName()));
        tableRowData.add(new TableRowData("account", this.getAccount().getName()));
        tableRowData.add(new TableRowData("date", ParseDate.parseDateToString(this.getDate())));
        return tableRowData;
    }

    public Float getAmount() {
        return this.amount;
    }

    public SimpleIdNameObj getAccount() {
        return this.account;
    }

    public SimpleIdNameObj getCategory() {
        return this.category;
    }

    public Date getDate() {
        return this.date;
    }
}

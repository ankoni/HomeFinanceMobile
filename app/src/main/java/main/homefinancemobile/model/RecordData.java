package main.homefinancemobile.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.homefinancemobile.common.ConstVariables;
import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.utils.ParseDate;

public class RecordData extends CommonTableData {
    private Float amount;
    private SimpleIdNameObj category;
    private SimpleIdNameObj account;
    private Date date;
    private boolean includedInBalance;

    public RecordData(String id, Float amount, SimpleIdNameObj category, SimpleIdNameObj account, Date date, boolean included) {
        super(id, null);
        this.amount = amount;
        this.category = category;
        this.account = account;
        this.date = date;
        this.includedInBalance = included;
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

    public boolean isIncludedInBalance() {
        return this.includedInBalance;
    }

    public void setIncludedInBalance(boolean includedInBalance) {
        this.includedInBalance = includedInBalance;
    }

    public boolean isIncomeRecord(DBHelper dbHelper) {
        return category.getId().equals(ConstVariables.INCOME_ID) || (CategoryData.getCategoryData(dbHelper, category.getId()).getParentId() != null && CategoryData.getCategoryData(dbHelper, category.getId()).getParentId().equals(ConstVariables.INCOME_ID));
    }

    public boolean isOutcomeRecord(DBHelper dbHelper) {
        return category.getId().equals(ConstVariables.CONSUMPTION_ID) || (CategoryData.getCategoryData(dbHelper, category.getId()).getParentId() != null && CategoryData.getCategoryData(dbHelper, category.getId()).getParentId().equals(ConstVariables.CONSUMPTION_ID));
    }

    public boolean includingInAccountBalance(DBHelper dbHelper, Date recordDate) throws ParseException {
        Date updateAccount = AccountData.getAccount(dbHelper, getAccount().getId()).getUpdateDate();
        Date recordingDate = ParseDate.getDateFromString(ParseDate.parseDateToString(recordDate));
        return updateAccount.compareTo(recordingDate) < 0 || updateAccount.compareTo(recordingDate) == 0;
    }
}

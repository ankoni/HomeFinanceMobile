package main.homefinancemobile.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import main.homefinancemobile.R;
import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.form.AccountForm;
import main.homefinancemobile.utils.ParseDate;

public class AccountData extends CommonTableData {
    private Float balance;
    private Date createDate;
    private Date delDate;
    private Date updateDate;

    public Float getBalance() {
        return this.balance;
    }
    public Date getUpdateDate() {
        return this.updateDate;
    }
    public Date getCreateDate() {
        return this.createDate;
    }

    public AccountData() {
        super();
    }
    public AccountData(String id, String name, Float balance, Date update_date) {
        super(id, name);
        this.balance = balance;
        this.createDate = null;
        this.delDate = null;
        this.updateDate = update_date;
    }
    public AccountData(String id, String name, Float balance, Date create_date, Date update_date) {
        super(id, name);
        this.balance = balance;
        this.createDate = create_date;
        this.delDate = null;
        this.updateDate = update_date;
    }

    public List<TableRowData> convertToTableRowData() {
        List<TableRowData> tableRowData = new ArrayList<>();
        tableRowData.add(new TableRowData("name", this.getName()));
        tableRowData.add(new TableRowData("balance", String.valueOf(this.getBalance())));
        tableRowData.add(new TableRowData("date", ParseDate.parseDateToString(this.getUpdateDate())));
        return tableRowData;
    }

    public static List<AccountData> getAllAccounts(DBHelper dbHelper) throws ParseException {
        List<AccountData> accounts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Accounts", null, "del_date is null", null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int balanceColIndex = c.getColumnIndex("balance");
            int updateDateColIndex = c.getColumnIndex("update_date");
            do {
                AccountData account = new AccountData(
                        c.getString(idColIndex),
                        c.getString(nameColIndex),
                        c.getFloat(balanceColIndex),
                        ParseDate.getDateFromString(c.getString(updateDateColIndex))
                );
                accounts.add(account);
            } while (c.moveToNext());
        }
        return accounts;
    }

    public static AccountData getAccount(DBHelper dbHelper, String id) throws ParseException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Accounts", null, "id = ?", new String[]{id}, null, null, null);
        AccountData account = new AccountData();
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int balanceColIndex = c.getColumnIndex("balance");
            int updateDateColIndex = c.getColumnIndex("update_date");
            account = new AccountData(
                    c.getString(idColIndex),
                    c.getString(nameColIndex),
                    c.getFloat(balanceColIndex),
                    ParseDate.getDateFromString(c.getString(updateDateColIndex))
            );
        }
        return account;
    }

    public static Float getTotalBalance(DBHelper dbHelper) {
        Float totalBalance = (float) 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Accounts", new String[]{"balance"}, "del_date is null", null, null, null, null);
        if (c.moveToFirst()) {
            int balanceColIndex = c.getColumnIndex("balance");
            do {
                totalBalance += c.getFloat(balanceColIndex);
            } while (c.moveToNext());
        }
        return totalBalance;
    }
}


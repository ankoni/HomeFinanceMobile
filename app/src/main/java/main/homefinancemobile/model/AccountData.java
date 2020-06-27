package main.homefinancemobile.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.utils.ParseDate;

public class AccountData extends CommonData {
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

    public AccountData() {}
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

    public static void addNewAccount(Context context, AccountData data) throws ParseException {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", data.getId());
        cv.put("name", data.getName());
        cv.put("balance", data.getBalance());
        cv.put("create_date", ParseDate.parseDateToString(data.getCreateDate()));
        cv.put("update_date", ParseDate.parseDateToString(data.getUpdateDate()));
        db.insert("Accounts", null, cv);
        DailyBalance.updateLastDailyBalance(context, data.getId());
    }

    public static void editAccount(Context context, AccountData data) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        db.update("Accounts", cv, "id = ?", new String[] { data.getId() });
    }

    public static void deleteAccount(Context context, String id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("del_date", ParseDate.parseDateToString(new Date()));
        db.update("Accounts", cv, "id = ?", new String[] { id });
    }

    public static void updateAccounts(Context context, DBHelper dbHelper, RecordData oldRecordData, RecordData record) throws ParseException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Float amount = record.getAmount();
        AccountData account = AccountData.getAccount(dbHelper, record.getAccount().getId());
        Float accountBalance = account.getBalance();
        if (isNeedToUpdateAccounts(oldRecordData, record)) {
            AccountData prevAccount = AccountData.getAccount(dbHelper, oldRecordData.getAccount().getId());

            Float prevBalance = prevAccount.getBalance();
            if (oldRecordData.isIncomeRecord(dbHelper)) {
                prevBalance -= oldRecordData.getAmount();
            } else {
                prevBalance += oldRecordData.getAmount();
            }

            if (account.getId().equals(prevAccount.getId())) {
                accountBalance = prevBalance;
            } else {
                cv.put("balance", prevBalance);
                db.update("Accounts", cv, "id = ?", new String[] { oldRecordData.getAccount().getId() });
            }
        }

        if (isNeedToUpdateAccounts(oldRecordData, record) || oldRecordData == null) {
            if (record.isIncomeRecord(dbHelper)) {
                accountBalance += amount;
            } else {
                accountBalance -= amount;
            }

            cv.clear();
            cv.put("balance", accountBalance);
            if (oldRecordData == null) {
                cv.put("update_date", ParseDate.parseDateToString(record.getDate()));
            }
            //изменение баланса счета, указанного в форме
            db.update("Accounts", cv, "id = ?", new String[] { record.getAccount().getId() });
        }

        if (oldRecordData == null) {
            DailyBalance.updateLastDailyBalance(context, record.getAccount().getId());
        } else {
            DailyBalance.updateOldDailyBalance(context, oldRecordData, record);
        }
    }

    private static boolean isNeedToUpdateAccounts(RecordData oldRecordData, RecordData updatedData) {
        return oldRecordData != null && (!oldRecordData.getAmount().equals(updatedData.getAmount())
                || !oldRecordData.getAccount().equals(updatedData.getAccount())
                || !oldRecordData.getCategory().equals(updatedData.getCategory()));
    }
}


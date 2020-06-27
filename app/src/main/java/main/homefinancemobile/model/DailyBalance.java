package main.homefinancemobile.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.util.Date;

import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.utils.ParseDate;

public class DailyBalance {
    private SimpleIdNameObj account;
    private Float balance;
    private Date date;

    public DailyBalance() {
    }

    public DailyBalance(SimpleIdNameObj account, Float balance, Date date) {
        setAccount(account);
        setBalance(balance);
        setDate(date);
    }

    public SimpleIdNameObj getAccount() {
        return account;
    }

    public Float getBalance() {
        return balance;
    }

    public Date getDate() {
        return date;
    }

    public void setAccount(SimpleIdNameObj account) {
        this.account = account;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // при добавлении счета, записи
    public static void updateLastDailyBalance(Context context, String accountId) throws ParseException {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        AccountData accountData = AccountData.getAccount(dbHelper, accountId);

        ContentValues cv = new ContentValues();
        Cursor c = db.query("DailyBalance", null, "account_id = ? and balance_date = ?", new String[]{accountId, ParseDate.parseDateToString(accountData.getUpdateDate())}, null, null, null);
        if (c.moveToFirst()) {
            cv.put("balance", accountData.getBalance());
            db.update("DailyBalance", cv, "account_id = ? and balance_date = ?", new String[]{accountId, ParseDate.parseDateToString(accountData.getUpdateDate())});
        } else {
            cv.put("account_id", accountId);
            cv.put("balance", accountData.getBalance());
            cv.put("balance_date", ParseDate.parseDateToString(accountData.getUpdateDate()));
            db.insert("DailyBalance", null, cv);
        }
        c.close();
    }

    // изменение ежедневного остатка, если изменяется старая запись из таблицы
    public static void updateOldDailyBalance(Context context, RecordData oldRecord, @Nullable RecordData newRecord) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Float oldBalance = null;
        //ищем запись об остатке старого счета
        Cursor oldDailyBalance = db.query("DailyBalance", null, "account_id = ? and balance_date = ?", new String[]{oldRecord.getAccount().getId(), ParseDate.parseDateToString(oldRecord.getDate())}, null, null, null);
        if (oldDailyBalance.moveToFirst()) {
            oldBalance = oldDailyBalance.getFloat(oldDailyBalance.getColumnIndex("balance"));

            if (oldRecord.isIncomeRecord(dbHelper)) {
                oldBalance -= oldRecord.getAmount();
            } else {
                oldBalance += oldRecord.getAmount();
            }

            ContentValues cv = new ContentValues();
            cv.put("balance", oldBalance);
            db.update("DailyBalance", cv, "account_id = ? and balance_date = ?", new String[]{oldRecord.getAccount().getId(), ParseDate.parseDateToString(oldRecord.getDate())});
        }
        oldDailyBalance.close();

        if (newRecord != null) {
            Cursor updatableDailyBalance = db.query("DailyBalance", null, "account_id = ? and balance_date = ?", new String[]{newRecord.getAccount().getId(), ParseDate.parseDateToString(newRecord.getDate())}, null, null, null);
            if (updatableDailyBalance.moveToFirst()) {
                Float newBalance = oldRecord.getAccount().getId().equals(newRecord.getAccount().getId()) && oldBalance != null ? oldBalance : updatableDailyBalance.getFloat(updatableDailyBalance.getColumnIndex("balance"));
                if (newRecord.isIncomeRecord(dbHelper)) {
                    newBalance += newRecord.getAmount();
                } else {
                    newBalance -= newRecord.getAmount();
                }
                ContentValues cv = new ContentValues();
                cv.put("balance", newBalance);
                db.update("DailyBalance", cv, "account_id = ? and balance_date = ?", new String[]{newRecord.getAccount().getId(), ParseDate.parseDateToString(newRecord.getDate())});
            }
            updatableDailyBalance.close();
        }
    }
}

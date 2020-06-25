package main.homefinancemobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import main.homefinancemobile.common.ConstVariables;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "homeFinance", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAccountsTable(db);
        createCategoriesTable(db);
        createRecordsTable(db);
        createAccountDailyBalance(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Records");
        db.execSQL("drop table if exists Accounts");
        db.execSQL("drop table if exists Categories");
        db.execSQL("drop table if exists DailyBalance");
        onCreate(db);
    }

    /**
     * Таблица Счетов. Колонки:
     *        id,
     *        name (название счета),
     *        balance (остаток на счету),
     *        create_date (дата создания),
     *        del_date (дата удаления),
     *        update_date (дата изменения)
     */
    private void createAccountsTable(SQLiteDatabase db) {
        db.execSQL("create table Accounts (" +
                "id text primary key," +
                "name text," +
                "balance real," +
                "create_date text," +
                "del_date text," +
                "update_date text)");
    }

    /**
     * Таблица категорий
     *         * id,
     *         * name - название категории,
     *         * parent_id - id родительской категории,
     *         * del_date - дата удаления
     */
    private void createCategoriesTable(SQLiteDatabase db) {
        db.execSQL("create table Categories (" +
                "id text primary key," +
                "name text," +
                "parent_id text," +
                "del_date text)");
        ContentValues incomeCategory = new ContentValues();
        incomeCategory.put("id", ConstVariables.INCOME_ID);
        incomeCategory.put("name", "Доход");
        db.insert("Categories", null, incomeCategory);
        ContentValues consumptionCategory = new ContentValues();
        consumptionCategory.put("id", ConstVariables.CONSUMPTION_ID);
        consumptionCategory.put("name", "Расход");
        db.insert("Categories", null, consumptionCategory);
    }

    /**
     * Таблица Записей. Колонки:
     *         id,
     *         amount (сумма),
     *         category_id (id категории из таблицы Categories),
     *         account_id (id счета из таблицы Accounts),
     *         create_date (дата создания),
     *         record_date (дата, указанная в форме при создании)
     *         description (комментарий)
     */
    private void createRecordsTable(SQLiteDatabase db) {
        db.execSQL("create table Records (" +
                "id text primary key," +
                "amount real," +
                "category_id text REFERENCES Categories(id) ON DELETE CASCADE," +
                "account_id text REFERENCES Accounts(id) ON DELETE CASCADE," +
                "create_date text," +
                "record_date text," +
                "description text," +
                "included_in_balance integer)");
    }

    /**
     * Сохранение статистики о ежедневном остатке
     * account_id - id счета,
     * balance - остаток на счету,
     * balance_date - дата
     * @param db
     */
    private void createAccountDailyBalance(SQLiteDatabase db) {
        db.execSQL("create table DailyBalance (" +
                "account_id text REFERENCES Accounts(id) ON DELETE CASCADE," +
                "balance real," +
                "balance_date text," +
                "PRIMARY KEY(account_id, balance_date))");
    }
}

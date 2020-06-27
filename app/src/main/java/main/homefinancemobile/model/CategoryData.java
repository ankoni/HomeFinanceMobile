package main.homefinancemobile.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.utils.ParseDate;

public class CategoryData extends CommonTableData {
    private String parentId;

    public CategoryData() {
    }

    public CategoryData(String id, String name, String parentId) {
        super(id, name);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public static List<SimpleIdNameObj> getAllCategories(DBHelper dbHelper) {
        List<SimpleIdNameObj> categoryDataList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Categories", new String[]{"id", "name", "parent_id"}, "del_date is null", null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int parentColIndex = c.getColumnIndex("parent_id");
            do {
                String name = c.getString(parentColIndex) != null ?
                        CategoryData.getCategory(dbHelper, c.getString(parentColIndex)).getName() + " - " + c.getString(nameColIndex) : c.getString(nameColIndex);
                SimpleIdNameObj category = new SimpleIdNameObj(
                        c.getString(idColIndex),
                        name
                );
                categoryDataList.add(category);
            } while (c.moveToNext());
        }

        return categoryDataList;
    }

    public static SimpleIdNameObj getCategory(DBHelper dbHelper, String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Categories", new String[]{"id", "name"}, "id = ?", new String[]{id}, null, null, null);
        SimpleIdNameObj category = new SimpleIdNameObj();
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            category = new SimpleIdNameObj(
                    c.getString(idColIndex),
                    c.getString(nameColIndex)
            );
        }
        return category;
    }

    public static CategoryData getCategoryData(DBHelper dbHelper, String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Categories", new String[]{"id", "name", "parent_id"}, "id = ?", new String[]{id}, null, null, null);
        CategoryData category = new CategoryData();
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int parentColIndex = c.getColumnIndex("parent_id");
            category = new CategoryData(
                    c.getString(idColIndex),
                    c.getString(nameColIndex),
                    c.getString(parentColIndex)
            );
        }
        return category;
    }

    public static void addNewCategory(Context context, String id, String name, String parentId) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("parent_id", parentId);
        db.insert("Categories", null, cv);
    }

    public static void updateCategory(Context context, String id, String name) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("Categories", cv, "id = ?", new String[] { id });
    }

    public static void deleteCategory(Context context, String id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("del_date", ParseDate.parseDateToString(new Date()));
        db.update("Categories", cv, "id = ?", new String[] { id });
    }
}

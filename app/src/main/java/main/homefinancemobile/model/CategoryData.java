package main.homefinancemobile.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;

public class CategoryData {
    private String id;
    private String name;
    private String parentId;

    public CategoryData() {
    }

    public CategoryData(String id, String name, String parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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
}

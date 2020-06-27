package main.homefinancemobile.fragments.setting;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Setting {

    public static String getSettingValue(SQLiteDatabase db, String valueName) {
        String value = null;
        Cursor setting = db.query("Settings", new String[]{"setting_value"}, "setting_name = ?", new String[]{valueName}, null, null, null);
        if (setting.moveToFirst()) {
            int settingValueColIndex = setting.getColumnIndex("setting_value");
            value = setting.getString(settingValueColIndex);
        }
        return value;
    }
}

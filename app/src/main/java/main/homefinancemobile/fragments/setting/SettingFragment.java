package main.homefinancemobile.fragments.setting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Set;

import main.homefinancemobile.MainActivity;
import main.homefinancemobile.R;
import main.homefinancemobile.common.ConstVariables;
import main.homefinancemobile.database.DBHelper;

public class SettingFragment extends Fragment {

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        EditText userName = view.findViewById(R.id.userNameSettingField);
        Switch darkModeSwitch = view.findViewById(R.id.darkModeSwitch);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        userName.setText(Setting.getSettingValue(db, ConstVariables.USER_NAME));
        darkModeSwitch.setChecked(Setting.getSettingValue(db, ConstVariables.DARK_MODE).equals("1"));

        FieldChanged fieldChangedListener = new FieldChanged(db, (MainActivity)getActivity());
        SwitchListener switchListener = new SwitchListener(db, (MainActivity)getActivity());
        userName.addTextChangedListener(fieldChangedListener);
        darkModeSwitch.setOnCheckedChangeListener(switchListener);
        return view;
    }

    private class FieldChanged implements TextWatcher {
        SQLiteDatabase db;
        MainActivity activity;

        public FieldChanged(SQLiteDatabase db, MainActivity activity) {
            this.db = db;
            this.activity = activity;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ContentValues cv = new ContentValues();
            cv.put("setting_value", s.toString());
            db.update("Settings", cv, "setting_name = ?", new String[]{ConstVariables.USER_NAME});
            activity.setUserName(s.toString());
        }
    }

    private class SwitchListener implements CompoundButton.OnCheckedChangeListener {
        SQLiteDatabase db;
        MainActivity activity;
        public SwitchListener(SQLiteDatabase db, MainActivity activity) {
            this.db = db;
            this.activity = activity;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ContentValues cv = new ContentValues();
            cv.put("setting_value", isChecked ? 1 : 0);
            db.update("Settings", cv, "setting_name = ?", new String[]{ConstVariables.DARK_MODE});
            activity.recreate();
        }
    }

}

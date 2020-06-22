package main.homefinancemobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;

import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.CategoryData;
import main.homefinancemobile.model.RecordData;
import main.homefinancemobile.form.AddRecordForm;
import main.homefinancemobile.table.TableActivity;
import main.homefinancemobile.utils.ParseDate;

/**
 * Данные о записях
 */
public class RecordTableFragment extends Fragment {
    DBHelper dbHelper;
    FloatingActionButton addButton;
    TableLayout financeTable;

    public RecordTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_record_table, container, false);

        financeTable = view.findViewById(R.id.financeTable);
        addButton = view.findViewById(R.id.addRecord);

        //добавление записи
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddRecordForm()).addToBackStack(null).commit();
            }
        });

        //достаем данные из базы и записываем в таблицу
        dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Records", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int amountColIndex = c.getColumnIndex("amount");
            int categoryColIndex = c.getColumnIndex("category_id");
            int accountColIndex = c.getColumnIndex("account_id");
            int recordDateColIndex = c.getColumnIndex("record_date");
            do {
                try {
                    RecordData record = new RecordData(
                            c.getString(idColIndex),
                            c.getFloat(amountColIndex),
                            CategoryData.getCategory(dbHelper, c.getString(categoryColIndex)),
                            AccountData.getAccount(dbHelper, c.getString(accountColIndex)).convertToSimpleIdNameObj(),
                            ParseDate.getDateFromString(c.getString(recordDateColIndex))
                    );
                    TableActivity.renderRow(record, financeTable, this.getContext());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        } else {
            TextView text = new TextView(this.getContext());
            text.setPadding(0, 40, 0, 0);
            text.setGravity(Gravity.CENTER);
            text.setText("No records.");
            financeTable.addView(text);
        }
        dbHelper.close();

        return view;
    }
}

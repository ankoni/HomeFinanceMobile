package main.homefinancemobile.fragments.record;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.homefinancemobile.R;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.CategoryData;
import main.homefinancemobile.model.RecordData;
import main.homefinancemobile.form.RecordForm;
import main.homefinancemobile.utils.ParseDate;

/**
 * Данные о записях
 */
public class RecordTableFragment extends Fragment {
    DBHelper dbHelper;
    FloatingActionButton addButton;
    ListView financeTable;
    List<RecordData> recordDataList;

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
        recordDataList = new ArrayList<>();
        financeTable = view.findViewById(R.id.financeTable);
        addButton = view.findViewById(R.id.addRecord);

        //добавление записи
        addButton.setOnClickListener(v -> openForm());

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
            int includedColIndex = c.getColumnIndex("included_in_balance");
            do {
                try {
                    RecordData record = new RecordData(
                            c.getString(idColIndex),
                            c.getFloat(amountColIndex),
                            CategoryData.getCategory(dbHelper, c.getString(categoryColIndex)),
                            AccountData.getAccount(dbHelper, c.getString(accountColIndex)).convertToSimpleIdNameObj(),
                            ParseDate.getDateFromString(c.getString(recordDateColIndex)),
                            c.getInt(includedColIndex) == 1
                    );
                    recordDataList.add(record);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());

            financeTable.setAdapter(new RecordRowAdapter(this.getActivity(), recordDataList));
            financeTable.setOnItemClickListener((parent, view1, position, id) -> openForm(recordDataList.get(position)));
        } else {
            TextView text = new TextView(this.getContext());
            text.setPadding(0, 40, 0, 0);
            text.setGravity(Gravity.CENTER);
            text.setText("No records.");
            ((LinearLayout)view.findViewById(R.id.tableContainer)).addView(text);
        }

        dbHelper.close();

        return view;
    }

    private void openForm() {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RecordForm()).addToBackStack(null).commit();
    }

    private void openForm(RecordData data) {
        Bundle args = new Bundle();
        args.putString("id", data.getId());
        args.putFloat("amount", data.getAmount());
        args.putString("categoryId", data.getCategory().getId());
        args.putString("accountId", data.getAccount().getId());
        args.putString("date", ParseDate.parseDateToString(data.getDate()));
        args.putBoolean("includedInBalance", data.isIncludedInBalance());
        RecordForm form = new RecordForm();
        form.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                form).addToBackStack(null).commit();
    }

}

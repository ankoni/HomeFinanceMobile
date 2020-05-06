package main.homefinancemobile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.RecordFormData;
import main.homefinancemobile.model.TableRowData;
import main.homefinancemobile.record.form.AddRecordForm;
import main.homefinancemobile.table.TableActivity;
import main.homefinancemobile.utils.ParseDate;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    FloatingActionButton addButton;
    TableLayout financeTable;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        financeTable = findViewById(R.id.financeTable);

        dbHelper = new DBHelper(this);
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
                    RecordFormData record = new RecordFormData(
                            c.getString(idColIndex),
                            c.getFloat(amountColIndex),
                            c.getString(categoryColIndex),
                            c.getString(accountColIndex),
                            ParseDate.getDateFromString(c.getString(recordDateColIndex))
                    );
                    TableActivity.addRow(record, financeTable, this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        } else {
            TextView text = new TextView(this);
            text.setText("No records.");
            financeTable.addView(text);
        }

        dbHelper.close();

        addButton = findViewById(R.id.addRecord);

        final Context context = this;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddRecordForm.class);
                startActivity(intent);
            }
        });
    }
}

package main.homefinancemobile.record.form;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import main.homefinancemobile.MainActivity;
import main.homefinancemobile.R;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.RecordFormData;
import main.homefinancemobile.table.TableActivity;
import main.homefinancemobile.utils.ParseDate;
import main.homefinancemobile.utils.Validate;

public class AddRecordForm extends AppCompatActivity {


    DBHelper dbHelper;
    EditText amountField;
    AutoCompleteTextView categoryField;
    AutoCompleteTextView accountField;
    EditText dateField;
    Button calendar;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    Button createRecordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_form);

        dateField = findViewById(R.id.dateField);
        createRecordBtn = findViewById(R.id.createRecordBtn);
        createRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initValueForm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        calendar = findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                if (AddRecordForm.this.dateField.getHint().length() != 0) {
                    try {
                        year = ParseDate.getYearFromString(dateField.getHint().toString());
                        month = ParseDate.getMonthFromString(dateField.getHint().toString());
                        day = ParseDate.getDayFromString(dateField.getHint().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                DatePickerDialog dialog = new DatePickerDialog(
                        AddRecordForm.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = ParseDate.mToMm(dayOfMonth) + "." + ParseDate.mToMm(month) + "." + year;
                AddRecordForm.this.dateField.setHint(date);
            }
        };

    }

    private void initValueForm() throws ParseException {
        amountField = findViewById(R.id.amountField);
        categoryField = findViewById(R.id.categoryField);
        accountField = findViewById(R.id.accountField);
        System.out.println(dateField.getText());
        Date date = ParseDate.getDateFromString(dateField.getHint().toString());
        RecordFormData recordFormData = new RecordFormData(
                UUID.randomUUID().toString(),
                Float.parseFloat(amountField.getText().toString()),
                categoryField.getText().toString(),
                accountField.getText().toString(),
                date
        );
        if (validateForm(recordFormData)) {
            addNewRecord(recordFormData);
        }
    }

    private void addNewRecord(RecordFormData formData) {
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", formData.getId());
        cv.put("amount", formData.getAmount());
        cv.put("category_id", formData.getCategory());
        cv.put("account_id", formData.getAccount());
        cv.put("create_date", ParseDate.parseDateToString(new Date()));
        cv.put("record_date", ParseDate.parseDateToString(formData.getDate()));
        cv.put("description", "");
        db.insert("Records", null, cv);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean validateForm(RecordFormData formData) {
        return !Validate.isEmpty(formData.getAccount()) && !Validate.isEmpty(formData.getCategory()) && formData.getAmount() != null;
    }


}

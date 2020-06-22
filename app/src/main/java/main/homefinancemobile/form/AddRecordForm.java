package main.homefinancemobile.form;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import main.homefinancemobile.R;
import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.CategoryData;
import main.homefinancemobile.model.CommonTableData;
import main.homefinancemobile.model.RecordData;
import main.homefinancemobile.utils.ParseDate;
import main.homefinancemobile.utils.Validate;

/**
 * Форма для Записи (добавление/редактирование)
 */
public class AddRecordForm extends Fragment implements AdapterView.OnItemSelectedListener {
    private TextInputEditText dateField;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private List<SimpleIdNameObj> accounts;
    private SimpleIdNameObj selectedAccount;
    private List<SimpleIdNameObj> categories;
    private SimpleIdNameObj selectedCategory;
    DBHelper dbHelper;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_record_form, container, false);
        dbHelper = new DBHelper(this.getContext());

        try {
            loadAccounts(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        loadCategories(view);
        renderCalendar(view);

        Button createRecordBtn = view.findViewById(R.id.createRecordBtn);
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
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * создание календаря в поле
     * @param view
     */
    private void renderCalendar(View view) {
        dateField = view.findViewById(R.id.dateFieldText);
        Button calendar = view.findViewById(R.id.calendar);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                if (AddRecordForm.this.dateField.getText().length() != 0) {
                    try {
                        year = ParseDate.getYearFromString(dateField.getText().toString());
                        month = ParseDate.getMonthFromString(dateField.getText().toString());
                        day = ParseDate.getDayFromString(dateField.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
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
                AddRecordForm.this.dateField.setText(date);
            }
        };
    }

    /**
     * загрузка списка счетов
     */
    private void loadAccounts(View view) throws ParseException {
        Spinner accountFieldSpinner = view.findViewById(R.id.accountFieldSpinner);
        accounts = AccountData.getAllAccounts(dbHelper).stream().map(CommonTableData::convertToSimpleIdNameObj).collect(Collectors.toList());
        List<String> accountNames = accounts.stream().map(it -> it.getName()).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, accountNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountFieldSpinner.setAdapter(adapter);
        accountFieldSpinner.setOnItemSelectedListener(this);
    }

    /**
     * загрузка списка категорий
     * @param view
     */
    private void loadCategories(View view) {
        Spinner categoryFieldSpinner = view.findViewById(R.id.categoryFieldSpinner);
        categories = CategoryData.getAllCategories(dbHelper);

        List<String> categoryNames = categories.stream().map(it -> it.getName()).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFieldSpinner.setAdapter(adapter);
        categoryFieldSpinner.setOnItemSelectedListener(this);
    }

    private void initValueForm() throws ParseException {
        TextInputEditText amountField = getView().findViewById(R.id.amountFieldText);

        Date date = ParseDate.getDateFromString(dateField.getText().toString());
        RecordData recordData = new RecordData(
                UUID.randomUUID().toString(),
                Float.parseFloat(amountField.getText().toString()),
                selectedCategory,
                selectedAccount,
                date
        );
        if (validateForm(recordData)) {
            addNewRecord(recordData);
            getFragmentManager().popBackStack();
        }
    }

    private void addNewRecord(RecordData formData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", formData.getId());
        cv.put("amount", formData.getAmount());
        cv.put("category_id", formData.getCategory().getId());
        cv.put("account_id", formData.getAccount().getId());
        cv.put("create_date", ParseDate.parseDateToString(new Date()));
        cv.put("record_date", ParseDate.parseDateToString(formData.getDate()));
        cv.put("description", "");
        db.insert("Records", null, cv);
    }

    private boolean validateForm(RecordData formData) {
        return formData.getAccount() != null && !Validate.isEmpty(formData.getCategory()) && formData.getAmount() != null;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.accountFieldSpinner:
                selectedAccount = accounts.get(position);
                break;
            case R.id.categoryFieldSpinner:
                selectedCategory = categories.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedAccount = null;
    }
}

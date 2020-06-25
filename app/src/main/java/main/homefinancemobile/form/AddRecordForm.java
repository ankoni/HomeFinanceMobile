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

import main.homefinancemobile.MainActivity;
import main.homefinancemobile.R;
import main.homefinancemobile.common.SimpleIdNameObj;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.CategoryData;
import main.homefinancemobile.model.CommonTableData;
import main.homefinancemobile.model.DailyBalance;
import main.homefinancemobile.model.RecordData;
import main.homefinancemobile.utils.ParseDate;
import main.homefinancemobile.utils.Validate;

/**
 * Форма для Записи (добавление/редактирование)
 */
public class AddRecordForm extends Fragment implements AdapterView.OnItemSelectedListener {
    private RecordData oldRecordData;
    TextInputEditText amountField;
    private TextInputEditText dateField;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner accountFieldSpinner;
    private List<SimpleIdNameObj> accounts;
    private SimpleIdNameObj selectedAccount;
    private Spinner categoryFieldSpinner;
    private List<SimpleIdNameObj> categories;
    private SimpleIdNameObj selectedCategory;
    Bundle args;
    DBHelper dbHelper;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_record_form, container, false);
        dbHelper = new DBHelper(this.getContext());

        args = getArguments();
        try {
            loadAccounts(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        loadCategories(view);
        renderCalendar(view);

        Button createRecordBtn = view.findViewById(R.id.createRecordBtn);
        amountField = view.findViewById(R.id.amountFieldText);

        if (args != null) {
            try {
                oldRecordData = new RecordData(
                        args.getString("id"),
                        args.getFloat("amount"),
                        categories.stream().filter(it -> it.getId().equals(args.getString("categoryId"))).findFirst().orElse(null),
                        accounts.stream().filter(it -> it.getId().equals(args.getString("accountId"))).findFirst().orElse(null),
                        ParseDate.getDateFromString(args.getString("date")),
                        args.getBoolean("includedInBalance")
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            createRecordBtn.setText("Изменить");
            amountField.setText(oldRecordData.getAmount().toString());
            dateField.setText(args.getString("date"));

            // если запись старше 7 дней
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            if (cal.getTime().compareTo(oldRecordData.getDate()) > 0) {
                createRecordBtn.setEnabled(false);
                createRecordBtn.setClickable(false);
            }
        }

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
        accountFieldSpinner = view.findViewById(R.id.accountFieldSpinner);
        accounts = AccountData.getAllAccounts(dbHelper).stream().map(CommonTableData::convertToSimpleIdNameObj).collect(Collectors.toList());
        List<String> accountNames = accounts.stream().map(it -> it.getName()).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, accountNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountFieldSpinner.setAdapter(adapter);
        accountFieldSpinner.setOnItemSelectedListener(this);
        if (args != null) {
            String accountId = args.getString("accountId");
            SimpleIdNameObj account = accounts.stream().filter(it -> it.getId().equals(accountId)).findFirst().orElse(null);
            if (account != null) {
                accountFieldSpinner.setSelection(accounts.indexOf(account));
            }
        }
    }

    /**
     * загрузка списка категорий
     * @param view
     */
    private void loadCategories(View view) {
        categoryFieldSpinner = view.findViewById(R.id.categoryFieldSpinner);
        categories = CategoryData.getAllCategories(dbHelper);

        List<String> categoryNames = categories.stream().map(it -> it.getName()).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFieldSpinner.setAdapter(adapter);
        categoryFieldSpinner.setOnItemSelectedListener(this);
        if (args != null) {
            String categoryId = args.getString("categoryId");
            SimpleIdNameObj category = categories.stream().filter(it -> it.getId().equals(categoryId)).findFirst().get();
            categoryFieldSpinner.setSelection(categories.indexOf(category));
        }
    }

    private void initValueForm() throws ParseException {
        Date date = ParseDate.getDateFromString(dateField.getText().toString());
        RecordData recordData = new RecordData(
                oldRecordData != null ? oldRecordData.getId() : UUID.randomUUID().toString(),
                Float.parseFloat(amountField.getText().toString()),
                selectedCategory,
                selectedAccount,
                date,
                oldRecordData == null || oldRecordData.isIncludedInBalance()
        );
        if (validateForm(recordData)) {
            if (oldRecordData != null) {
                updateRecord(recordData);
            } else {
                recordData.setIncludedInBalance(recordData.includingInAccountBalance(dbHelper, recordData.getDate()));
                addNewRecord(recordData);
            }
            if (recordData.isIncludedInBalance() || (oldRecordData != null && oldRecordData.isIncludedInBalance())) {
                updateAccounts(recordData);
            }
            getFragmentManager().popBackStack();
        }
    }

    private void updateRecord(RecordData recordData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", recordData.getAmount());
        cv.put("category_id", recordData.getCategory().getId());
        cv.put("account_id", recordData.getAccount().getId());
        cv.put("create_date", ParseDate.parseDateToString(new Date()));
        cv.put("record_date", ParseDate.parseDateToString(recordData.getDate()));
        cv.put("description", "");
        cv.put("included_in_balance", recordData.isIncludedInBalance());
        db.update("Records", cv, "id = ?", new String[] { recordData.getId() });
    }

    private void addNewRecord(RecordData formData) throws ParseException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", formData.getId());
        cv.put("amount", formData.getAmount());
        cv.put("category_id", formData.getCategory().getId());
        cv.put("account_id", formData.getAccount().getId());
        cv.put("create_date", ParseDate.parseDateToString(new Date()));
        cv.put("record_date", ParseDate.parseDateToString(formData.getDate()));
        cv.put("description", "");
        cv.put("included_in_balance", formData.isIncludedInBalance() ? 1 : 0);
        db.insert("Records", null, cv);
    }

    private void deleteRecord(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        db.delete("Records", "id = ?", new String[]{id});
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

    private void updateAccounts(RecordData record) throws ParseException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Float amount = record.getAmount();
        AccountData account = AccountData.getAccount(dbHelper, record.getAccount().getId());
        Float accountBalance = account.getBalance();
        if (isNeedToUpdateAccounts(record)) {
            AccountData prevAccount = AccountData.getAccount(dbHelper, oldRecordData.getAccount().getId());

            Float prevBalance = prevAccount.getBalance();
            if (oldRecordData.isIncomeRecord(dbHelper)) {
                prevBalance -= oldRecordData.getAmount();
            } else {
                prevBalance += oldRecordData.getAmount();
            }

            if (account.getId().equals(prevAccount.getId())) {
                accountBalance = prevBalance;
            } else {
                cv.put("balance", prevBalance);
                db.update("Accounts", cv, "id = ?", new String[] { oldRecordData.getAccount().getId() });
            }
        }

        if (isNeedToUpdateAccounts(record) || oldRecordData == null) {
            if (record.isIncomeRecord(dbHelper)) {
                accountBalance += amount;
            } else {
                accountBalance -= amount;
            }

            cv.clear();
            cv.put("balance", accountBalance);
            if (oldRecordData == null) {
                cv.put("update_date", ParseDate.parseDateToString(record.getDate()));
            }
            //изменение баланса счета, указанного в форме
            db.update("Accounts", cv, "id = ?", new String[] { record.getAccount().getId() });
        }

        if (oldRecordData == null) {
            DailyBalance.updateLastDailyBalance(getContext(), record.getAccount().getId());
        } else {
            DailyBalance.updateOldDailyBalance(getContext(), oldRecordData, record);
        }

        ((MainActivity)getActivity()).setTotalBalance();
    }

    private boolean isNeedToUpdateAccounts(RecordData updatedData) {
        return oldRecordData != null && (!oldRecordData.getAmount().equals(updatedData.getAmount())
                || !oldRecordData.getAccount().equals(updatedData.getAccount())
                || !oldRecordData.getCategory().equals(updatedData.getCategory()));
    }
}

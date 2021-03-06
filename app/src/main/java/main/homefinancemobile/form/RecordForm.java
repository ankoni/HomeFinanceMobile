package main.homefinancemobile.form;

import android.app.DatePickerDialog;
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
import main.homefinancemobile.model.CommonData;
import main.homefinancemobile.model.RecordData;
import main.homefinancemobile.utils.ParseDate;
import main.homefinancemobile.utils.Validate;

/**
 * Форма для Записи (добавление/редактирование)
 */
public class RecordForm extends Fragment implements AdapterView.OnItemSelectedListener {
    private RecordData oldRecordData;
    private TextInputEditText amountField;
    private TextInputEditText dateField;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner accountFieldSpinner;
    private List<SimpleIdNameObj> accounts;
    private SimpleIdNameObj selectedAccount;
    private Spinner categoryFieldSpinner;
    private List<SimpleIdNameObj> categories;
    private SimpleIdNameObj selectedCategory;
    private Button calendar;
    private Bundle args;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.form_record, container, false);
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
            if (cal.getTime().compareTo(oldRecordData.getDate()) > 0 && oldRecordData.isIncludedInBalance()) {
                createRecordBtn.setEnabled(false);
                createRecordBtn.setClickable(false);
            }
            calendar.setVisibility(View.INVISIBLE);
        }

        createRecordBtn.setOnClickListener(v -> {
            try {
                getValueForm();
            } catch (ParseException e) {
                e.printStackTrace();
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
        calendar = view.findViewById(R.id.calendar);

        if (args == null) {
            dateField.setText(ParseDate.parseDateToString(new Date()));
        }

        calendar.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            if (RecordForm.this.dateField.getText().length() != 0) {
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
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = ParseDate.mToMm(dayOfMonth) + "." + ParseDate.mToMm(month) + "." + year;
            RecordForm.this.dateField.setText(date);
        };
    }

    /**
     * загрузка списка счетов
     */
    private void loadAccounts(View view) throws ParseException {
        accountFieldSpinner = view.findViewById(R.id.accountFieldSpinner);
        accounts = AccountData.getAllAccounts(dbHelper).stream().map(CommonData::convertToSimpleIdNameObj).collect(Collectors.toList());
        List<String> accountNames = accounts.stream().map(SimpleIdNameObj::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, accountNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountFieldSpinner.setAdapter(adapter);
        accountFieldSpinner.setOnItemSelectedListener(this);
        if (args != null) {
            String accountId = args.getString("accountId");
            SimpleIdNameObj account = accounts.stream().filter(it -> it.getId().equals(accountId)).findFirst().orElse(null);
            if (account == null) {
                AccountData deletedAccountData = AccountData.getAccount(dbHelper, accountId);
                SimpleIdNameObj deletedAccount = deletedAccountData != null ? deletedAccountData.convertToSimpleIdNameObj() : null;
                if (deletedAccount != null) {
                    accounts.add(deletedAccount);
                    accountNames.add(deletedAccount.getName());
                    adapter.notifyDataSetChanged();
                    accountFieldSpinner.setSelection(accounts.indexOf(deletedAccount));
                }
            } else {
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

        List<String> categoryNames = categories.stream().map(SimpleIdNameObj::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFieldSpinner.setAdapter(adapter);
        categoryFieldSpinner.setOnItemSelectedListener(this);
        if (args != null) {
            String categoryId = args.getString("categoryId");
            SimpleIdNameObj category = categories.stream().filter(it -> it.getId().equals(categoryId)).findFirst().orElse(null);
            if (category == null) {
                SimpleIdNameObj deletedCategory = CategoryData.getCategory(dbHelper, categoryId);
                if (deletedCategory != null) {
                    categories.add(deletedCategory);
                    categoryNames.add(deletedCategory.getName());
                    adapter.notifyDataSetChanged();
                    categoryFieldSpinner.setSelection(categories.indexOf(deletedCategory));
                }
            } else {
                categoryFieldSpinner.setSelection(categories.indexOf(category));
            }
        }
    }

    /**
     * обработка данных формы
     * @throws ParseException
     */
    private void getValueForm() throws ParseException {
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
                recordData.updateRecord(dbHelper);
            } else {
                recordData.setIncludedInBalance(recordData.includingInAccountBalance(dbHelper, recordData.getDate()));
                recordData.addNewRecord(dbHelper);
            }
            if (recordData.isIncludedInBalance() || (oldRecordData != null && oldRecordData.isIncludedInBalance())) {
                AccountData.updateAccounts(getContext(), dbHelper, oldRecordData, recordData);
                ((MainActivity)getActivity()).setTotalBalance();
            }
            getFragmentManager().popBackStack();
        }
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

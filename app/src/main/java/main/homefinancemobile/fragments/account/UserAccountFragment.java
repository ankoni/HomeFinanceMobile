package main.homefinancemobile.fragments.account;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.homefinancemobile.MainActivity;
import main.homefinancemobile.R;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.form.AccountForm;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.TableRowData;
import main.homefinancemobile.utils.ParseDate;

/**
 * Данные о счетах
 */
public class UserAccountFragment extends Fragment implements CardRecyclerAdapter.OnCardListener {
    private DBHelper dbHelper;
    private RecyclerView accountContainer;
    private FloatingActionButton addButton;
    private ArrayList<AccountData> accountDataList = new ArrayList<>();
    private CardRecyclerAdapter mCardRecyclerAdapter;

    public UserAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);
        accountContainer = view.findViewById(R.id.accountContainer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        accountContainer.setLayoutManager(linearLayoutManager);
        mCardRecyclerAdapter = new CardRecyclerAdapter(accountDataList, this);
        accountContainer.setAdapter(mCardRecyclerAdapter);
        addButton = view.findViewById(R.id.addRecord);

        //добавление записи
        addButton.setOnClickListener(v -> openDialog());

        dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        initData(db, view);
        dbHelper.close();
        return view;
    }

    private void initData(SQLiteDatabase db, View view) {
        Cursor c = db.query("Accounts", null, "del_date is null", null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int balanceColIndex = c.getColumnIndex("balance");
            int dateColIndex = c.getColumnIndex("update_date");
            do {
                try {
                    AccountData accountData = new AccountData(
                            c.getString(idColIndex),
                            c.getString(nameColIndex),
                            c.getFloat(balanceColIndex),
                            ParseDate.getDateFromString(c.getString(dateColIndex))
                    );
                    accountDataList.add(accountData);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());

            mCardRecyclerAdapter.notifyDataSetChanged();
        } else {
            TextView text = new TextView(this.getContext());
            text.setPadding(0, 40, 0, 0);
            text.setGravity(Gravity.CENTER);
            text.setText("No accounts.");
            ((ConstraintLayout)view.findViewById(R.id.accountView)).addView(text);
        }
        c.close();
    }

    /**
     * Открытие диалога для создания
     */
    private void openDialog() {
        AccountForm accountFormDialog = new AccountForm();
        accountFormDialog.setTargetFragment(this, 1);
        accountFormDialog.show(getFragmentManager().beginTransaction(), "create");
    }

    /**
     * Открытие диалога для редактирования
     * @param id
     * @throws ParseException
     */
    private void openDialog(String id) throws ParseException {
        AccountData accountData = AccountData.getAccount(dbHelper, id);
        AccountForm accountFormDialog = new AccountForm();
        accountFormDialog.setTargetFragment(this, 1);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        List<TableRowData> rowData = accountData.convertToTableRowData();
        rowData.forEach(it -> {
            bundle.putString(it.getColumn(), it.getValue());
        });
        accountFormDialog.setArguments(bundle);
        accountFormDialog.show(getFragmentManager().beginTransaction(), "edit");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
           updateData();
        }
    }

    /**
     * обновляет фрагмент
     */
    private void updateData() {
        accountDataList.clear();
        Fragment frg;
        frg = this;
        final FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        ((MainActivity)getActivity()).setTotalBalance();
    }

    @Override
    public void onCardClick(int position) {
        try {
            openDialog(accountDataList.get(position).getId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

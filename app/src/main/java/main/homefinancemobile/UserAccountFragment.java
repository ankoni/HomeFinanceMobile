package main.homefinancemobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.form.AccountForm;
import main.homefinancemobile.fragments.account.CardRecyclerAdapter;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.TableRowData;
import main.homefinancemobile.utils.ParseDate;

/**
 * Данные о счетах
 */
public class UserAccountFragment extends Fragment implements CardRecyclerAdapter.OnCardListener {
    DBHelper dbHelper;
    RecyclerView accountContainer;
    FloatingActionButton addButton;
    ArrayList<AccountData> accountDataList = new ArrayList<>();
    CardRecyclerAdapter mCardRecyclerAdapter;

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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Accounts", null, null, null, null, null, null);
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
                    // AccountData.renderToCard(view.getContext(), accountContainer, accountData);
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
            text.setText("No records.");
            accountContainer.addView(text);
        }
        dbHelper.close();
        return view;
    }

    private void openDialog() {
        AccountForm accountFormDialog = new AccountForm();
        accountFormDialog.setTargetFragment(this, 1);
        accountFormDialog.show(getFragmentManager().beginTransaction(), "create");
    }

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

    public static void addNewRecord(Context context, AccountData data) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", data.getId());
        cv.put("name", data.getName());
        cv.put("balance", data.getBalance());
        cv.put("create_date", ParseDate.parseDateToString(data.getCreateDate()));
        cv.put("update_date", ParseDate.parseDateToString(data.getUpdateDate()));
        db.insert("Accounts", null, cv);
    }

    public static void editRecord(Context context, AccountData data) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        db.update("Accounts", cv, "id = ?", new String[] { data.getId() });
    }

    public static void deleteRecord(Context context, String id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Accounts","id = ?", new String[] { id });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
           updateData();
        }
    }
    private void updateData() {
        accountDataList.clear();
        Fragment frg = null;
        frg = this;
        final FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
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

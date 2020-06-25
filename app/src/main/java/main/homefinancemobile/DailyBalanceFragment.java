package main.homefinancemobile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.fragments.totalbalance.DailyBalanceListAdapter;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.model.DailyBalance;
import main.homefinancemobile.utils.ParseDate;

public class DailyBalanceFragment extends Fragment {
    private DBHelper dbHelper;
    private ListView totalBalanceList;
    private HashMap<Date, List<DailyBalance>> totalBalanceData = new HashMap<>();

    public DailyBalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_balance, container, false);
        totalBalanceList = view.findViewById(R.id.totalBalanceList);

        dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("DailyBalance", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int accountIdColIndex = c.getColumnIndex("account_id");
            int balanceColIndex = c.getColumnIndex("balance");
            int dateColIndex = c.getColumnIndex("balance_date");
            do {
                try {
                    DailyBalance balanceRecord = new DailyBalance(
                            AccountData.getAccount(dbHelper, c.getString(accountIdColIndex)).convertToSimpleIdNameObj(),
                            c.getFloat(balanceColIndex),
                            ParseDate.getDateFromString(c.getString(dateColIndex))
                    );
                    if (totalBalanceData.get(balanceRecord.getDate()) != null) {
                        totalBalanceData.get(balanceRecord.getDate()).add(balanceRecord);
                    } else {
                        List<DailyBalance> list = new ArrayList<>();
                        list.add(balanceRecord);
                        totalBalanceData.put(balanceRecord.getDate(), list);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
            totalBalanceList.setAdapter(new DailyBalanceListAdapter(getActivity(), totalBalanceData));
        } else {
            TextView text = new TextView(this.getContext());
            text.setPadding(0, 40, 0, 0);
            text.setGravity(Gravity.CENTER);
            text.setText("No records.");
            ((LinearLayout)view.findViewById(R.id.totalBalanceFragment)).addView(text);
        }

        return view;
    }
}

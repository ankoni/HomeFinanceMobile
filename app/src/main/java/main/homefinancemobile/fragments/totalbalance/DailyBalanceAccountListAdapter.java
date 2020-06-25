package main.homefinancemobile.fragments.totalbalance;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import main.homefinancemobile.R;
import main.homefinancemobile.model.DailyBalance;

public class DailyBalanceAccountListAdapter extends ArrayAdapter {
    private List<DailyBalance> dailyBalanceData;
    private Activity activity;

    public DailyBalanceAccountListAdapter(Activity activity, List<DailyBalance> data) {
        super(activity, R.layout.daily_balance_account_item, data);
        this.dailyBalanceData = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DailyBalanceAccountItemView dailyBalanceAccountItemView = null;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.daily_balance_account_item, null);

            dailyBalanceAccountItemView = new DailyBalanceAccountItemView();
            dailyBalanceAccountItemView.accountNameDailyBalance = row.findViewById(R.id.accountNameDailyBalance);
            dailyBalanceAccountItemView.accountDailyBalance = row.findViewById(R.id.accountDailyBalance);
            row.setTag(dailyBalanceAccountItemView);
        } else {
            dailyBalanceAccountItemView = (DailyBalanceAccountItemView)row.getTag();
        }

        DailyBalance rowData = dailyBalanceData.get(position);

        dailyBalanceAccountItemView.accountNameDailyBalance.setText(rowData.getAccount().getName());
        dailyBalanceAccountItemView.accountDailyBalance.setText(rowData.getBalance().toString());

        return row;
    }

    public static class DailyBalanceAccountItemView {
        protected TextView accountNameDailyBalance;
        protected TextView accountDailyBalance;
    }
}

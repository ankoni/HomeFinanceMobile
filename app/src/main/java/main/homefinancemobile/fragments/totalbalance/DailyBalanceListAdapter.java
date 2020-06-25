package main.homefinancemobile.fragments.totalbalance;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import main.homefinancemobile.R;
import main.homefinancemobile.model.DailyBalance;
import main.homefinancemobile.utils.ParseDate;

public class DailyBalanceListAdapter extends ArrayAdapter {
    private HashMap<Date, List<DailyBalance>> totalBalanceData;
    private Activity activity;

    public DailyBalanceListAdapter(Activity activity, HashMap<Date, List<DailyBalance>> data) {
        super(activity, R.layout.daily_balance_row, data.keySet().toArray());
        this.totalBalanceData = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DailyBalanceRowView dailyBalanceRowView = null;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.daily_balance_row, null);

            dailyBalanceRowView = new DailyBalanceRowView();
            dailyBalanceRowView.accountList = row.findViewById(R.id.accountList);
            dailyBalanceRowView.dailyBalanceResult = row.findViewById(R.id.dailyBalanceResult);
            dailyBalanceRowView.dailyBalanceDate = row.findViewById(R.id.dailyBalanceDate);
            row.setTag(dailyBalanceRowView);
        } else {
            dailyBalanceRowView = (DailyBalanceRowView) row.getTag();
        }

        List<DailyBalance> rowData = totalBalanceData.get(new ArrayList<>(totalBalanceData.keySet()).get(position));
        AtomicReference<Float> result = new AtomicReference<>(Float.valueOf(0));
        rowData.forEach(it -> result.updateAndGet(v -> v + it.getBalance()));

        dailyBalanceRowView.accountList.setAdapter(new DailyBalanceAccountListAdapter(activity, rowData));

        DailyBalanceAccountListAdapter accountAdapter = (DailyBalanceAccountListAdapter)dailyBalanceRowView.accountList.getAdapter();
        updateHeightOfAccountList(accountAdapter, dailyBalanceRowView.accountList);

        dailyBalanceRowView.dailyBalanceResult.setText(result.toString());
        dailyBalanceRowView.dailyBalanceDate.setText(ParseDate.parseDateToString(rowData.get(0).getDate()));
        return row;
    }

    private void updateHeightOfAccountList(DailyBalanceAccountListAdapter accountAdapter, ListView accountList) {
        if (accountAdapter != null) {
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(accountList.getWidth(), View.MeasureSpec.UNSPECIFIED);
            int totalHeight = 0;
            View view = null;
            for (int i = 0; i < accountAdapter.getCount(); i++) {
                view = accountAdapter.getView(i, view, accountList);
                if (i == 0)
                    view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

                view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += view.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = accountList.getLayoutParams();
            params.height = totalHeight + (accountList.getDividerHeight() * (accountAdapter.getCount() - 1));
            accountList.setLayoutParams(params);
            accountList.requestLayout();
        }
    }

    public static class DailyBalanceRowView {
        protected ListView accountList;
        protected TextView dailyBalanceResult;
        protected TextView dailyBalanceDate;
    }
}

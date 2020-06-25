package main.homefinancemobile.fragments.record;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import main.homefinancemobile.R;
import main.homefinancemobile.model.RecordData;
import main.homefinancemobile.utils.ParseDate;

public class RecordRowAdapter extends ArrayAdapter {
    private List<RecordData> recordDataList;
    private Activity activity;

    public RecordRowAdapter(Activity activity, List<RecordData> data) {
        super(activity, R.layout.record_row, data);
        this.recordDataList = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        RecordRowView recordRowView = null;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.record_row, null);

            recordRowView = new RecordRowView();
            recordRowView.amount = row.findViewById(R.id.amountRow);
            recordRowView.category = row.findViewById(R.id.categoryRow);
            recordRowView.account = row.findViewById(R.id.accountRow);
            recordRowView.date = row.findViewById(R.id.dateRow);
            row.setTag(recordRowView);
        } else {
            recordRowView = (RecordRowView) row.getTag();
        }

        RecordData rowData = recordDataList.get(position);
        recordRowView.amount.setText(rowData.getAmount().toString());
        recordRowView.category.setText(rowData.getCategory().getName());
        recordRowView.account.setText(rowData.getAccount().getName());
        recordRowView.date.setText(ParseDate.parseDateToString(rowData.getDate()));
        return row;
    }

    public static class RecordRowView {
        protected TextView amount;
        protected TextView category;
        protected TextView account;
        protected TextView date;
    }
}

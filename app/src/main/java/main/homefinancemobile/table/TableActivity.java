package main.homefinancemobile.table;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.function.Consumer;

import main.homefinancemobile.R;
import main.homefinancemobile.model.RecordFormData;
import main.homefinancemobile.model.TableRowData;

public class TableActivity {

    final static String RECORD_ROW_PREFIX = "row_";

    public static void addRow(RecordFormData data, TableLayout table, final Context context) {
        final TableRow row = new TableRow(context);
        List<TableRowData> rowData = data.convertToTableRowData();
        for (int i = 0; i < rowData.size(); i++) {
            TextView text = new TextView(row.getContext());
            text.setLayoutParams(new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            text.setGravity(Gravity.CENTER);
            text.setText(rowData.get(i).getValue());
            text.setBackgroundResource(R.mipmap.column_body);
            row.setTag(RECORD_ROW_PREFIX + data.getId());
            row.addView(text);
        }
        table.addView(row);
    }
}

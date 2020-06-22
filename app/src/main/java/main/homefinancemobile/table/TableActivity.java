package main.homefinancemobile.table;

import android.content.Context;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import main.homefinancemobile.R;
import main.homefinancemobile.model.CommonTableData;
import main.homefinancemobile.model.TableRowData;

public abstract class TableActivity {

    final static String RECORD_ROW_PREFIX = "row_";

    /**
     * метод для добавления строки в таблицу
     * @param data
     * @param table
     * @param context
     */
    public static <T extends CommonTableData> void renderRow(T data, TableLayout table, final Context context) {
        final TableRow row = new TableRow(context);
        List<TableRowData> rowData = data.convertToTableRowData();
        for (int i = 0; i < rowData.size(); i++) {
            TextView text = new TextView(row.getContext());
            text.setLayoutParams(new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            text.setGravity(Gravity.LEFT);
            if (i != 0) {
                text.setPadding(convertToDp(context, 8), 0, 0, 0);
            }
            text.setText(rowData.get(i).getValue());
            text.setTextSize(14);
            text.setMinimumHeight(convertToDp(context, 30));
            text.setGravity(Gravity.CENTER_VERTICAL);
            text.setBackgroundResource(R.mipmap.row);
            row.setTag(RECORD_ROW_PREFIX + data.getId());
            row.setPadding(convertToDp(context, 16), 0, convertToDp(context, 16), 0);
            row.setClickable(true);
            row.addView(text);
        }
        table.addView(row);
    }

    /**
     * Конвертация в dp
     */
    public static int convertToDp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (px*scale + 0.5f);
        return dpAsPixels;
    }
}

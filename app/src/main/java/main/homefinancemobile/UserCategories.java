package main.homefinancemobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.form.CategoryFormDialog;
import main.homefinancemobile.fragments.category.ExpCategoryList;
import main.homefinancemobile.model.CategoryData;

public class UserCategories extends Fragment implements ExpCategoryList.CategoryClickListener {
    ExpandableListView categoryList;
    ExpandableListAdapter listAdapter;
    DBHelper dbHelper;
    FloatingActionButton addButton;

    private List<CategoryData> rootCategories = new ArrayList<>();
    private List<CategoryData> childCategories = new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String, List<CategoryData>> listDataChild;
    public UserCategories() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        addButton = view.findViewById(R.id.addRecord);

        // добавление записи
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        categoryList = view.findViewById(R.id.categoryList);
        categoryList.setDividerHeight(2);
        categoryList.setGroupIndicator(null);
        categoryList.setClickable(true);

        loadData();

        return view;
    }

    private void openDialog() {
        CategoryFormDialog categoryFormDialog = new CategoryFormDialog();
        categoryFormDialog.setTargetFragment(this, 1);
        categoryFormDialog.show(getFragmentManager().beginTransaction(), "category dialog");
    }
    private void openDialog(CategoryData data) {
        CategoryFormDialog categoryFormDialog = new CategoryFormDialog();
        categoryFormDialog.setTargetFragment(this, 1);
        Bundle args = new Bundle();
        args.putString("id", data.getId());
        args.putString("name", data.getName());
        args.putString("parent", data.getParentId());
        categoryFormDialog.setArguments(args);
        categoryFormDialog.show(getFragmentManager().beginTransaction(), "edit");
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        rootCategories.forEach(root -> {
            List<CategoryData> child = childCategories.stream().filter(item ->
                    item.getParentId().equals(root.getId())).collect(Collectors.toList());
            listDataChild.put(root.getName(), child);
        });
    }

    public static void sendResult(Context context, String id, String name, String parentId) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("parent_id", parentId);
        db.insert("Categories", null, cv);
    }

    public static void updateCategory(Context context, String id, String name) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("Categories", cv, "id = ?", new String[] { id });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            updateData();
        }
    }

    private void loadData() {
        rootCategories = new ArrayList<>();
        childCategories = new ArrayList<>();

        dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Categories", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<CategoryData>>();

            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int parentColIndex = c.getColumnIndex("parent_id");
            do {
                if (c.getString(parentColIndex) == null) {
                    listDataHeader.add(c.getString(nameColIndex));
                    rootCategories.add(new CategoryData(c.getString(idColIndex), c.getString(nameColIndex), null));
                } else {
                    childCategories.add(new CategoryData(c.getString(idColIndex), c.getString(nameColIndex), c.getString(parentColIndex)));
                }
            } while (c.moveToNext());

            prepareListData();

            listAdapter = new ExpCategoryList(getContext(), listDataHeader, listDataChild, this);
            categoryList.setAdapter(listAdapter);
        } else {
            TextView text = new TextView(this.getContext());
            text.setPadding(0, 40, 0, 0);
            text.setGravity(Gravity.CENTER);
            text.setText("No categories.");
            categoryList.addView(text);
        }
        dbHelper.close();
    }

    private void updateData() {
        Fragment frg = null;
        frg = this;
        final FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    public void onCategoryClick(int groupPosition, int childPosition) {
        CategoryData data = this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
        openDialog(data);
    }
}

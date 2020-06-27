package main.homefinancemobile.fragments.category;

import android.app.Activity;
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

import main.homefinancemobile.R;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.form.CategoryForm;
import main.homefinancemobile.model.CategoryData;

public class UserCategoriesFragment extends Fragment implements ExpCategoryList.CategoryClickListener {
    private ExpandableListView categoryList;
    private ExpandableListAdapter listAdapter;
    private DBHelper dbHelper;
    private FloatingActionButton addButton;

    private List<CategoryData> rootCategories = new ArrayList<>();
    private List<CategoryData> childCategories = new ArrayList<>();
    private List<String> listDataHeader;
    private HashMap<String, List<CategoryData>> listDataChild;
    public UserCategoriesFragment() {
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
        addButton.setOnClickListener(v -> openDialog());

        categoryList = view.findViewById(R.id.categoryList);
        categoryList.setDividerHeight(2);
        categoryList.setGroupIndicator(null);
        categoryList.setClickable(true);

        initData();

        return view;
    }

    private void initData() {
        rootCategories = new ArrayList<>();
        childCategories = new ArrayList<>();

        dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Categories", null, "del_date is null", null, null, null, null);
        if (c.moveToFirst()) {
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

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

    private void openDialog() {
        CategoryForm categoryForm = new CategoryForm();
        categoryForm.setTargetFragment(this, 1);
        categoryForm.show(getFragmentManager().beginTransaction(), "category dialog");
    }
    private void openDialog(CategoryData data) {
        CategoryForm categoryForm = new CategoryForm();
        categoryForm.setTargetFragment(this, 1);
        Bundle args = new Bundle();
        args.putString("id", data.getId());
        args.putString("name", data.getName());
        args.putString("parent", data.getParentId());
        categoryForm.setArguments(args);
        categoryForm.show(getFragmentManager().beginTransaction(), "edit");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            updateData();
        }
    }

    private void updateData() {
        Fragment frg;
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

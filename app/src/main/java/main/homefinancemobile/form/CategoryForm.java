package main.homefinancemobile.form;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import main.homefinancemobile.R;
import main.homefinancemobile.common.ConstVariables;
import main.homefinancemobile.model.CategoryData;

public class CategoryForm extends AppCompatDialogFragment {
    private TextInputEditText categoryName;
    private RadioGroup selectParentCategory;
    boolean editing = false;
    private CategoryData category;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.form_category_dialog, null);

        categoryName = view.findViewById(R.id.categoryNameFieldText);
        selectParentCategory = view.findViewById(R.id.parentCategory);
        category = new CategoryData();
        setEditing(getTag().equals(ConstVariables.EDIT));

        builder.setView(view)
                .setTitle("Создание категории")
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(editing ? R.string.edit : R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        accept();
                    }
                });
        if (editing) {
            builder.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAction();
                }
            });
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Инициализация данных в форме, если открыто редактирование
     * @param editing
     */
    private void setEditing(boolean editing) {
        this.editing = editing;
        if (editing) {
            Bundle args = getArguments();
            String id = args.getString("id");
            String name = args.getString("name");

            categoryName.setText(name);
            RadioButton incomeBtn = selectParentCategory.findViewById(R.id.income);
            RadioButton expenceBtn = selectParentCategory.findViewById(R.id.expence);
            if (args.getString("parent").equals(ConstVariables.INCOME_ID)) {
                incomeBtn.setChecked(true);
            } else {
                expenceBtn.setChecked(true);
            }
            incomeBtn.setEnabled(false);
            expenceBtn.setEnabled(false);

            category.setId(id);
            category.setName(name);
            category.setParentId(incomeBtn.isChecked() ? ConstVariables.INCOME_ID : ConstVariables.CONSUMPTION_ID);
        }
    }

    /**
     * Добавление или редактирование
     */
    private void accept() {
        category.setName(categoryName.getText().toString());
        if (editing) {
            category.updateCategory(getContext());
        } else {
            RadioButton btn = getView().findViewById(selectParentCategory.getCheckedRadioButtonId());
            String parentName = btn.getText().toString();
            category.setId(UUID.randomUUID().toString());
            category.setParentId(parentName.equals("Доход") ? ConstVariables.INCOME_ID : ConstVariables.CONSUMPTION_ID);
            category.addNewCategory(getContext());
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
    }

    private void deleteAction() {
        category.deleteCategory(getContext());
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
    }
}

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
import main.homefinancemobile.UserCategories;
import main.homefinancemobile.common.ConstVariables;
import main.homefinancemobile.utils.ParseDate;

public class CategoryFormDialog extends AppCompatDialogFragment {
    private String id;
    private TextInputEditText categoryName;
    private RadioGroup selectParentCategory;
    boolean editing = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.category_form_dialog, null);

        editing = getTag().equals(ConstVariables.EDIT);

        categoryName = view.findViewById(R.id.categoryNameFieldText);
        selectParentCategory = view.findViewById(R.id.parentCategory);

        if (editing) {
            Bundle args = getArguments();
            id = args.getString("id");
            categoryName.setText(args.getString("name"));
            RadioButton incomeBtn = selectParentCategory.findViewById(R.id.income);
            RadioButton expenceBtn = selectParentCategory.findViewById(R.id.expence);
            if (args.getString("parent").equals(ConstVariables.INCOME_ID)) {
                incomeBtn.setChecked(true);
            } else {
                expenceBtn.setChecked(true);
            }
            incomeBtn.setEnabled(false);
            expenceBtn.setEnabled(false);
        }

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
                        String name = categoryName.getText().toString();
                        if (editing) {
                            UserCategories.updateCategory(getContext(), id, name);
                        } else {
                            String id = UUID.randomUUID().toString();
                            RadioButton btn = (RadioButton) view.findViewById(selectParentCategory.getCheckedRadioButtonId());
                            String parentName = btn.getText().toString();
                            UserCategories.sendResult(getContext(), id, name, parentName.equals("Доход") ? ConstVariables.INCOME_ID : ConstVariables.CONSUMPTION_ID);
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                });
        if (editing) {
            builder.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}

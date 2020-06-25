package main.homefinancemobile.form;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import main.homefinancemobile.R;
import main.homefinancemobile.UserAccountFragment;
import main.homefinancemobile.common.ConstVariables;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.utils.Validate;

/**
 * Форма для Счета (добавление/редактирование)
 */
public class AccountForm extends AppCompatDialogFragment {
    public String id;
    public TextInputEditText accountName;
    public TextInputEditText accountBalance;
    View view;
    DBHelper dbHelper;
    boolean editing = false;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        editing = getTag().equals(ConstVariables.EDIT);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_account_form, null);

        accountName = view.findViewById(R.id.accountNameFieldText);
        accountBalance = view.findViewById(R.id.accountBalanceFieldText);
        if (editing) {
            Bundle args = getArguments();
            id = args.getString("id");
            accountName.setText(args.getString("name"));
            accountBalance.setText(args.getString("balance"));
            accountBalance.setEnabled(false);
        }
        builder.setView(view)
                .setTitle("Создание счета")
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(editing ? R.string.edit : R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accountName = view.findViewById(R.id.accountNameFieldText);
                accountBalance = view.findViewById(R.id.accountBalanceFieldText);
                if (validateForm(view)) {
                    try {
                        initValueForm();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                }
            }
        });
        if (editing) {
            builder.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserAccountFragment.deleteAccount(getContext(), id);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                }
            });
        }
        return builder.create();
    }


    /**
     * запись нового счета
     */
    private void initValueForm() throws ParseException {
        AccountData accountData = new AccountData(
                this.id == null ? UUID.randomUUID().toString() : this.id,
                accountName.getText().toString(),
                Float.parseFloat(accountBalance.getText().toString()),
                new Date(),
                new Date()
        );
        if (editing) {
            UserAccountFragment.editAccount(getContext(), accountData);
        } else {
            UserAccountFragment.addNewAccount(getContext(), accountData);
        }
    }

    private boolean validateForm(View view) {
        boolean valid = true;
        if (Validate.isEmpty(accountName.getText().toString())) {
            TextInputLayout accountNameContainer = view.findViewById(R.id.accountNameField);
            accountNameContainer.setHelperText("Обязательно для заполнения");
            accountNameContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            valid = false;
        }
        if (Validate.isEmpty(accountBalance.getText().toString())) {
            TextInputLayout accountBalanceContainer = view.findViewById(R.id.accountBalanceField);
            accountBalanceContainer.setHelperText("Обязательно для заполнения");
            accountBalanceContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            valid = false;
        }
        return valid;
    }
}

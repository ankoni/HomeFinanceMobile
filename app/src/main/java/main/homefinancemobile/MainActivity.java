package main.homefinancemobile;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import main.homefinancemobile.common.ConstVariables;
import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.fragments.account.UserAccountFragment;
import main.homefinancemobile.fragments.category.UserCategoriesFragment;
import main.homefinancemobile.fragments.dailybalance.DailyBalanceFragment;
import main.homefinancemobile.fragments.record.RecordTableFragment;
import main.homefinancemobile.fragments.setting.Setting;
import main.homefinancemobile.fragments.setting.SettingFragment;
import main.homefinancemobile.model.AccountData;

import static main.homefinancemobile.R.*;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DBHelper dbHelper;
    NavigationView navigation;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    TextView totalBalance;
    TextView userNameText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        changeTheme(db);
        setContentView(layout.activity_main);

        //меню
        navigation = findViewById(id.navigation);
        navigation.setNavigationItemSelectedListener(this);

        // создаем кнопку для открытия меню
        drawerLayout = findViewById(id.mainDrawerLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, string.open, string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //базовый фрагмент
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                    new RecordTableFragment()).commit();
            navigation.setCheckedItem(id.userRecords);
            setTitle(string.record_title);
        }

        Button settingBtn = navigation.getHeaderView(0).findViewById(id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                        new SettingFragment()).commit();
                drawerLayout.closeDrawer(navigation);
                setTitle(string.setting_title);
            }
        });
        userNameText = navigation.getHeaderView(0).findViewById(id.userName);
        setUserName(Setting.getSettingValue(db, ConstVariables.USER_NAME));



        totalBalance = navigation.getHeaderView(0).findViewById(id.totalBalance);
        setTotalBalance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case id.userRecords:
                getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                        new RecordTableFragment()).commit();
                setTitle(string.record_title);
                break;
            case id.userAccount:
                getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                        new UserAccountFragment()).commit();
                setTitle(string.account_title);
                break;
            case id.userCategory:
                getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                        new UserCategoriesFragment()).commit();
                setTitle(string.categories_title);
                break;
            case id.dailyBalance:
                getSupportFragmentManager().beginTransaction().replace(id.fragment_container,
                        new DailyBalanceFragment()).commit();
                setTitle(string.daily_balance_title);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Пересчет общего остатка и изменение текста на навигации
     */
    public void setTotalBalance() {
        totalBalance.setText(AccountData.getTotalBalance(dbHelper).toString() + "p.");
    }

    public void setUserName(String name) {
        userNameText.setText(name);
    }

    public void changeTheme(SQLiteDatabase db ) {
        if (Setting.getSettingValue(db, ConstVariables.DARK_MODE).equals("1")) {
            setTheme(style.DarkTheme);
        } else {
            setTheme(style.AppTheme);
        }
    }
}

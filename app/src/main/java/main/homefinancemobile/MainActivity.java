package main.homefinancemobile;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Objects;

import main.homefinancemobile.database.DBHelper;
import main.homefinancemobile.model.AccountData;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DBHelper dbHelper;
    NavigationView navigation;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    TextView totalBalance;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        //меню
        navigation = findViewById(R.id.navigation);
        navigation.setNavigationItemSelectedListener(this);

        // создаем кнопку для открытия меню
        drawerLayout = findViewById(R.id.mainDrawerLayout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //базовый фрагмент
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RecordTableFragment()).commit();
            navigation.setCheckedItem(R.id.userRecords);
            setTitle(R.string.record_title);
        }

        totalBalance = navigation.getHeaderView(0).findViewById(R.id.totalBalance);
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
            case R.id.userRecords:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RecordTableFragment()).commit();
                setTitle(R.string.record_title);
                break;
            case R.id.userAccount:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UserAccountFragment()).commit();
                setTitle(R.string.account_title);
                break;
            case R.id.userCategory:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UserCategories()).commit();
                setTitle(R.string.categories_title);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setTotalBalance() {
        totalBalance.setText(AccountData.getTotalBalance(dbHelper).toString() + "p.");
    }
}

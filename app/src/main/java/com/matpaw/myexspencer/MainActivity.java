package com.matpaw.myexspencer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.matpaw.myexspencer.cache.DataCache;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.PaymentType;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;
import com.matpaw.myexspencer.viewhandler.ExpenseViewHandler;
import com.matpaw.myexspencer.viewhandler.ExpensesViewHandler;
import com.matpaw.myexspencer.viewhandler.LimitViewHandler;
import com.matpaw.myexspencer.viewhandler.LimitsViewHandler;
import com.matpaw.myexspencer.viewhandler.StatusViewHandler;
import com.matpaw.myexspencer.write.DataWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;

    private StatusViewHandler statusViewHandler;
    private ExpensesViewHandler expensesViewHandler;
    private ExpenseViewHandler expenseViewHandler;
    private LimitsViewHandler limitsViewHandler;
    private LimitViewHandler limitViewHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        DataCache.init(getApplication());
        DataWriter.init(getApplication());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setTripAndCurrentDateInfo(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        initStatusViewHandler(viewFlipper);
        initExpenseViewHandler(viewFlipper);
        initExpensesViewHandler(viewFlipper);
        initLimitViewHandler(viewFlipper);
        initLimitsViewHandler(viewFlipper);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseViewHandler.flipToExpenseView();
            }
        });

        statusViewHandler.flipToStatusView();
    }

    private void initExpenseViewHandler(ViewFlipper viewFlipper) {
        expenseViewHandler = new ExpenseViewHandler(getApplicationContext(), MainActivity.this, viewFlipper, (LinearLayout) findViewById(R.id.expense_container));
    }

    private void initStatusViewHandler(ViewFlipper viewFlipper) {
        statusViewHandler = new StatusViewHandler(getApplicationContext(), viewFlipper, (ListView) findViewById(R.id.status_container));
    }

    private void initExpensesViewHandler(ViewFlipper viewFlipper) {
        expensesViewHandler = new ExpensesViewHandler(getApplicationContext(), viewFlipper, (ListView) findViewById(R.id.expenses_list_view), expenseViewHandler);
        expenseViewHandler.setExpensesViewHandler(expensesViewHandler);
    }

    private void initLimitViewHandler(ViewFlipper viewFlipper) {
        limitViewHandler = new LimitViewHandler(getApplicationContext(), MainActivity.this, viewFlipper, (LinearLayout) findViewById(R.id.limit_container));
    }

    private void initLimitsViewHandler(ViewFlipper viewFlipper) {
        limitsViewHandler = new LimitsViewHandler(getApplicationContext(), viewFlipper, (ListView) findViewById(R.id.limits_list_view), limitViewHandler);
        limitViewHandler.setLimitsViewHandler(limitsViewHandler);
    }

    private void setTripAndCurrentDateInfo(NavigationView navigationView) {
        TextView textView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView);
        Optional<Trip> activeTrip = DataReader.get().getActiveTrip();
        if(activeTrip.isPresent()) {
            textView.setText(activeTrip.get().getTitle() + "  |  " + getCurrentDate());
        } else {
            textView.setText("No active trips.");
        }
    }

    boolean backForExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            resetBackButton();
        } else if (expenseViewHandler.isExpenseViewActive()) {
            expensesViewHandler.flipToExpensesView();
            resetBackButton();
        } else if (expensesViewHandler.isExpensesViewActive()) {
            statusViewHandler.flipToStatusView();
            resetBackButton();
        } else if (limitsViewHandler.isExpensesViewActive()) {
            statusViewHandler.flipToStatusView();
            resetBackButton();
        }
        else if(limitViewHandler.isExpenseViewActive()) {
            limitsViewHandler.flipToLimitsView();
            resetBackButton();
        } else {
            if (!backForExitPressedOnce) {
                Toast.makeText(this, "Press back button once again to exit", Toast.LENGTH_SHORT).show();
                backForExitPressedOnce = true;
            } else {
                resetBackButton();
                super.onBackPressed();
            }
        }
    }

    private void resetBackButton() {
        backForExitPressedOnce = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        if (id == R.id.nav_status) {
            statusViewHandler.flipToStatusView();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expenseViewHandler.flipToExpenseView();
                }
            });
        } else if (id == R.id.nav_expenses) {
            expensesViewHandler.flipToExpensesView();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expenseViewHandler.flipToExpenseView();
                }
            });
        } else if (id == R.id.nav_limitations) {
            limitsViewHandler.flipToLimitsView();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    limitViewHandler.flipToLimitView();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }
}

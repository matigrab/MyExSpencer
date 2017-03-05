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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setTripAndCurrentDateInfo(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            flipToStatusView(viewFlipper);
        } else if (id == R.id.nav_expenses) {
            flipToExpensesView(viewFlipper);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void flipToStatusView(ViewFlipper viewFlipper) {
        ListView statusListView = (ListView) findViewById(R.id.status_container);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        statusListView.setAdapter(adapter);

        Date today = Dates.get(2017, 03, 02);

        adapter.add("----- ONLY TODAY -----");
        for (String statusPart : getStatusForDate(today)) {
            adapter.add(statusPart);
        }
        adapter.add("");

        adapter.add("----- TILL NOW -----");
        for (String statusPart : getStatusTillDate(today)) {
            adapter.add(statusPart);
        }
        adapter.add("");

        adapter.add("----- TILL END OF TRIP -----");
        for (String statusPart : getStatusTillDate(DataReader.get().getActiveTrip().get().getEndDate())) {
            adapter.add(statusPart);
        }
        adapter.add("");

        adapter.notifyDataSetChanged();
        viewFlipper.setDisplayedChild(0);
    }

    private List<String> getStatusTillDate(Date date) {
        Collection<Expense> expenses = DataReader.get().getExpensesForActiveTrip();
        Collection<Limit> limits = DataReader.get().getLimitsForActiveTrip();

        Collection<Expense> expensesBeforeOrTheSameDayAsDate = Lists.newArrayList();
        for (Expense expense : expenses) {
            if (expense.getDate().before(date) || Dates.theSameDay(expense.getDate(), date)) {
                expensesBeforeOrTheSameDayAsDate.add(expense);
            }
        }

        Collection<Limit> limitsBeforeOrTheSameDayAsDate = Lists.newArrayList();
        for (Limit limit : limits) {
            if(limit.getDate().before(date) || Dates.theSameDay(limit.getDate(), date)) {
                limitsBeforeOrTheSameDayAsDate.add(limit);
            }
        }

        return getStatusInternal(expensesBeforeOrTheSameDayAsDate, limitsBeforeOrTheSameDayAsDate);
    }

    private List<String> getStatusForDate(Date date) {
        Collection<Expense> expenses = DataReader.get().getExpensesForActiveTrip();
        Collection<Limit> limits = DataReader.get().getLimitsForActiveTrip();

        Collection<Expense> expensesForDate = Lists.newArrayList();
        for (Expense expense : expenses) {
            if (Dates.theSameDay(expense.getDate(), date)) {
                expensesForDate.add(expense);
            }
        }

        Collection<Limit> limitsForDate = Lists.newArrayList();
        for (Limit limit : limits) {
            if(Dates.theSameDay(limit.getDate(), date)) {
                limitsForDate.add(limit);
            }
        }

        return getStatusInternal(expensesForDate, limitsForDate);
    }

    private List<String> getStatusInternal(Collection<Expense> expenses, Collection<Limit> limits) {
        List<String> status = Lists.newArrayList();

        float sumOfExpensesThatConsumesLimitInEuro = 0f;
        float sumOfExpensesThatConsumesLimitInPLN = 0f;
        float sumOfExpensesThatOptionallyConsumesLimitInEuro = 0f;
        float sumOfExpensesThatOptionallyConsumesLimitInPLN = 0f;
        float lastExchangeRate = Constants.DEFAULT_EURO_EXCHANGE_RATE;
        boolean lastExchangeRateSet = false;

        for (Expense expense : expenses) {
            if (!lastExchangeRateSet) {
                lastExchangeRate = expense.getValueInPLN() / expense.getValueInEuro();
                lastExchangeRateSet = true;
            }

            if (LimitImpactType.CONSUMES.equals(expense.getLimitImpactType())) {
                sumOfExpensesThatConsumesLimitInEuro += expense.getValueInEuro();
                sumOfExpensesThatConsumesLimitInPLN += expense.getValueInPLN();
            }

            if (LimitImpactType.OPTIONAL.equals(expense.getLimitImpactType())) {
                sumOfExpensesThatOptionallyConsumesLimitInEuro += expense.getValueInEuro();
                sumOfExpensesThatOptionallyConsumesLimitInPLN += expense.getValueInPLN();
            }
        }

        sumOfExpensesThatOptionallyConsumesLimitInEuro += sumOfExpensesThatConsumesLimitInEuro;
        sumOfExpensesThatOptionallyConsumesLimitInPLN += sumOfExpensesThatConsumesLimitInPLN;

        status.add("EXPENSES : " +sumOfExpensesThatConsumesLimitInEuro + " Euro (" + sumOfExpensesThatConsumesLimitInPLN + " PLN)");
        status.add("OPTIONAL EXPENSES : " + sumOfExpensesThatOptionallyConsumesLimitInEuro + " Euro (" + sumOfExpensesThatOptionallyConsumesLimitInPLN + " PLN)");

        float limitsSum = 0f;
        for (Limit limit : limits) {
            limitsSum += limit.getValue();
        }

        status.add("LIMIT : " + limitsSum + " PLN (" + limitsSum/lastExchangeRate + " Euro)");

        float balanceInPLN = limitsSum - sumOfExpensesThatConsumesLimitInPLN;
        float balanceInEuro = (balanceInPLN == 0f) ? 0f : balanceInPLN / lastExchangeRate;
        float optionalBalanceInPLN = limitsSum - sumOfExpensesThatOptionallyConsumesLimitInPLN;
        float optionalBalanceInEuro = (optionalBalanceInPLN == 0f) ? 0f : optionalBalanceInPLN/lastExchangeRate;

        status.add("BALANCE : " + balanceInEuro + " Euro (" + balanceInPLN + " PLN)");
        status.add("OPTIONAL-BALANCE : " + optionalBalanceInEuro + " Euro (" + optionalBalanceInPLN + " PLN)");

        return status;
    }

    private void flipToExpensesView(ViewFlipper viewFlipper) {
        ListView expensesListView = (ListView) findViewById(R.id.expenses_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        expensesListView.setAdapter(adapter);
        Collection<Expense> expenses = DataReader.get().getExpensesForActiveTrip();
        for (Expense expense : expenses) {
            adapter.add(Dates.format(expense.getDate()) + " | " + expense.getExpenseType() + " | " + expense.getDescription());
        }
        adapter.notifyDataSetChanged();
        viewFlipper.setDisplayedChild(1);
    }

    public String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }
}

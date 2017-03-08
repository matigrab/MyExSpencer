package com.matpaw.myexspencer.viewhandler;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.google.common.collect.Lists;
import com.matpaw.myexspencer.Constants;
import com.matpaw.myexspencer.R;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StatusViewHandler {
    private ViewFlipper viewFlipper;
    private ListView statusContainer;
    private ArrayAdapter<String> adapter;

    public StatusViewHandler(final Context context, final ViewFlipper viewFlipper, final ListView statusContainer) {
        this.viewFlipper = viewFlipper;
        initAdapter(context, statusContainer);
    }

    private void initAdapter(final Context context, final ListView statusContainer) {
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        statusContainer.setAdapter(adapter);
    }

    public void flipToStatusView() {
        adapter.clear();

        Date today = new Date();

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

        BigDecimal sumOfExpensesThatConsumesLimitInEuro = BigDecimal.ZERO;
        BigDecimal sumOfExpensesThatConsumesLimitInPLN = BigDecimal.ZERO;
        BigDecimal sumOfExpensesThatOptionallyConsumesLimitInEuro = BigDecimal.ZERO;
        BigDecimal sumOfExpensesThatOptionallyConsumesLimitInPLN = BigDecimal.ZERO;

        for (Expense expense : expenses) {
            if (LimitImpactType.CONSUMES.equals(expense.getLimitImpactType())) {
                sumOfExpensesThatConsumesLimitInEuro.add(expense.getValueInEuro());
                sumOfExpensesThatConsumesLimitInPLN.add(expense.getValueInPLN());
            }

            if (LimitImpactType.OPTIONAL.equals(expense.getLimitImpactType())) {
                sumOfExpensesThatOptionallyConsumesLimitInEuro.add(expense.getValueInEuro());
                sumOfExpensesThatOptionallyConsumesLimitInPLN.add(expense.getValueInPLN());
            }
        }

        sumOfExpensesThatOptionallyConsumesLimitInEuro.add(sumOfExpensesThatConsumesLimitInEuro);
        sumOfExpensesThatOptionallyConsumesLimitInPLN.add(sumOfExpensesThatConsumesLimitInPLN);

        status.add("EXPENSES : " +sumOfExpensesThatConsumesLimitInEuro + " Euro (" + sumOfExpensesThatConsumesLimitInPLN + " PLN)");

        final BigDecimal limitsSum = BigDecimal.ZERO;;
        for (Limit limit : limits) {
            limitsSum.add(limit.getValue());
        }

        BigDecimal exchangeRate = DataReader.get().getEuroToPlnExchangeRate();

        status.add("LIMIT : " + limitsSum + " PLN (" + limitsSum.divide(exchangeRate) + " Euro)");

        BigDecimal balanceInPLN = limitsSum.subtract(sumOfExpensesThatConsumesLimitInPLN);
        BigDecimal balanceInEuro = balanceInPLN.divide(exchangeRate);
        BigDecimal optionalBalanceInPLN = limitsSum.subtract(sumOfExpensesThatOptionallyConsumesLimitInPLN);
        BigDecimal optionalBalanceInEuro = optionalBalanceInPLN.divide(exchangeRate);

        status.add("BALANCE : " + balanceInEuro + " Euro (" + balanceInPLN + " PLN)");

        if(!Objects.equals(sumOfExpensesThatConsumesLimitInPLN, sumOfExpensesThatOptionallyConsumesLimitInPLN)) {
            status.add("");
            status.add("OPTIONAL EXPENSES : " + sumOfExpensesThatOptionallyConsumesLimitInEuro + " Euro (" + sumOfExpensesThatOptionallyConsumesLimitInPLN + " PLN)");
            status.add("OPTIONAL BALANCE : " + optionalBalanceInEuro + " Euro (" + optionalBalanceInPLN + " PLN)");
        }
        return status;
    }
}

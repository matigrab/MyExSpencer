package com.matpaw.myexspencer.cache;

import android.app.Application;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.matpaw.myexspencer.Constants;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.ExpenseType;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.PaymentType;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.utils.Dates;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DataCache {
    private static DataCache dataCache;
    private static Application application;

    private static Set<Trip> trips = Sets.newHashSet();
    private static Set<Expense> expenses = Sets.newHashSet();
    private static Set<Limit> limits = Sets.newTreeSet();
    private static Multimap<UUID, UUID> tripToExpenses = HashMultimap.create();
    private static Multimap<UUID, UUID> tripToLimits = HashMultimap.create();

    private DataCache() {}

    public static void init(Application application) {
        DataCache.application = application;
    }

    public static DataCache get() {
        if(application == null) {
            throw new IllegalStateException("DataCache has to be initialized before usage!");
        }

        if(dataCache == null) {
            dataCache = new DataCache();
            dataCache.reload();
            dataCache.addMockedData(); //TODO: remove after reload() implementation
        }
        return dataCache;
    }

    public Collection<Trip> getTrips() {
        return trips;
    }

    public void reload() {
        UUID activeTripId = Constants.TRIP_TO_GENT_2017_ID;
        try {
            loadExpenses(activeTripId);
            loadLimits(activeTripId);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses(UUID activeTripId) throws IOException {
        expenses.clear();
        tripToExpenses.clear();

        FileInputStream fileInputStream = application.openFileInput(activeTripId.toString() + ".expenses");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(";");

            UUID id = UUID.fromString(split[0]);
            Date date = Dates.get(split[1]);
            Date additionDate = Dates.get(split[2]);
            String payer = split[3];
            String description = split[4];
            ExpenseType expenseType = ExpenseType.valueOf(split[5]);
            PaymentType paymentType = PaymentType.valueOf(split[6]);
            BigDecimal valueInEuro = new BigDecimal(split[7]);
            BigDecimal valueInPLN = new BigDecimal(split[8]);
            LimitImpactType limitImpactType = LimitImpactType.valueOf(split[9]);
            boolean bankConfirmation = Boolean.valueOf(split[10]);

            Expense expense = new Expense(id, date, additionDate, description, expenseType, payer, valueInEuro, valueInPLN, limitImpactType, bankConfirmation, paymentType);
            expenses.add(expense);
            tripToExpenses.put(activeTripId, expense.getId());
        }
        fileInputStream.close();
    }

    private void loadLimits(UUID activeTripId) throws IOException {
        limits.clear();
        tripToLimits.clear();

        FileInputStream fileInputStream = application.openFileInput(activeTripId.toString() + ".limits");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(";");

            UUID id = UUID.fromString(split[0]);
            Date date = Dates.get(split[1]);
            Date additionDate = Dates.get(split[1]);
            BigDecimal valueInPLN = new BigDecimal(split[2]);

            Limit limit = new Limit(id, date, additionDate, valueInPLN);
            limits.add(limit);
            tripToLimits.put(activeTripId, limit.getId());
        }
        fileInputStream.close();
    }

    private void addMockedData() {
        UUID tripId = Constants.TRIP_TO_GENT_2017_ID;
        trips.add(new Trip(tripId, "Gent 2017", Dates.get(2017, 03, 01), Dates.get(2017, 03, 30)));
    }

    public Collection<Expense> getExpensesForTrip(UUID tripId) {
        List<Expense> expensesForTrip = Lists.newArrayList();
        Collection<UUID> expensesIds = tripToExpenses.get(tripId);
        for (Expense expense : expenses) {
            if(expensesIds.contains(expense.getId())) {
                expensesForTrip.add(expense);
            }
        }
        Collections.sort(expensesForTrip);
        return expensesForTrip;
    }

    public Set<Limit> getLimitsForTrip(UUID tripId) {
        Set<Limit> limitsForTrip = Sets.newTreeSet();
        Collection<UUID> limitIds = tripToLimits.get(tripId);
        for (Limit limit : limits) {
            if(limitIds.contains(limit.getId())) {
                limitsForTrip.add(limit);
            }
        }
        return limitsForTrip;
    }
}

package com.matpaw.myexspencer.cache;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.ExpenseType;
import com.matpaw.myexspencer.model.LimitConsumptionType;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.utils.Dates;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataCache {
    private static DataCache dataCache;

    private static Set<Trip> trips = Sets.newHashSet();
    private static Set<Expense> expenses = Sets.newHashSet();
    private static Multimap<UUID, UUID> tripToExpenses = HashMultimap.create();

    private DataCache() {}

    public static DataCache get() {
        if(dataCache == null) {
            dataCache = new DataCache();
            dataCache.reload();
            dataCache.addTestData(); //TODO: remove after reload() implementation
        }
        return dataCache;
    }

    public Collection<Trip> getTrips() {
        return trips;
    }

    public void reload() {
        // TODO: reload from file
    }

    private void addTestData() {
        UUID tripToGent2017Id = UUID.randomUUID();
        trips.add(new Trip(tripToGent2017Id, "Gent 2017", Dates.get(2017, 03, 01), Dates.get(2017, 03, 15)));

        for(int i = 0; i <= 5; i++) {
            UUID id = UUID.randomUUID();
            Expense expense = new Expense(id, Dates.get(2017, 03, i + 1), "desc " + i, ExpenseType.DINNER, "mati", 1.0f + i, LimitConsumptionType.YES);
            expenses.add(expense);

            tripToExpenses.put(tripToGent2017Id, id);
        }
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
}

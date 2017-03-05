package com.matpaw.myexspencer.cache;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.ExpenseType;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.utils.Dates;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DataCache {
    private static DataCache dataCache;

    private static Set<Trip> trips = Sets.newHashSet();
    private static Set<Expense> expenses = Sets.newHashSet();
    private static Set<Limit> limits = Sets.newHashSet();
    private static Multimap<UUID, UUID> tripToExpenses = HashMultimap.create();
    private static Multimap<UUID, UUID> tripToLimits = HashMultimap.create();

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
        trips.add(new Trip(tripToGent2017Id, "Gent 2017", Dates.get(2017, 03, 01), Dates.get(2017, 03, 05)));

        for(int i = 1; i <= 2; i++) {
            UUID id = UUID.randomUUID();
            float valueInEuro = i;
            float valueInPLN = valueInEuro * 5;
            Expense expense = new Expense(id, Dates.get(2017, 03, i), "desc " + i, ExpenseType.DINNER, "mati", valueInEuro, valueInPLN, LimitImpactType.CONSUMES, false);
            expenses.add(expense);

            tripToExpenses.put(tripToGent2017Id, id);
        }

        UUID limit1Id = UUID.randomUUID();
        UUID limit2Id = UUID.randomUUID();
        limits.add(new Limit(limit1Id, Dates.get(2017, 03, 01), 90.0f));
        limits.add(new Limit(limit2Id, Dates.get(2017, 03, 02), 150.0f));
        limits.add(new Limit(limit2Id, Dates.get(2017, 03, 03), 150.0f));
        limits.add(new Limit(limit2Id, Dates.get(2017, 03, 04), 150.0f));
        limits.add(new Limit(limit2Id, Dates.get(2017, 03, 05), 150.0f));
        tripToLimits.put(tripToGent2017Id, limit1Id);
        tripToLimits.put(tripToGent2017Id, limit2Id);
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

    public Collection<Limit> getLimitsForTrip(UUID tripId) {
        List<Limit> limitsForTrip = Lists.newArrayList();
        Collection<UUID> limitIds = tripToLimits.get(tripId);
        for (Limit limit : limits) {
            if(limitIds.contains(limit.getId())) {
                limitsForTrip.add(limit);
            }
        }
        Collections.sort(limitsForTrip);
        return limitsForTrip;
    }
}

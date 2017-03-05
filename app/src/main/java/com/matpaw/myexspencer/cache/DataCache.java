package com.matpaw.myexspencer.cache;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.utils.Dates;

import java.util.Collection;
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
        }
        return dataCache;
    }

    public Collection<Trip> getTrips() {
        return trips;
    }

    public void reload() {
        UUID tripToGent2017Id = UUID.randomUUID();
        trips.add(new Trip(tripToGent2017Id, "Gent 2017", Dates.get(2017, 03, 01), Dates.get(2017, 03, 15)));
    }
}

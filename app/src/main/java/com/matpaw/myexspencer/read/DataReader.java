package com.matpaw.myexspencer.read;

import com.google.common.base.Optional;
import com.matpaw.myexspencer.cache.DataCache;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.utils.Dates;

import java.util.Collection;

public class DataReader {
    private static DataReader dataReader;

    public static DataReader get() {
        if(dataReader == null) {
            dataReader = new DataReader();
        }
        return dataReader;
    }

    public Collection<Trip> getAllTrips() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Optional<Trip> getActiveTrip() {
        return Optional.of(DataCache.get().getTrips().iterator().next());
    }
}

package com.matpaw.myexspencer.read;

import android.app.Application;

import com.google.common.base.Optional;
import com.matpaw.myexspencer.Constants;
import com.matpaw.myexspencer.cache.DataCache;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.utils.Dates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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

    public Collection<Expense> getExpensesForActiveTrip() {
        Optional<Trip> activeTrip = getActiveTrip();
        if(activeTrip.isPresent()) {
            return DataCache.get().getExpensesForTrip(activeTrip.get().getId());
        }
        return Collections.EMPTY_SET;
    }

    public Set<Limit> getLimitsForActiveTrip() {
        Optional<Trip> activeTrip = getActiveTrip();
        if(activeTrip.isPresent()) {
            return DataCache.get().getLimitsForTrip(activeTrip.get().getId());
        }
        return Collections.EMPTY_SET;
    }

    public BigDecimal getEuroToPlnExchangeRate() {
        for (Expense expense : getExpensesForActiveTrip()) {
            if(expense.isConfirmedByBank()) {
                return expense.getValueInPLN().divide(expense.getValueInEuro(), 2, RoundingMode.HALF_UP);
            }
        }
        return Constants.DEFAULT_EURO_EXCHANGE_RATE;
    }
}

package com.matpaw.myexspencer.write;

import android.app.Application;
import android.content.Context;

import com.google.common.collect.Lists;
import com.matpaw.myexspencer.cache.DataCache;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.read.DataReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class DataWriter {
    private static DataWriter dataWriter;
    private static Application application;

    public static void init(Application application) {
        DataWriter.application = application;
    }

    public static DataWriter get() {
        if(application == null) {
            throw new IllegalStateException("DataWriter has to be initialized before usage!");
        }

        if(dataWriter == null) {
            dataWriter = new DataWriter();
        }
        return dataWriter;
    }

    public void saveExpense(Expense expenseToSave) {
        Collection<Expense> expenses = DataReader.get().getExpensesForActiveTrip();
        Collection<Expense> expensesToSave = addOrUpdate(expenseToSave, expenses);

        saveExpenses(expensesToSave);
    }

    public void deleteExpense(Expense expenseToRemove) {
        Collection<Expense> expenses = DataReader.get().getExpensesForActiveTrip();
        Collection<Expense> expensesToSave = remove(expenseToRemove, expenses);

        saveExpenses(expensesToSave);
    }

    private void saveExpenses(Collection<Expense> expensesToSave) {
        UUID activeTripId = DataReader.get().getActiveTrip().get().getId();
        try {
            FileOutputStream fileOutputStream = application.openFileOutput(activeTripId.toString() + ".expenses", Context.MODE_PRIVATE);
            fileOutputStream.write(toCSV(expensesToSave).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataCache.get().reload();
    }

    private String toCSV(Collection<Expense> expenses) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Expense expense : expenses) {
            stringBuilder.append(expense.toString() + "\n");
        }
        return stringBuilder.toString();
    }

    private Collection<Expense> addOrUpdate(Expense expenseToSave, Collection<Expense> expenses) {
        Collection<Expense> expensesToSave = Lists.newArrayList();
        boolean updateAction = false;
        for (Expense expense : expenses) {
            if(expense.getId().equals(expenseToSave.getId())) {
                expensesToSave.add(expenseToSave);
                updateAction = true;
            } else {
                expensesToSave.add(expense);
            }
        }

        if(!updateAction) {
            expensesToSave.add(expenseToSave);
        }

        return expensesToSave;
    }

    private Collection<Expense> remove(Expense expenseToRemove, Collection<Expense> expenses) {
        Collection<Expense> expensesToSave = Lists.newArrayList(expenses);
        Iterator<Expense> iterator = expensesToSave.iterator();
        while (iterator.hasNext()) {
            Expense next = iterator.next();
            if(next.getId().equals(expenseToRemove.getId())) {
                iterator.remove();
            }
        }

        return expensesToSave;
    }
}

package com.matpaw.myexspencer.viewhandler;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.google.common.collect.Lists;
import com.matpaw.myexspencer.R;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;

import java.util.List;

public class ExpensesViewHandler {
    private ViewFlipper viewFlipper;
    private ListView expensesContainer;
    private ArrayAdapter<String> adapter;
    private ExpenseViewHandler expenseViewHandler;

    public ExpensesViewHandler(final Context context, final ViewFlipper viewFlipper, final ListView expensesContainer, final ExpenseViewHandler expenseViewHandler) {
        this.viewFlipper = viewFlipper;
        this.expensesContainer = expensesContainer;
        initAdapter(context);
        this.expenseViewHandler = expenseViewHandler;
    }

    private void initAdapter(final Context context) {
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        expensesContainer.setAdapter(adapter);
    }

    public void flipToExpensesView() {
        adapter.clear();

        final List<Expense> expenses = Lists.newArrayList(DataReader.get().getExpensesForActiveTrip());
        for (Expense expense : expenses) {
            String confirmedByBank = (expense.isConfirmedByBank()) ? "(C)" : "(~)";
            adapter.add(confirmedByBank + " | " + Dates.format(expense.getDate()) + " | " + expense.getExpenseType() + " | " + expense.getDescription());
        }
        adapter.notifyDataSetChanged();
        expensesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                expenseViewHandler.flipToExpenseView(expenses.get(position));
            }
        });
        viewFlipper.setDisplayedChild(1);
    }
}

package com.matpaw.myexspencer.viewhandler;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.matpaw.myexspencer.R;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.ExpenseType;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.PaymentType;
import com.matpaw.myexspencer.utils.Dates;
import com.matpaw.myexspencer.write.DataWriter;

import java.util.UUID;

public class ExpenseViewHandler {
    private Context context;
    private ViewFlipper viewFlipper;
    private LinearLayout expenseContainer;
    private ExpensesViewHandler expensesViewHandler;

    public ExpenseViewHandler(final Context context, final ViewFlipper viewFlipper, final LinearLayout expenseContainer) {
        this.context = context;
        this.viewFlipper = viewFlipper;
        this.expenseContainer = expenseContainer;
    }

    public void flipToExpenseView() {
        UUID newExpenseId = UUID.randomUUID();

        setAllFieldsToDefaultValues();

        initButtons(newExpenseId);
    }

    public void flipToExpenseView(final Expense expense) {
        EditText descriptionField = (EditText) expenseContainer.findViewById(R.id.expense_description);
        descriptionField.setText(expense.getDescription());

        initButtons(expense.getId());
    }

    private void setAllFieldsToDefaultValues() {
        EditText descriptionField = (EditText) expenseContainer.findViewById(R.id.expense_description);
        descriptionField.setText("DEFAULT VALUE");
    }

    private void initButtons(final UUID expenseId) {
        Button saveButton = (Button) expenseContainer.findViewById(R.id.save_expense);
        Button cancelButton = (Button) expenseContainer.findViewById(R.id.cancel);
        Button deleteButton = (Button) expenseContainer.findViewById(R.id.delete_expense);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataWriter.get().saveExpense(getExpense(expenseId));
                expensesViewHandler.flipToExpensesView();
                Toast.makeText(context, "Expense saved.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataWriter.get().deleteExpense(getExpense(expenseId));
                expensesViewHandler.flipToExpensesView();
                Toast.makeText(context, "Expense removed.", Toast.LENGTH_SHORT).show();
            }
        });

        viewFlipper.setDisplayedChild(2);
    }

    private Expense getExpense(UUID expenseId) {
        EditText descriptionField = (EditText) expenseContainer.findViewById(R.id.expense_description);

        Expense expense = new Expense(expenseId, Dates.get(2017, 03, 02), descriptionField.getText().toString(), ExpenseType.DINNER, "mati", 1f, 5f, LimitImpactType.CONSUMES, false, PaymentType.CASH);
        return expense;
    }

    public void setExpensesViewHandler(ExpensesViewHandler expensesViewHandler) {
        this.expensesViewHandler = expensesViewHandler;
    }
}

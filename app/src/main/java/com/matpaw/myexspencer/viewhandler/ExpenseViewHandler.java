package com.matpaw.myexspencer.viewhandler;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.matpaw.myexspencer.R;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.ExpenseType;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.PaymentType;
import com.matpaw.myexspencer.write.DataWriter;

import java.util.Date;
import java.util.UUID;

public class ExpenseViewHandler {
    private Context context;
    private ViewFlipper viewFlipper;
    private LinearLayout expenseContainer;
    private ExpensesViewHandler expensesViewHandler;

    private Spinner expenseTypeSpinner;
    private Spinner paymentTypeSpinner;
    private Spinner limitImpactTypeSpinner;
    private CalendarView calendarView;
    private EditText descriptionEditText;
    private CheckBox disableAutoCurrencyExchangeCheckBox;
    private EditText valueInEuroEditText;
    private EditText valueInPLNEditText;
    private CheckBox bankConfirmationCheckBox;

    public ExpenseViewHandler(final Context context, final ViewFlipper viewFlipper, final LinearLayout expenseContainer) {
        this.context = context;
        this.viewFlipper = viewFlipper;
        this.expenseContainer = expenseContainer;

        initFields();
    }

    private void initFields() {
        calendarView = (CalendarView) expenseContainer.findViewById(R.id.expense_calendar);
        expenseTypeSpinner = (Spinner) expenseContainer.findViewById(R.id.expense_type);
        paymentTypeSpinner = (Spinner) expenseContainer.findViewById(R.id.expense_payment_type);
        limitImpactTypeSpinner = (Spinner) expenseContainer.findViewById(R.id.expense_limit_impact_type);
        descriptionEditText = (EditText) expenseContainer.findViewById(R.id.expense_description);
        disableAutoCurrencyExchangeCheckBox = (CheckBox) expenseContainer.findViewById(R.id.expense_disable_auto_currency_exchange);
        valueInEuroEditText = (EditText) expenseContainer.findViewById(R.id.expense_value_in_euro);
        valueInPLNEditText = (EditText) expenseContainer.findViewById(R.id.expense_value_in_pln);
        bankConfirmationCheckBox = (CheckBox) expenseContainer.findViewById(R.id.expense_bank_confirmation);

        initSpinner(expenseTypeSpinner, ExpenseType.values());
        initSpinner(paymentTypeSpinner, PaymentType.values());
        initSpinner(limitImpactTypeSpinner, LimitImpactType.values());
    }

    private void initSpinner(Spinner spinner, Object[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        for (Object type : values) {
            adapter.add(type.toString());
        }
        adapter.notifyDataSetChanged();
    }

    public void flipToExpenseView() {
        UUID newExpenseId = UUID.randomUUID();

        setAllFieldsToDefaultValues();

        initButtons(newExpenseId);
    }

    public void flipToExpenseView(final Expense expense) {
        descriptionEditText.setText(expense.getDescription());
        expenseTypeSpinner.setSelection(ExpenseType.index(expense.getExpenseType()));
        paymentTypeSpinner.setSelection(PaymentType.index(expense.getPaymentType()));
        limitImpactTypeSpinner.setSelection(LimitImpactType.index(expense.getLimitImpactType()));
        calendarView.setDate(expense.getDate().getTime());
        valueInEuroEditText.setText(""+expense.getValueInEuro());
        valueInPLNEditText.setText(""+expense.getValueInPLN());
        disableAutoCurrencyExchangeCheckBox.setChecked(false);
        bankConfirmationCheckBox.setChecked(expense.isConfirmedByBank());

        initButtons(expense.getId());
    }

    private void setAllFieldsToDefaultValues() {
        descriptionEditText.setText("");
        expenseTypeSpinner.setSelection(0);
        paymentTypeSpinner.setSelection(0);
        limitImpactTypeSpinner.setSelection(0);
        calendarView.setDate(new Date().getTime());
        valueInEuroEditText.setText("");
        valueInPLNEditText.setText("");
        disableAutoCurrencyExchangeCheckBox.setChecked(false);
        bankConfirmationCheckBox.setChecked(false);
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
        Date date = new Date(calendarView.getDate());
        String description = descriptionEditText.getText().toString();
        ExpenseType expenseType = ExpenseType.values()[expenseTypeSpinner.getSelectedItemPosition()];
        PaymentType paymentType = PaymentType.values()[paymentTypeSpinner.getSelectedItemPosition()];
        LimitImpactType limitImpactType = LimitImpactType.values()[limitImpactTypeSpinner.getSelectedItemPosition()];
        Float valueInEuro = Float.valueOf(valueInEuroEditText.getText().toString());
        Float valueInPLN = Float.valueOf(valueInPLNEditText.getText().toString());
        boolean bankConfirmation = bankConfirmationCheckBox.isChecked();

        return new Expense(expenseId, date, new Date(), description, expenseType, "user", valueInEuro, valueInPLN, limitImpactType, bankConfirmation, paymentType);
    }

    public void setExpensesViewHandler(ExpensesViewHandler expensesViewHandler) {
        this.expensesViewHandler = expensesViewHandler;
    }
}

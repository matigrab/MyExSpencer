package com.matpaw.myexspencer.viewhandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
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

import com.matpaw.myexspencer.MainActivity;
import com.matpaw.myexspencer.R;
import com.matpaw.myexspencer.model.Expense;
import com.matpaw.myexspencer.model.ExpenseType;
import com.matpaw.myexspencer.model.LimitImpactType;
import com.matpaw.myexspencer.model.PaymentType;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;
import com.matpaw.myexspencer.write.DataWriter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static com.matpaw.myexspencer.Constants.DEFAULT_SCALE;

public class ExpenseViewHandler {
    private Context context;
    private MainActivity mainActivity;
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
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;

    private Date dateFromCalendarView;

    public ExpenseViewHandler(final Context context, final MainActivity mainActivity, final ViewFlipper viewFlipper, final LinearLayout expenseContainer) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.viewFlipper = viewFlipper;
        this.expenseContainer = expenseContainer;

        initFields();
    }

    public boolean isExpenseViewActive() {
        return viewFlipper.getDisplayedChild() == 2;
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
        saveButton = (Button) expenseContainer.findViewById(R.id.save_expense);
        cancelButton = (Button) expenseContainer.findViewById(R.id.cancel);
        deleteButton = (Button) expenseContainer.findViewById(R.id.delete_expense);

        initSpinner(expenseTypeSpinner, ExpenseType.values());
        initSpinner(paymentTypeSpinner, PaymentType.values());
        initSpinner(limitImpactTypeSpinner, LimitImpactType.values());

        dateFromCalendarView = new Date();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateFromCalendarView = Dates.get(year, month, dayOfMonth);
            }
        });

        valueInEuroEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(!disableAutoCurrencyExchangeCheckBox.isChecked()) {
                        String valueInEuro = valueInEuroEditText.getText().toString();
                        if(validatePaymentValue("Euro", valueInEuro)) {
                            BigDecimal valueInEuroFloat = new BigDecimal(valueInEuro);
                            BigDecimal valueInPLNAfterExchange = valueInEuroFloat.multiply(DataReader.get().getEuroToPlnExchangeRate());
                            valueInPLNEditText.setText("" + valueInPLNAfterExchange.setScale(DEFAULT_SCALE, BigDecimal.ROUND_HALF_EVEN));
                        }
                    }
                }
            }
        });

        valueInPLNEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(!disableAutoCurrencyExchangeCheckBox.isChecked()) {
                        String valueInPLN = valueInPLNEditText.getText().toString();
                        if(validatePaymentValue("PLN", valueInPLN)) {
                            BigDecimal valueInPLNFloat = new BigDecimal(valueInPLN);
                            BigDecimal valueInEuroAfterExchange = valueInPLNFloat.divide(DataReader.get().getEuroToPlnExchangeRate());
                            valueInEuroEditText.setText("" + valueInEuroAfterExchange.setScale(DEFAULT_SCALE, BigDecimal.ROUND_HALF_EVEN));
                        }
                    }
                }
            }
        });
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
        deleteButton.setVisibility(View.GONE);
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = validateDataFromFields();

                if(valid) {
                    DataWriter.get().saveExpense(getExpense(expenseId));
                    expensesViewHandler.flipToExpensesView();
                    Toast.makeText(context, "Expense saved.", Toast.LENGTH_SHORT).show();
                }
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
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Delete expense")
                        .setMessage("Do you really want to delete expense?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DataWriter.get().deleteExpense(getExpense(expenseId));
                                expensesViewHandler.flipToExpensesView();
                                Toast.makeText(context, "Expense removed.", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null)
                        .show();

            }
        });
        deleteButton.setVisibility(View.VISIBLE);

        viewFlipper.setDisplayedChild(2);
    }

    private boolean validateDataFromFields() {
        String description = descriptionEditText.getText().toString();
        if(description.isEmpty()) {
            showInvalidFieldValueMessage("Description can not be empty!");
            return false;
        }

        if(!validatePaymentValue("Euro", valueInEuroEditText.getText().toString())) {
            return false;
        }

        if(!validatePaymentValue("PLN", valueInPLNEditText.getText().toString())) {
            return false;
        }

        Trip activeTrip = DataReader.get().getActiveTrip().get();
        if(dateFromCalendarView.before(activeTrip.getStartDate()) || dateFromCalendarView.after(activeTrip.getEndDate())) {
            showInvalidFieldValueMessage("Expense date is not in trip timerange!");
            return false;
        }

        return true;
    }

    private boolean validatePaymentValue(String fieldName, String value) {
        try {
            if(value.isEmpty()) {
                showInvalidFieldValueMessage("Value in " + fieldName + " can not be empty!");
                return false;
            }

            Float valueInEuro = Float.valueOf(value);
            if(valueInEuro <= 0f) {
                showInvalidFieldValueMessage("Value in " + fieldName + " has to be greather than 0.");
                return false;
            }
        } catch (NumberFormatException ex) {
            showInvalidFieldValueMessage("Value in " + fieldName + " is not numeric value!");
            return false;
        }
        return true;
    }

    private void showInvalidFieldValueMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private Expense getExpense(UUID expenseId) {
        String description = descriptionEditText.getText().toString();
        ExpenseType expenseType = ExpenseType.values()[expenseTypeSpinner.getSelectedItemPosition()];
        PaymentType paymentType = PaymentType.values()[paymentTypeSpinner.getSelectedItemPosition()];
        LimitImpactType limitImpactType = LimitImpactType.values()[limitImpactTypeSpinner.getSelectedItemPosition()];
        BigDecimal valueInEuro = new BigDecimal(valueInEuroEditText.getText().toString());
        BigDecimal valueInPLN = new BigDecimal(valueInPLNEditText.getText().toString());
        boolean bankConfirmation = bankConfirmationCheckBox.isChecked();

        return new Expense(expenseId, dateFromCalendarView, new Date(), description, expenseType, "user", valueInEuro, valueInPLN, limitImpactType, bankConfirmation, paymentType);
    }

    public void setExpensesViewHandler(ExpensesViewHandler expensesViewHandler) {
        this.expensesViewHandler = expensesViewHandler;
    }
}

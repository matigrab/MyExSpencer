package com.matpaw.myexspencer.viewhandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.matpaw.myexspencer.MainActivity;
import com.matpaw.myexspencer.R;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.model.Trip;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;
import com.matpaw.myexspencer.write.DataWriter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class LimitViewHandler {
    private Context context;
    private MainActivity mainActivity;
    private ViewFlipper viewFlipper;
    private LinearLayout limitContainer;
    private LimitsViewHandler limitsViewHandler;

    private CalendarView calendarView;
    private EditText valueInPLNEditText;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;

    private Date dateFromCalendarView;

    public LimitViewHandler(final Context context, final MainActivity mainActivity, final ViewFlipper viewFlipper, final LinearLayout limitContainer) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.viewFlipper = viewFlipper;
        this.limitContainer = limitContainer;

        initFields();
    }

    public boolean isExpenseViewActive() {
        return viewFlipper.getDisplayedChild() == 4;
    }

    private void initFields() {
        calendarView = (CalendarView) limitContainer.findViewById(R.id.limit_calendar);
        valueInPLNEditText = (EditText) limitContainer.findViewById(R.id.limit_value_in_pln);
        saveButton = (Button) limitContainer.findViewById(R.id.save_limit);
        cancelButton = (Button) limitContainer.findViewById(R.id.cancel_from_limit);
        deleteButton = (Button) limitContainer.findViewById(R.id.delete_limit);

        dateFromCalendarView = new Date();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateFromCalendarView = Dates.get(year, month + 1, dayOfMonth);
            }
        });
    }

    public void flipToLimitView() {
        UUID newLimitId = UUID.randomUUID();

        setAllFieldsToDefaultValues();

        initButtons(newLimitId);
        deleteButton.setVisibility(View.GONE);
    }

    public void flipToLimitView(final Limit limit) {
        calendarView.setDate(limit.getDate().getTime());
        valueInPLNEditText.setText(""+limit.getValue());
        dateFromCalendarView = limit.getDate();

        initButtons(limit.getId());
    }

    private void setAllFieldsToDefaultValues() {
        calendarView.setDate(new Date().getTime());
        valueInPLNEditText.setText("");
        dateFromCalendarView = new Date();
    }

    private void initButtons(final UUID limitId) {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = validateDataFromFields();

                if(valid) {
                    DataWriter.get().saveLimit(getLimit(limitId));
                    limitsViewHandler.flipToLimitsView();
                    Toast.makeText(context, "Limit saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(3);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Delete limit")
                        .setMessage("Do you really want to delete limit?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DataWriter.get().deleteLimit(getLimit(limitId));
                                limitsViewHandler.flipToLimitsView();
                                Toast.makeText(context, "Limit removed.", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null)
                        .show();

            }
        });
        deleteButton.setVisibility(View.VISIBLE);

        viewFlipper.setDisplayedChild(4);
    }

    private boolean validateDataFromFields() {
        if(!validatePaymentValue("PLN", valueInPLNEditText.getText().toString())) {
            return false;
        }

        Trip activeTrip = DataReader.get().getActiveTrip().get();
        if(dateFromCalendarView.before(activeTrip.getStartDate()) || dateFromCalendarView.after(activeTrip.getEndDate())) {
            showInvalidFieldValueMessage("Limit date is not in trip timerange!");
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

    private Limit getLimit(UUID limitId) {
        BigDecimal valueInPLN = new BigDecimal(valueInPLNEditText.getText().toString());

        return new Limit(limitId, dateFromCalendarView, new Date(), valueInPLN);
    }

    public void setLimitsViewHandler(LimitsViewHandler limitsViewHandler) {
        this.limitsViewHandler = limitsViewHandler;
    }
}

package com.matpaw.myexspencer.viewhandler;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.google.common.collect.Lists;
import com.matpaw.myexspencer.model.Limit;
import com.matpaw.myexspencer.read.DataReader;
import com.matpaw.myexspencer.utils.Dates;

import java.util.List;

public class LimitsViewHandler {
    private ViewFlipper viewFlipper;
    private ListView limitsContainer;
    private ArrayAdapter<String> adapter;
    private LimitViewHandler limitViewHandler;

    public LimitsViewHandler(final Context context, final ViewFlipper viewFlipper, final ListView limitsContainer, final LimitViewHandler limitViewHandler) {
        this.viewFlipper = viewFlipper;
        this.limitsContainer = limitsContainer;
        initAdapter(context);
        this.limitViewHandler = limitViewHandler;
    }

    public boolean isExpensesViewActive() {
        return viewFlipper.getDisplayedChild() == 3;
    }

    private void initAdapter(final Context context) {
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        limitsContainer.setAdapter(adapter);
    }

    public void flipToLimitsView() {
        adapter.clear();

        final List<Limit> limits = Lists.newArrayList(DataReader.get().getLimitsForActiveTrip());
        for (Limit limit : limits) {
            adapter.add(Dates.format(limit.getDate()) + " | " + limit.getValue());
        }
        adapter.notifyDataSetChanged();
        limitsContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                limitViewHandler.flipToLimitView(limits.get(position));
            }
        });
        viewFlipper.setDisplayedChild(3);
    }
}

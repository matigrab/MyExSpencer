package com.matpaw.myexspencer.model;

import java.util.Date;
import java.util.UUID;

public class Trip {
    private UUID id;
    private String title;
    private Date startDate;
    private Date endDate;

    public Trip(UUID id, String title, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}

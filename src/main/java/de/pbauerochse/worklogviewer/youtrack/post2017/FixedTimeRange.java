package de.pbauerochse.worklogviewer.youtrack.post2017;

import java.time.LocalDate;
import java.time.ZoneId;

public class FixedTimeRange implements Post2017ReportRange {

    private final LocalDate startDate;
    private final LocalDate endDate;

    FixedTimeRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getFrom() {
        return startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public long getTo() {
        return endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

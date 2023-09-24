package com.pmk.notif.utils;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Service
public class GetCurrentTimeService {

    public Timestamp getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 7);
        return new Timestamp(calendar.getTimeInMillis());
    }

}

package com.pmk.notif.utils;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Service
public class GetCurrentTimeService {

    public Timestamp getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 7);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static LocalDateTime[] getCurrentDayRange() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23:59:59.999999999
        return new LocalDateTime[]{startOfDay, endOfDay};
    }

    public HashMap<String, String> inString() {

        HashMap<String, String> result = new HashMap<>();


        try {

            // Get the current date
            LocalDate date = LocalDate.now();

            // Create LocalDateTime for start and end of the day
            LocalDateTime startOfDay = date.atStartOfDay(); // Current date 00:00:00
            LocalDateTime endOfDay = date.atTime(23, 59, 59); // Current date 23:59:59

            // Define the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Format the timestamps
            String startTimestamp = startOfDay.format(formatter);
            String endTimestamp = endOfDay.format(formatter);

            // Print the formatted timestamps
            System.out.println("Start of the day: " + startTimestamp);
            result.put("start_date", startTimestamp);
            System.out.println("End of the day: " + endTimestamp);
            result.put("end_date", endTimestamp);


        } catch (Exception e) {
            System.out.println("Error in generate current timestatamp");
            System.out.println(e.getLocalizedMessage());
        }
        return result;
    }

}

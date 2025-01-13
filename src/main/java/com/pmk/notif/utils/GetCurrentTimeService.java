package com.pmk.notif.utils;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
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
        ZoneId localZoneId = ZoneId.systemDefault(); // Get the machine's local time zone
        LocalDate today = LocalDate.now(localZoneId); // Use the local time zone for the current date
        LocalDateTime startOfDay = today.atStartOfDay(); // Start of the day
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // End of the day

        // Convert to ZonedDateTime in the local time zone
        ZonedDateTime startOfDayZoned = startOfDay.atZone(localZoneId);
        ZonedDateTime endOfDayZoned = endOfDay.atZone(localZoneId);

        // Return the adjusted LocalDateTime values
        return new LocalDateTime[]{
                startOfDayZoned.toLocalDateTime(),
                endOfDayZoned.toLocalDateTime()
        };
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

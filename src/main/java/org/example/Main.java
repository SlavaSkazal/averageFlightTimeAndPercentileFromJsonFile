package org.example;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        try (FileReader reader = new FileReader("src/main/resources/tickets.json")) {
            try  {
                JSONObject ticketsJSON = (JSONObject) new JSONParser().parse(reader);
                reader.close();
                JSONArray ticketsJsonArr = (JSONArray) ticketsJSON.get("tickets");
                Object[] ticketsArr = ticketsJsonArr.toArray();

                long[] averageTimeSecArr = getTimeFlightsArr(ticketsArr);
                long averageTimeSec = calculateAverageTime(averageTimeSecArr);
                long procentil = calculateProcentil(averageTimeSecArr, 90);

                System.out.printf("Average time is %d hours %d minutes\n", averageTimeSec / 3600, (averageTimeSec % 3600) / 60);
                System.out.printf("Procentil is %d hours %d minutes\n", procentil / 3600, (procentil % 3600) / 60);
            } catch (ParseException e) {
                throw new ParseException(e.getErrorType());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static long[] getTimeFlightsArr(Object[] ticketsArr) throws java.text.ParseException {
        long durationFlightsArr[] = new long[ticketsArr.length];
        int i = 0;
        SimpleDateFormat sdfDateForm = new SimpleDateFormat("dd.MM.yy");
        SimpleDateFormat sdfTimeForm = new SimpleDateFormat("HH:mm");

        try {
            for (i = 0; i < ticketsArr.length; i++) {
                JSONObject ticket =  (JSONObject) ticketsArr[i];

                if (ticket.get("origin").toString().equals("VVO") && ticket.get("destination").toString().equals("TLV")) {
                    Date deptDate = sdfDateForm.parse(ticket.get("departure_date").toString());
                    Date deptTime = sdfTimeForm.parse(ticket.get("departure_time").toString());
                    Date arDate = sdfDateForm.parse(ticket.get("arrival_date").toString());
                    Date arTime = sdfTimeForm.parse(ticket.get("arrival_time").toString());

                    durationFlightsArr[i] = Duration.between(deptDate.toInstant(), arDate.toInstant()).toSeconds()
                            + Duration.between(deptTime.toInstant(), arTime.toInstant()).toSeconds();
                }
            }
        } catch (java.text.ParseException e) {
            throw new java.text.ParseException(e.getMessage(), i);
        }
        return durationFlightsArr;
    }

    public static long calculateAverageTime(long[] averageTimeSecArr) {
        long durationOfAllFlights = 0;

        for (int i = 0; i < averageTimeSecArr.length; i++) {
            durationOfAllFlights += averageTimeSecArr[i];
        }
        return (durationOfAllFlights / Long.valueOf(averageTimeSecArr.length));
    }
    public static long calculateProcentil(long[] averageTimeSecArr, int procentil) {
        Arrays.sort(averageTimeSecArr);
        int procentilInd = (int)(((double)procentil / 100.0) * (double)averageTimeSecArr.length);
        return averageTimeSecArr[procentilInd-1];
    }
}
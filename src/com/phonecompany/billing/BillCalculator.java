package com.phonecompany.billing;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillCalculator implements TelephoneBillCalculator{

    private final int MINUTE_PRICE_IN_PEAK = 1;
    private final double MINUTE_PRICE_OUTSIDE_PEAK = 0.5D;
    private final double REDUCED_MINUTE_PRICE = 0.2D;
    private final int MINUTES_FOR_REDUCED_PRICE = 5;

    @Override
    public BigDecimal calculate(String phoneLog) {
        List<PhoneCall> phoneCallList = getPhoneCalls(phoneLog);
        return getTotalPhoneCallCost(phoneCallList);
    }

   // param phoneLog Řetězec s telefonním záznamem ze souboru csv
   // return Seznam rozebraných telefonních hovorů
 
    private List<PhoneCall> getPhoneCalls(String phoneLog){
        List<PhoneCall> phoneCallList = new ArrayList<>();

        //Split phone log by lines
        String[] phoneLogRows = phoneLog.split("\n");
        for(String row : phoneLogRows){
            String[] rowToParse = row.split(",");

            long phoneNumber = parseLongNumber(rowToParse[0]);
            Date startDate = parseDate(rowToParse[1]);
            Date endDate = parseDate(rowToParse[2]);

            phoneCallList.add(new PhoneCall(phoneNumber, startDate, endDate));
        }

        return phoneCallList;
    }

    // Pomocná metoda pro analýzu dlouhých čísel
   // param number Řetězec s dlouhým číslem
   // return Parsované dlouhé číslo v případě úspěchu, jinak nula

    private long parseLongNumber(String number){
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    // Pomocná metoda pro parsování data
    // param date Řetězec s datem ve formátu (dd-MM-yyyy HH:mm:ss)
    // return Parsované datum v případě úspěchu, jinak null

    private Date parseDate(String date){
        try {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    //Získání nejčastěji volaného čísla ze seznamu telefonních hovorů
    //param phoneCallList Seznam s telefonními hovory
    ///return Číslo, které je nejčastěji volané

    private long getMostCalledNumber(List<PhoneCall> phoneCallList){
    	 //Vytvoření hashmapy s počítadlem výskytů
        HashMap<Long, Integer> numberCounter = new HashMap<>();
        int maxCount = 0;
        long maxCountNumber = 0L;

        for (PhoneCall call : phoneCallList){
        	 //Přidat jedničku k počítadlu volání
            numberCounter.compute(call.getNumber(), (key, value) -> (value == null) ? 1 : value + 1);
            int numberCount = numberCounter.get(call.getNumber());

            //Kontrola, zda je čítač volání čísla větší nebo zda je čítač stejný a číslo má větší aritmetickou hodnotu
            if(numberCount > maxCount || (numberCount == maxCount && call.getNumber() > maxCountNumber)){
                maxCount = numberCount;
                maxCountNumber = call.getNumber();
            }
        }

        return maxCountNumber;
    }

 
    //return Doba trvání telefonního hovoru v minutách zaokrouhlených nahoru
   
    private int getCallDurationInMinutes(PhoneCall call){
        return (int) Math.ceil((call.getEndDate().getTime() - call.getStartDate().getTime()) / 1000.0 / 60.0);
    }

    //  Pomocná metoda pro získání data hodiny
  
    private int getHourFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    
    // Výpočet účtu za volání
  // vrátit cenu telefonního hovoru
    
    private BigDecimal getPhoneCallCost(PhoneCall call){
        int callStartHour = getHourFromDate(call.getStartDate());
        int callDuration = getCallDurationInMinutes(call);

        double priceToUse = (callStartHour >= 8 && callStartHour < 16) ? MINUTE_PRICE_IN_PEAK : MINUTE_PRICE_OUTSIDE_PEAK;

        if(callDuration > MINUTES_FOR_REDUCED_PRICE){
            return BigDecimal.valueOf(priceToUse * MINUTES_FOR_REDUCED_PRICE + (callDuration - MINUTES_FOR_REDUCED_PRICE) * REDUCED_MINUTE_PRICE);
        }else {
            return BigDecimal.valueOf(priceToUse * callDuration);
        }
    }


    // Získat účet za seznam telefonních hovorů
 //   @param phoneCallList Seznam objektů telefonních hovorů
  //  return Cena za seznam telefonních hovorů
    private BigDecimal getTotalPhoneCallCost(List<PhoneCall> phoneCallList){
        BigDecimal total = new BigDecimal(0);
        long mostCalledNumber = getMostCalledNumber(phoneCallList);

        for (PhoneCall phoneCall : phoneCallList){
        	//Nejčastěji volané číslo je zdarma
            if(phoneCall.getNumber() == mostCalledNumber){
                continue;
            }

            total = total.add(getPhoneCallCost(phoneCall));
        }

        return total;
    }

}
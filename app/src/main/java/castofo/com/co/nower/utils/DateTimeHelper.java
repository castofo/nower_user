package castofo.com.co.nower.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alejandrosanchezaristizabal on 12/4/17.
 */

public class DateTimeHelper {

  private static Calendar calendar;
  private static SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private static String date;

  public static void getCurrentTime() {
    calendar = Calendar.getInstance();
    date = simpleDateFormat.format(calendar.getTime());
  }

  public static Date getDateFromString(String dateAsString) {
    Date convertedDate = null;
    try {
      convertedDate = simpleDateFormat.parse(dateAsString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return convertedDate;
  }

}

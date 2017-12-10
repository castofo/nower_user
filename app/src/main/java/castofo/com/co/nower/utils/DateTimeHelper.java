package castofo.com.co.nower.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alejandrosanchezaristizabal on 12/4/17.
 */

public class DateTimeHelper {

  public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private static Calendar calendar;
  private static SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat(DATE_FORMAT);

  public static Date getCurrentDate() {
    calendar = Calendar.getInstance();
    String dateAsString = simpleDateFormat.format(calendar.getTime());
    return getDateFromString(dateAsString);
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

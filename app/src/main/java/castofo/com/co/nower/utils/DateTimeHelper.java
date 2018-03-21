package castofo.com.co.nower.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

/**
 * Created by alejandrosanchezaristizabal on 12/4/17.
 */

public class DateTimeHelper {

  public final static String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ssZ";

  public static Date getCurrentDate() {
    DateTime currentDateTime = DateTime.now(DateTimeZone.getDefault());
    Date currentDate = currentDateTime.toLocalDateTime().toDate();
    return currentDate;
  }
}

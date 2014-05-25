package au.com.twobit.yosane.service.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.google.common.base.Optional;

public class InformalPeriod {
    final private static PeriodFormatter formatter;
    static {
        formatter = new PeriodFormatterBuilder()
            .appendWeeks().appendSuffix("w")
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();
    }
    
    public static DateTime subtractPeriodFromDate(String period, DateTime date) {
        date = Optional.fromNullable(date).or( DateTime.now() );
        return date.minus(createPeriodFromFormattedString(period));        
    }
    
    public static DateTime addPeriodToDate(String period, DateTime date) {
        date = Optional.fromNullable(date).or( DateTime.now() );
        return date.plus(createPeriodFromFormattedString(period));        
    }
    
    static Period createPeriodFromFormattedString(String period) {
        return formatter.parsePeriod(period);
    }
}

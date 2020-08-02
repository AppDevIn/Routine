package com.mad.p03.np2020.routine.Calender.CustomCalender;

        import com.mad.p03.np2020.routine.models.Check;

        import java.util.Date;

/**
 *
 *Interface for date change in the calender
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
public interface DateChangeListener {

    /**
     * When the data is changed
     * @param date The new data was changed to
     */
    void onDateChange(Date date);

}

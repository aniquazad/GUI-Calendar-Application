package hw4;

import java.time.LocalDate;

/**
 * This class runs the Calendar application.
 * @author Aniqua Azad
 *
 */
public class SimpleCalendar 
{
	public static void main(String[] args)
	{
		SimpleCalendarModel model = new SimpleCalendarModel(LocalDate.now());
		SimpleCalendarView view = new SimpleCalendarView(model);
		model.attach(view);
	}
}

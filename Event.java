package hw4;

import java.io.Serializable;

/**
 * This class has accessor/mutator methods for the Event attributes and checks for
 * time conflict and sort events by starting time and date.
 * 
 * This class contains code from a previous assignment (HW2: Console Calendar).
 * @author Aniqua Azad
 * @version 1.0
 *
 */
public class Event implements Comparable<Event>, Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private TimeInterval ti;
	
	/**
	 * Creates an instance of an event
	 * @param name the name of the event
	 * @param ti the time interval of the event
	 */
	public Event(String name, TimeInterval ti)
	{
		this.name = name;
		this.ti = ti;
	}
	
	/**
	 * Returns the name of the event
	 * @return the name of the event
	 */
	public String getEventName()
	{
		return this.name;
	}
	
	/**
	 * Returns the time interval of the event
	 * @return the time interval of the event
	 */
	public TimeInterval getTimeInterval()
	{
		return this.ti;
	}
	
	/**
	 * Returns the time interval in HH:mm HH:mm format
	 * @return the time interval in a certain format
	 */
	public String getTimeIntervalString()
	{
		String startTime = ti.getStartTime().toString();
		startTime = startTime.substring(startTime.indexOf("T")+1);
		String endTime = ti.getEndTime().toString();
		endTime = endTime.substring(endTime.indexOf("T")+1);
		String timeInterval = startTime + "-" + endTime;
		return timeInterval;
	}

	@Override
	/**
	 * Compares the parameter event with another to see if it occurs before or after
	 * @param e the event to be sorted
	 */
	public int compareTo(Event e) 
	{
		if(this.getTimeInterval().getStartDateTime().isAfter(e.getTimeInterval().getStartDateTime()))
		{
			return 1;
		}
		else if(this.getTimeInterval().getStartDateTime().isBefore(e.getTimeInterval().getStartDateTime()))
		{
			return -1;
		}
		return 0;
	}
}

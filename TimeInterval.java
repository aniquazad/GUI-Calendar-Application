package hw4;

import java.io.Serializable;
import java.time.*;

/**
 * This class handles time interval manipulation and checks for time conflict 
 * between events.
 * 
 * This class contains code from a previous assignment (HW2: Console Calendar).
 * @author Aniqua Azad
 * @version 1.0
 *
 */
public class TimeInterval implements Serializable
{
	private static final long serialVersionUID = 1L;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	
	/**
	 * Creates an instance of a TimeInterval with a start and end time.
	 * @param startTime the start time of the event
	 * @param endTime the end time of the event
	 */
	public TimeInterval(LocalDateTime startTime, LocalDateTime endTime)
	{
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * Gets the start date of the event
	 * @return the start date
	 */
	public LocalDate getStartDate()
	{
		return this.startTime.toLocalDate();
	}
	
	/**
	 * Gets the start time of the event
	 * @return the start time
	 */
	public LocalTime getStartTime()
	{
		return this.startTime.toLocalTime();
	}
	
	/**
	 * Gets the end date of the event
	 * @return the end date
	 */
	public LocalTime getEndTime()
	{
		return this.endTime.toLocalTime();
	}
	
	/**
	 * Gets the start date and time of the event
	 * @return the start date and time
	 */
	public LocalDateTime getStartDateTime()
	{
		return this.startTime;
	}
	
	/**
	 * Gets the end date and time of the event
	 * @return the end date and time
	 */
	public LocalDateTime getEndDateTime()
	{
		return this.endTime;
	}
	
	/**
	 * Gets the end date of the event
	 * @return the end date
	 */
	public LocalDate getEndDate()
	{
		return this.endTime.toLocalDate();
	}
	
	/**
	 * Evaluates a certain event to see if there is a time conflict 
	 * with other events.
	 * @return whether the event has a time conflict with another event
	 */
	public boolean isTimeConflict(Event possibleEvent)
	{
		LocalTime eventStart = possibleEvent.getTimeInterval().getStartTime();
		LocalTime eventEnd = possibleEvent.getTimeInterval().getEndTime();
		
		boolean isTimeConflictBool = false;
		
		//if the possible events falls into any of these categories, there is a time conflict
		if(this.getStartTime() == eventStart || this.getEndTime() == eventEnd 
				|| eventStart == this.getEndTime() || eventEnd == this.getStartTime())
		{
			isTimeConflictBool = true;
		}
		else if(this.getStartTime().isBefore(eventStart) && this.getEndTime().isAfter(eventEnd))
		{
			isTimeConflictBool = true;
		}
		else if(this.getStartTime().isAfter(eventStart) && this.getEndTime().isBefore(eventEnd))
		{
			isTimeConflictBool = true;
		}
		else if(this.getStartTime().isAfter(eventStart) && this.getStartTime().isBefore(eventEnd))
		{
			isTimeConflictBool = true;
		}
		else if(this.getEndTime().isAfter(eventStart) && this.getEndTime().isBefore(eventEnd))
		{
			isTimeConflictBool = true;
		}
		return isTimeConflictBool;
	}
}

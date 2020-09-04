package hw4;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.event.*;

/**
 * This class is the Model part of the MVC pattern. It holds 
 * accessor-mutator methods, and updates the calendar when any
 * changes are made.
 * 
 * This class contains code from a previous assignment (HW2: Console Calendar).
 * @author Aniqua Azad
 * @version 1.0
 *
 */
public class SimpleCalendarModel 
{
	private Map<LocalDate, ArrayList<Event>> dateAndEvents;
	private ArrayList<ChangeListener> listeners;
	private LocalDate currDate;
	File eventFile;
	
	/**
	 * Initializes the data structure containing the dates and their
	 * corresponding events, in addition to the listeners and file.
	 */
	public SimpleCalendarModel(LocalDate d)
	{
		currDate = d;
		dateAndEvents = new HashMap<>();
		listeners = new ArrayList<>();
		eventFile = new File("events.txt");
	}
	
	/**
	 * Adds a ChangeListener to the List of Listeners
	 * @param cl ChangeListener to add
	 */
	public void attach(ChangeListener cl)
	{
		listeners.add(cl);
	}
	
	/**
	 * Calls the stateChanged() method for every listener
	 * when a change is made to the model.
	 */
	public void updateCalendar()
	{
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener cl: listeners)
			cl.stateChanged(e);
	}
	
	/**
	 * Get events from events.txt if it exists and add to data structure
	 */
	public void readFromFile() 
	{
		if(eventFile.exists())
		{
			try 
			{
				FileInputStream fis = new FileInputStream(eventFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Event e;
				while(!(e = (Event) ois.readObject()).equals(null))
				{
					addEvent(e);
				}
				ois.close();
				fis.close();
				updateCalendar();
			} 
			catch (IOException e) 
			{
				System.out.println("Problem reading from " + eventFile.getName()+" " + e);
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println("Class cannot be found "+ e);
			}
		}
	}
	
	/**
	 * Get events from data struture and save Event objects to file
	 */
	public void writeToFile()
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream(eventFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for(LocalDate d : dateAndEvents.keySet())
			{
				for(Event e : dateAndEvents.get(d))
				{
					oos.writeObject(e);
				}
			}
			oos.close();
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println(eventFile.getName() + " cannot be found." + e);
			File eventFile = new File("events.txt");
			//e.printStackTrace();
		} 
		catch (IOException e) 
		{
			System.out.println("Problem writing to " + eventFile.getName()+" " + e);
		}
		
	}
	
	/**
	 * Sets the current date with a new date
	 * @param newDate the new date
	 */
	public void setDate(LocalDate newDate)
	{
		currDate = newDate;
		updateCalendar();
	}
	
	/**
	 * Adds an event to its specific start date
	 * @param newEvent the event to be added
	 */
	public void addEvent(Event newEvent)
	{
		LocalDate dateKey = newEvent.getTimeInterval().getStartDate();
		ArrayList<Event> events = dateAndEvents.get(dateKey);
		if(events == null)
		{
			events = new ArrayList<>();
			events.add(newEvent);
			dateAndEvents.put(newEvent.getTimeInterval().getStartDate(), events);
		}
		else
		{
			if(!events.contains(newEvent))
			{
				events.add(newEvent);
			}
		}
		updateCalendar();
	}
	
	/**
	 * Returns all the events for a certain date
	 * @param date the date to get the events from
	 * @param startTime the HH of the start time
	 * @return the event for a given date and time
	 */
	public String getEventsForDate(LocalDate date, String startTime)
	{
		String eventInfo = "";
		ArrayList<Event> eventsForDate = dateAndEvents.get(date);
		if(eventsForDate != null)
		{
			Collections.sort(eventsForDate);
			for(Event e : eventsForDate)
			{
				String ti = e.getTimeIntervalString();
				String eventName = e.getEventName();
				if(startTime.equals(ti.substring(0, 2)))
				{
					eventInfo += "                    "+eventName + "       ("+ti+")\n";
				}
			}
		}
		return eventInfo;
	}
	
	/**
	 * Checks for time conflict between 2 events
	 * @param possibleEvent the possible event to be added
	 * @return if the event has a time conflict with another event
	 */
	public boolean checkForEventConflict(Event possibleEvent)
	{
		boolean conflict = false;
		LocalDate possibleEventDate = possibleEvent.getTimeInterval().getStartDate();
		if(!dateAndEvents.containsKey(possibleEventDate))
		{
			conflict = false;
		}
		else
		{
			for(Event e : dateAndEvents.get(possibleEventDate))
			{
				if(e.getTimeInterval().isTimeConflict(possibleEvent))
					conflict = true;
				else
					conflict = false;
			}
		}
		return conflict;
	}
	
	/**
	 * Creates an event after user inputs information 
	 * @param date the date of the event
	 * @param endTime the time the event ends
	 * @param startTime the time the event starts
	 * @param eventName name of the even
	 * @return an Event
	 */
	public Event createEventToCheck(String eventName, String startTime, String endTime, String date)
	{
		String start = "";
		String end = "";
		DateTimeFormatter dtfMilitary = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
		start = getStandardTime(startTime);
		end = getStandardTime(endTime);
		
		String dateAndStartTime = date + " " + start;
		String dateAndEndTime = date + " " + end;
		LocalDateTime eventStart = LocalDateTime.parse(dateAndStartTime, dtfMilitary);
		LocalDateTime eventEnd = LocalDateTime.parse(dateAndEndTime, dtfMilitary);
		
		TimeInterval ti = new TimeInterval(eventStart, eventEnd);
		Event e = new Event(eventName, ti);
		return e;
	}
	
	/**
	 * Formats the time to HH:mm
	 * @param time the time to format
	 * @return the time in HH:mm format
	 */
	public String getStandardTime(String time)
	{
		String t = "";
		if(time.substring(5,6).equals("a"))
		{
			if(time.substring(0,2).equals("12"))
				t = "00:00";
			else
				t = time.substring(0, 5);
		}
		else
		{
			if(time.substring(0,2).equals("12"))
				t = "12:00";
			else
			{
				int hour = Integer.parseInt(time.substring(0,2));
				String hh = Integer.toString(hour + 12);
				t = hh + time.substring(2,5);
			}
		}
		return t;
	}
	
	/**
	 * Gets the current date
	 * @return the current date
	 */
	public LocalDate getCurrDate()
	{
		return currDate;
	}
	
	/**
	 * Gets the previous date
	 * @return the previous date
	 */
	public void getPrevDate()
	{
		currDate = currDate.minusDays(1);
		updateCalendar();
	}
	
	/**
	 * Gets the next date
	 * @return the next date
	 */
	public void getNextDate()
	{
		currDate = currDate.plusDays(1);
		updateCalendar();
	}
	
	/**
	 * Returns the current date in MM/dd/yyyy format
	 * @return
	 */
	public String getCurrDateString()
	{
		String date = "";
		String year = Integer.toString(currDate.getYear());
		String day = Integer.toString(currDate.getDayOfMonth());
		String month = Integer.toString(currDate.getMonthValue());
		if(currDate.getMonthValue() < 10)
			month = "0"+month;
		date = month + "/" + day + "/"+ year;
		return date;
	}
}

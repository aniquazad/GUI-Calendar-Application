package hw4;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class is the View and Controller portion of the MVC pattern. It displays the month
 * and day view of the calendar, and allows the user to manipulate the day and month
 * by creating events.
 * 
 * @author Aniqua Azad
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class SimpleCalendarView extends JFrame implements ChangeListener
{
	private SimpleCalendarModel model;
	private static final int FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int MAX_HOURS = 24;
	private JPanel monthPanel;
	private JPanel dayPanel;
	private JPanel optionPanel;
	private LocalDate today;
	private JPanel monthDayPanel;
	
	/**
	 * Creates an instance of the CalendarView
	 * @param scm an instance of the Model from the MVC pattern
	 */
	public SimpleCalendarView(SimpleCalendarModel scm)
	{
		model = scm;
		model.readFromFile();
		setTitle("Calendar");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLayout(new BorderLayout());
		today = LocalDate.now();
		
		//creates month and day panels, and panel holding the former panels and panel holding option buttons
		monthPanel = createMonthPanel();
		monthDayPanel = new JPanel();
		monthDayPanel.setLayout(new FlowLayout());
		dayPanel = createDayPanel();
		optionPanel = new JPanel();
		
		//creates buttons
		JButton quitButton = new JButton("Quit");
		quitButton.setBackground(Color.RED);
		JButton nextButton = new JButton(">");
		nextButton.setBackground(Color.LIGHT_GRAY);
		JButton prevButton = new JButton("<");
		prevButton.setBackground(Color.LIGHT_GRAY);
		JButton createButton = new JButton("Create");
		createButton.setBackground(new Color(71, 162, 117));
		
		//adds option buttons to optionPanel
		optionPanel.setLayout(new FlowLayout());
		optionPanel.add(quitButton);
		optionPanel.add(Box.createRigidArea(new Dimension(400,0)));
		optionPanel.add(prevButton);
		optionPanel.add(nextButton);
		optionPanel.add(Box.createRigidArea(new Dimension(100,0)));
		optionPanel.add(createButton);
		
		quitButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.writeToFile();
				setVisible(false);
				dispose();
			}
		});
		
		prevButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.getPrevDate();
			}
		});
		
		nextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				model.getNextDate();
			}
		});
		
		/*
		 * Creates a JOptionPane which allows user to inout event info.
		 * After creating the event, check for time conflict.
		 * 		-if no conflict, add event to calendar and in-memory data structure
		 *      -if conflict, error message
		 */
		ActionListener createButtonAL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Object[] saveOption = {"Save"};
				JPanel createEventPanel = new JPanel(new BorderLayout());
				
				//get event name
				JPanel top = new JPanel(new GridLayout(0,1));
				JLabel eventNameLabel = new JLabel("Event name:");
				top.add(eventNameLabel);
				JTextField eventNameTextField = new JTextField("Untitled event");
				top.add(eventNameTextField);
				
				//show event date
				JPanel bottom = new JPanel(new FlowLayout());
				String dateOfEvent = model.getCurrDateString();
				JTextArea eventDateTextArea = new JTextArea(dateOfEvent);
				eventDateTextArea.setEditable(false);
				bottom.add(eventDateTextArea);
				bottom.add(Box.createRigidArea(new Dimension(50,0)));
				
				//get start time
				JTextField startTimeTextField = new JTextField("Start time");
				bottom.add(startTimeTextField);
				JLabel filler = new JLabel("to");
				bottom.add(filler);
				JTextField endTimeTextField = new JTextField("End time");
				bottom.add(endTimeTextField);
				
				//get end time
				createEventPanel.add(top, BorderLayout.NORTH);
				createEventPanel.add(bottom, BorderLayout.SOUTH);
				int clicked = JOptionPane.showOptionDialog(null, createEventPanel,"Create an event", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, saveOption, null);
				
				//if 'Save' is pressed, create event
				if(clicked == JOptionPane.OK_OPTION)
				{
					String eventName = eventNameTextField.getText();
					String startTime = startTimeTextField.getText();
					String endTime = endTimeTextField.getText();
					
					Event eventToCheck = model.createEventToCheck(eventName, startTime, endTime, model.getCurrDateString());
					boolean conflict = model.checkForEventConflict(eventToCheck);
					if(conflict == false)
					{
						model.addEvent(eventToCheck);
					}
					else
					{
						String error = "TIME CONFLICT! Please enter an event without a time conflict.";
						JOptionPane.showMessageDialog(null, error, "Time Conflict", JOptionPane.ERROR_MESSAGE);
					}
				}
			}

		};
		createButton.addActionListener(createButtonAL);
		
		add(optionPanel, BorderLayout.NORTH);
		monthDayPanel.add(monthPanel);
		monthDayPanel.add(dayPanel);
		add(monthDayPanel, BorderLayout.CENTER);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(size.width/2 - getSize().width/2, size.height/2 - getSize().height/2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	/**
	 * Repaints the panel holding the month and day panels
	 * @param e ChangeEvent object
	 */
	@Override
	public void stateChanged(ChangeEvent e) 
	{
		//removes all components from monthDayPanel
		monthDayPanel.removeAll();
		
		//creates a new month and day panel and adds to monthDayPanel
		monthPanel = createMonthPanel();
		monthDayPanel.add(monthPanel);
		dayPanel = createDayPanel();
		monthDayPanel.add(dayPanel);
		//add new panels to monthDayPanel
		add(monthDayPanel, BorderLayout.CENTER);
		//re-layouts and repaints the frame
		validate();
		repaint();
	}
	
	/**
	 * Creates the JPanel holding the day view (with events)
	 * @return the JPanel holding the day view
	 */
	public JPanel createDayPanel() 
	{
		//create the JPanel which holds the day view
		dayPanel = new JPanel();
		dayPanel.setLayout(new BorderLayout());
		
		//get current date to print day label
		LocalDate today = model.getCurrDate();
		String day = today.getDayOfWeek().toString();
		int monthNum = today.getMonthValue();
		int dayOfMonth = today.getDayOfMonth();
		String dayLabelString = day + " " + monthNum+"/"+dayOfMonth;
		JLabel dayLabel = new JLabel(dayLabelString);
		dayLabel.setHorizontalAlignment(JLabel.CENTER);
		dayLabel.setFont(new Font("TimesRoman", Font.ITALIC+Font.BOLD, 12));
		
		JTextArea dayTextPane = new JTextArea();
		dayTextPane.setEditable(false);
		dayTextPane.setBackground(new Color(250, 244, 202));
		
		String t = "";
		String eventInfo = "";
		for(int i = 0; i < MAX_HOURS; i++)
		{
			if(i <= 9)
				t = "0"+i;
			else
				t = ""+i;
			eventInfo = model.getEventsForDate(today, t);
			dayTextPane.append(t+":00" + "     ");
			dayTextPane.append(eventInfo+"\n");
		}
		
		JScrollPane dayScrollPane = new JScrollPane(dayTextPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		dayPanel.setPreferredSize(new Dimension(5*FRAME_WIDTH/12, FRAME_HEIGHT-200));
		dayPanel.add(dayLabel, BorderLayout.NORTH);
		dayPanel.add(dayScrollPane, BorderLayout.CENTER);
		
		return dayPanel;
	}
	
	/**
	 * Creates the JPanel holding the month view (calendar and headers)
	 * @return JPanel holding month view
	 */
	public JPanel createMonthPanel() 
	{
		LocalDate c = model.getCurrDate();
		
		monthPanel = new JPanel();
		monthPanel.setLayout(new BorderLayout());
		monthPanel.setPreferredSize(new Dimension(5*FRAME_WIDTH/12, FRAME_HEIGHT-200));
		JPanel monthDetailPanel = new JPanel(new BorderLayout()); //holds the month and days of week
		final int buttonSize = 20;
		Font monthDetailFont = new Font("TimesRoman", Font.BOLD, 18);
		GridLayout gl = new GridLayout();
		gl.setColumns(7);
		gl.setRows(0);
		
		String[] days = {"S", "M", "Tu", "W", "Th", "F", "Sa"};
		JPanel daysOfWeek = new JPanel(gl); //JPanel which will hold days of the week
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM  yyyy");
		String formattedMonth = formatter.format(c);
		JLabel monthLabel = new JLabel(formattedMonth);
		monthLabel.setFont(monthDetailFont);
		monthLabel.setHorizontalAlignment(JLabel.CENTER);
		monthDetailPanel.add(monthLabel, BorderLayout.NORTH);
		
		// To figure out the day of week of the 1st day of the given month
        LocalDate x = LocalDate.of(c.getYear(), c.getMonth(), 1); 
        int todayDayOfWeekInt = x.getDayOfWeek().getValue();
        int lengthOfCurrentMonth = c.lengthOfMonth();
        
        //adds in the days of the week to the calendar
        for(int i = 0; i < days.length; i++)
        {
        	JLabel day = new JLabel(days[i]);
        	day.setHorizontalAlignment(JLabel.CENTER);
        	day.setFont(monthDetailFont);
        	daysOfWeek.add(day);
        }
        
		monthDetailPanel.add(daysOfWeek, BorderLayout.SOUTH);
		monthPanel.add(monthDetailPanel, BorderLayout.NORTH);

		JPanel calPanel = new JPanel(); //holds calendar
		calPanel.setLayout(gl);
		
		if(todayDayOfWeekInt != 7)
		{
	        //puts in blank buttons until the 1st day of week is reached
	        for(int i = 0; i < todayDayOfWeekInt; i++)
	        {
	        	JButton fillerButton = new JButton();
	        	fillerButton.setPreferredSize(new Dimension(buttonSize,buttonSize));
	        	fillerButton.setEnabled(false);
	        	calPanel.add(fillerButton);
	        }
		}

        //creates day buttons which user can press
        for(int i = 1; i <= lengthOfCurrentMonth; i++)
        {
        	JButton dayButton = new JButton(Integer.toString(i));
        	//draws border around current selected date
        	if(i == c.getDayOfMonth())
        	{
        		dayButton.setBorder(new LineBorder(new Color(153, 0, 0)));
        	}
        	else
        	{
        		dayButton.setBackground(Color.WHITE);
        	}
        	//if it's today's date, make the button a different color
        	if(today.getMonth() == c.getMonth() && today.getDayOfMonth() == i)
    		{
    			dayButton.setBackground(new Color(239, 210, 24));
    		}
        	dayButton.setPreferredSize(new Dimension(buttonSize,buttonSize));
        	//action listener for each button
        	dayButton.addActionListener(new ActionListener()
        	{
        		@Override
        		public void actionPerformed(ActionEvent arg0) 
        		{
        			int dayNum = Integer.parseInt(dayButton.getText());
        			LocalDate newDate = LocalDate.of(c.getYear(), c.getMonth(), dayNum);
        			model.setDate(newDate);
        		}
        	});
        	calPanel.add(dayButton);
        }
		monthPanel.add(calPanel, BorderLayout.CENTER);
		return monthPanel;
	}
}

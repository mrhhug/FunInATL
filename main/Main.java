package com.webreadllc.FunInATL.main;

import com.google.gson.Gson;
import com.webreadllc.FunInATL.event.Event;
import com.webreadllc.FunInATL.httpscrape.DadsGarageScrape;
import com.webreadllc.FunInATL.httpscrape.DragonConScrape;
import com.webreadllc.FunInATL.httpscrape.PiedmontParkScrape;
import com.webreadllc.FunInATL.httpscrape.TicketmasterScrape;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;

/**
 *
 * @author michael
 */
public class Main
{

    /**
     * Passing anything to this jar will be ignored as i don't need your help
     * @param args
     */
    public static void main(String[] args)
    {
	try
	{
	    exceptionThrower();
	}
	catch (Exception ex)
	{
	    //I want this to email me
	    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * This will throw all* exceptions so the main can catch and email me
     * @throws IOException
     */
    public static void exceptionThrower() throws IOException
    {
	Set<Event> allEvents = new HashSet<>();
	allEvents.addAll(new TicketmasterScrape().scrape());
	allEvents.addAll(new DragonConScrape().scrape());
	allEvents.addAll(new PiedmontParkScrape().scrape());
	allEvents.addAll(new DadsGarageScrape().scrape());
        
	MusicMidtownChanger(allEvents);
	Gson gson = new Gson();
	
	Jedis jedis = new Jedis("localhost");
	LocalDate dateIterator = LocalDate.now();
	for(int i =0; i<45 ; i++)
	{
	    Set<Event> theDaysEvents = new HashSet<>();
	    for(Event j: allEvents)
	    {
		if(j.getLocalDate().isEqual(dateIterator))
		    theDaysEvents.add(j);
	    }
	    
	    if(!theDaysEvents.isEmpty())
	    {
		String value = gson.toJson(theDaysEvents);
		String key = "events."+dateIterator.format(DateTimeFormatter.ISO_DATE).replaceAll("-", ".");
		jedis.set(key, value);
		//System.out.println("SET events."+key+" "+value);
		//String get = jedis.get(key);
	    }
	    dateIterator = dateIterator.plusDays(1);
	}
    }
    public static void MusicMidtownChanger(Set<Event> allEvents)
    {
	for( Event i : allEvents)
	{
	    if(i.getTitle().contains("Music Midtown"))
	    {
		i.setPrice("$135");
		i.setWebsite("http://www.musicmidtown.com/");
	    }
	}
    }
}

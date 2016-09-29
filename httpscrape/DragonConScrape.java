package com.webreadllc.FunInATL.httpscrape;

import com.webreadllc.FunInATL.event.Event;
import com.webreadllc.FunInATL.event.Image;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

/**
 * @author michael
 */
public class DragonConScrape  extends AbstactScrape
{
    //Yeah this is a one off, their website is soooooo 1990s
    @Override
    public Set<Event> scrape() throws IOException
    {
	Set<Event> ret = new HashSet<>();
	LocalDate date = LocalDate.now();
	LocalDate endof2016Fesivities = LocalDate.of(2016, Month.SEPTEMBER, 5);
	if(date.isBefore(endof2016Fesivities))
	{
	    getAllDates(ret);
	}
	return ret;
    }
    private void getAllDates(Set<Event> par)
    {
	String title = "Dragon Con 2016";
	String googlePlacesID = "ChIJ6YwB3kE9YgRladXs-V0lGc";
	String catagory = "Arts & Theater";
	String website = "http://www.dragoncon.org";
	String price = "$150.00 - $2,565.00";
	//Just change Date
	LocalTime localTime = LocalTime.NOON;
	
	Set<Image> images = new HashSet<>();
	images.add(new Image("300", "300", "1_1", "http://store.dragoncon.org/images/2016-4day-Memberships.jpg"));
	String shortDescription = "Dragon Con is a North America multigenre convention, founded in 1987, which takes place once each year in Atlanta, Georgia";
	String longDescription = "As of 2014, the convention draws attendance of over 70,000, features hundreds of guests, encompasses five hotels in the Peachtree Center neighborhood of downtown Atlanta near Centennial Olympic Park, and runs thousands of hours of programming for fans of science fiction, fantasy, comic books, and other elements of fan culture. It is operated by a private for-profit corporation, with the help of a 1,500-member volunteer staff. Dragon Con has hosted the 1990 Origins Game Fair and the 1995 North American Science Fiction Convention (NASFiC).";
	String status = "active";
	String eventHash = "Dragon Con 2016";
	for(int i = 2; i<=5; i++)
	{
	    Event myEvent = new Event(title,googlePlacesID,catagory, website,price, LocalDate.of(2016, Month.SEPTEMBER, i),localTime);
	    myEvent.setImages(images);
	    myEvent.setShort_description(shortDescription);
	    myEvent.setLong_description(longDescription);
	    myEvent.setStatus(status);
	    myEvent.setEventHash(eventHash);
	    par.add(myEvent);
	}
	
    }
}

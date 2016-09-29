package com.webreadllc.FunInATL.httpscrape;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webreadllc.FunInATL.event.Event;
import com.webreadllc.FunInATL.event.Image;
import com.webreadllc.FunInATL.httpscrape.ticketMasterGson.TicketMasterEvents;
import com.webreadllc.FunInATL.httpscrape.ticketMasterGson.TicketMasterImage;
import com.webreadllc.FunInATL.httpscrape.ticketMasterGson.TicketMasterPriceRanges;
import com.webreadllc.FunInATL.httpscrape.ticketMasterGson.TicketMasterQuery;
import com.webreadllc.FunInATL.httpscrape.ticketMasterGson.TicketMasterVenue;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author michael
 */
public class TicketmasterScrape extends AbstactScrape
{
    private final String APIKEY = "0NYDmo8198CxTEvAAjMWIA5Bms0EGchB";
    
    String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey="+APIKEY+"&latlong=33.87497640410964,-84.4148254594&radius=20&unit=miles&startDateTime=2016-08-29T05:00:00Z&endDateTime=2016-09-19T05:00:00Z&size=500";
    
    @Override
    public Set<Event> scrape() throws IOException
    {
	LocalDateTime currentTime = LocalDateTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	String currentStamp = currentTime.format(formatter);
	String plus45Days = currentTime.plusDays(30).format(formatter);
	String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey="+APIKEY+"&latlong=33.87497640410964,-84.4148254594&radius=20&unit=miles&startDateTime="+currentStamp+"T05:00:00Z&endDateTime="+plus45Days+"T05:00:00Z&size=500";
	Set<Event> ret = new HashSet<>();
	String urLasString = getURLasString(url);
	
	Gson gson = new GsonBuilder().create();
	TicketMasterQuery fromJson = gson.fromJson(urLasString, TicketMasterQuery.class);
	
	for (TicketMasterEvents i : fromJson.getEmbedded().getEvents())
	{
	    //I wrote this horrible code so i can fix it later, Aisle 5 needs their own special parser
	    //They don't have prices or catagories through TM
	    
	    //lakewood does some weird stuff
	    if(!i.getEmbedded().getVenues().get(0).getName().equals("Aisle 5") && !i.getName().contains("VIP"))
	    {
		String title = i.getName();	
		TicketMasterVenue venue = i.getEmbedded().getVenues().get(0);
		String googlePlaceID = getGooglePlace(venue.getName()+venue.getAddress().getLine1());
		String catagory = i.getClassifications().get(0).getSegment().getName();
		String website = i.getUrl();
		String priceRanges = getPriceRanges(i.getPriceRanges());
		LocalDate localDate = LocalDate.parse(i.getDates().getStart().getLocalDate());
		LocalTime localTime;
		if( null != i.getDates().getStart().getLocalTime())
		    localTime = LocalTime.parse(i.getDates().getStart().getLocalTime());
		else
		    localTime = LocalTime.of(0, 1);
		
		//we have an event!
		Event myEvent = new Event(title, googlePlaceID, catagory, website, priceRanges, localDate, localTime);

		//bonus stuff!	    
		myEvent.setSubCategory(i.getClassifications().get(0).getGenre().getName());

		//this could use some refactoring & clearer names
		myEvent.setImages(convertImages(i.getImages()));
		myEvent.setShort_description(i.getDescription());
		myEvent.setEventHash(i.getId());
		myEvent.setStatus(i.getDates().getStatus().getCode());
		ret.add(myEvent);
	    }
	} 
	return ret;
    }
    private Set<Image> convertImages(List<TicketMasterImage> par)
    {
	HashSet<Image> ret = new HashSet<>();
	for(TicketMasterImage i : par)
	{
	    Image myImage = new Image();
	    myImage.setHeight(i.getHeight());
	    myImage.setRatio(i.getRatio());
	    myImage.setURL(i.getUrl());
	    myImage.setWidth(i.getWidth());
	    ret.add(myImage);
	}
	return ret;	
    }
    private String getPriceRanges(List<TicketMasterPriceRanges> par)
    {
	String ret = "?";
	BigDecimal min = getMinPrice(par);
	BigDecimal max = getMaxPrice(par);	
	DecimalFormat df = new DecimalFormat("0.00");
		
	//yep, this assumes if we have a min we will have a max
	if(0 != min.compareTo(new BigDecimal(Integer.MAX_VALUE)))
	    //probobly somewhat safe to hardcode USD for FunInAtl
	    ret = "$"+df.format(min)+" - $"+df.format(max);
	if(0 == min.compareTo(max))
	    ret = "$"+df.format(min);
	return ret;
    }
    private BigDecimal getMinPrice(List<TicketMasterPriceRanges> par)
    {
	BigDecimal ret = new BigDecimal(Integer.MAX_VALUE);
	if(null != par)
	{
	    for(TicketMasterPriceRanges i : par)
	    {
		ret = i.getMin().min(ret);
	    }
	}
	return ret;
    }
    private BigDecimal getMaxPrice(List<TicketMasterPriceRanges> par)
    {
	BigDecimal ret = new BigDecimal(Integer.MIN_VALUE);
	if(null != par)
	{
	    for(TicketMasterPriceRanges i : par)
	    {
		ret = i.getMax().max(ret);
	    }
	}
	return ret;
    }
    
}
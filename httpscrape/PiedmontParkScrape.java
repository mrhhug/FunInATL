package com.webreadllc.FunInATL.httpscrape;

import com.webreadllc.FunInATL.event.Event;
import com.webreadllc.FunInATL.event.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author michael
 */
public class PiedmontParkScrape extends AbstactScrape
{
    Map<String,Integer> monthMap;
    @Override
    public Set<Event> scrape() throws IOException
    {
	monthMap = new HashMap<>();
	monthMap.put("January", 1);
	monthMap.put("February",2);
	monthMap.put("March",3);
	monthMap.put("April",4);
	monthMap.put("May",5);
	monthMap.put("June",6);
	monthMap.put("July",7);
	monthMap.put("August",8);
	monthMap.put("September",9);
	monthMap.put("October",10);
	monthMap.put("November",11);
	monthMap.put("December",12);
	    
	Set<Event> ret = new HashSet<>();
	
	String htmlString = getURLasString("http://www.piedmontpark.org/do/calendar.html");
	Document doc = Jsoup.parse(htmlString);
	
	Elements elementsByClass = doc.getElementsByClass("headline2");
	//The featured event
	elementsByClass.remove(0);
	//social media page
	elementsByClass.remove(elementsByClass.size()-1);
	for( Element i :elementsByClass)
	{
	    
	    String month = i.text().replace(String.valueOf((char) 160), " ").trim();
	    Elements peers = i.parent().parent().children();
	    peers.remove(0);
	    peers.remove(0);
	    for( Element j : peers)
	    {
		Elements children = j.children();
		
		String date = children.get(0).text();
		String ThisShouldbeTD = children.get(1).html();
		ret.addAll(parseEventsTheWrongWay(ThisShouldbeTD.split("<br>"), month, date));
	    }
	    
	    
	}
	return ret;
    }
    private Set<Event> parseEventsTheWrongWay(String[] par, String month, String datesss)
    {
	
	Set<Event> ret = new HashSet<>();
	if(!datesss.trim().isEmpty())
	{
	    ArrayList<List<String>> divideEvents = divideEvents(removeTrailingEmptyLines(makeAL(par)));
	    for(List<String> i :divideEvents)
	    {
		if(3 < i.size())
		{
		    String title = getTitle(i.get(0));
		    String website = getWebsite(i.get(0));
		    String image = getImage(i.get(0));
		    String googlePlacesID = "ChIJ8RaDszgE9YgRZa41ajfDTFI";
		    String category = "Arts & Theater";
		    String price = getPrice(i.get(1),i.get(2));
		    LocalTime start = getStart(i.get(1));
		    
		    int year = Calendar.getInstance().get(Calendar.YEAR);
		    Integer intMonth = monthMap.get(month);
		    int day = getDay(datesss);
		    LocalDate date = LocalDate.of(year, intMonth, day);
		    
		    Event myEvent = new Event(title, googlePlacesID, category, website, price, date, start);
		    myEvent.setShort_description(i.get(i.size()-1));
		    myEvent.setLong_description(i.get(i.size()-1));
		    Set<Image> myImages = new HashSet<>();
		    myImages.add(new Image("","","",image));
		    myEvent.setImages(myImages);
		    myEvent.setStatus("active");
		    ret.add(myEvent);
		}
	    }
	}
	return ret;
    }
    private int getDay(String par)
    {
	int ret = 0;
	Pattern p = Pattern.compile("(^.*)( \\d+$)");
	Matcher m = p.matcher(par);
	while (m.find())	    
	    ret = Integer.parseInt(m.group(2).trim());
	return ret;
    }
    private LocalTime getStart(String par)
    {
	par = par.replaceAll("[rR]egistration", "");
	LocalTime ret;
	String match = "";
	Pattern p = Pattern.compile("(^ *)(\\d+:\\d\\d[pa]m|Noon)(.*$)");
	Matcher m = p.matcher(par);
	while (m.find())	    
	    match = m.group(2).toLowerCase();
	//i realize how dngerous this code is right here
	if(match.equals("noon"))
	    ret = LocalTime.NOON;
	else
	{
	    int subract = 0;
	    String[] split = match.split(":");
	    int index = match.length()-2;
	    char charAt = match.charAt(index);
	    if(charAt == 'p')
		subract = 2;
	    //int parseInt = Integer.parseInt(split[1].substring(0,2));
	    ret = LocalTime.of(Integer.parseInt(split[0])-subract, Integer.parseInt(split[1].substring(0,2)));
	}
	return ret;
    }
    private String getPrice(String par0, String par1)
    {
	String ret = "FREE";
	if(par0.contains("price varies"))
	    ret = "price varies";
	if(par1.contains("$"))
	    ret = par1.substring(par1.indexOf("$"));
	return ret;
    }
    private String getTitle(String par)
    {
	String ret = "UNKNOWN Evnet";
	Pattern p = Pattern.compile("(<strong>)(.*?)(</strong>)");
	Matcher m = p.matcher(par);
	while (m.find())	    
	    ret = m.group(2);
	return ret;
    }
    private String getWebsite(String par)
    {
	String ret = "http://www.piedmontpark.org/";
	Pattern p = Pattern.compile("(href=\"../)(.*?)(\")");
	Matcher m = p.matcher(par);
	while (m.find())	    
	    ret = "http://www.piedmontpark.org/"+m.group(2);
	return ret;
    }
    private String getImage(String par)
    {
	String ret = "http://www.piedmontpark.org/images/ppark_logo.gif";
	Pattern p = Pattern.compile("(<img src=\"../images/)(.*?)(\")");
	Matcher m = p.matcher(par);
	while (m.find())
	    ret = "http://www.piedmontpark.org/images/"+m.group(2);
	return ret;
    }
    private ArrayList<String> makeAL(String[] par)
    {
	ArrayList<String> ret = new ArrayList<>();
	ret.addAll(Arrays.asList(par));
	return ret;
    }
    private ArrayList<String> removeTrailingEmptyLines(ArrayList<String> par)
    {
	while(par.get(par.size()-1).trim().isEmpty())
	    par.remove(par.size()-1);
	return par;
    }

    private ArrayList<List<String>> divideEvents(ArrayList<String> par)
    {
	ArrayList<List<String>> ret = new ArrayList<>();
	ArrayList<Integer> indicies = new ArrayList<>();
	for(int i =0 ;i <par.size(); i++)
	{
	    if(par.get(i).trim().isEmpty())
		indicies.add(i);
	}
	indicies.add(par.size());
	int start = 0;
	int end = 0;
	for(Integer i : indicies)
	{
	    start = end;
	    end = i;
	    List<String> subList = par.subList(start,end);
	    ret.add(subList);
	    end++;
	}
	return ret;
    }
//    private int numberOfEventsInDay(String par)
//    {
//	Pattern p = Pattern.compile("(<strong>)(.*)(</strong><br>)");
//	Matcher m = p.matcher(par);
//	int count = 0;
//	while (m.find())
//	{
//	    
//	    String title = m.group(2);
//	    count +=1;
//	}
//	
//	return count;
//    }
    
    private String readFile() throws IOException 
    {
	byte[] encoded = Files.readAllBytes(Paths.get("/tmp/calendar.html"));
	return new String(encoded);
    }

}

package com.webreadllc.FunInATL.httpscrape;

import com.webreadllc.FunInATL.event.Event;
import com.webreadllc.FunInATL.event.Image;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * @author michael
 */
public class DadsGarageScrape extends AbstactScrape
{
    private final Map<String,String> classifications;
    private final String googlePlacesID;
    
    //we need to talk about how this works; here is the pattern i have deciphered, they are represented
    //as unique ids on dads garage calender table
    
    ///<-----------------constant------------> YEAR_m_dd_isThisDateInRange
    ///pagestructure_0_content_1_EventCalendar_2016_7_31_1
    //pagestructure_0_content_1_EventCalendar_2016_8_1_0
    //pagestructure_0_content_1_EventCalendar_2016_9_1_1
    ////I opened the page in default and this month is august
    ///the requested date range can be altered through javascript
    
    public DadsGarageScrape()
    {
	super();
	this.classifications = new HashMap<>();
	this.classifications.put("purple", "Improv");
	this.classifications.put("yellow", "Special Events");
	this.classifications.put("blue", "Kids Shows");
	this.classifications.put("orange", "Scripted");
	this.classifications.put("green", "Classes");
        this.googlePlacesID = "ChIJ255RQwEE9YgRSOmoPEwQbKc";
    }
    
    @Override
    public Set<Event> scrape() throws IOException
    {
        Set<Event> ret = new HashSet<>();
        ret.addAll(getEventsForCurrentMonth());
	return ret;
    }
    
    private Set<Event> getEventsForCurrentMonth() throws IOException
    {
	Document doc = Jsoup.parse(getCalenderPage());
	Set<Event> ret = new HashSet<>();

	// Get the number of days in that month
	YearMonth yearMonthObject = YearMonth.of(nowYear, nowMonth);
	int daysInMonth = yearMonthObject.lengthOfMonth();
	
	for(int i = nowDayofMonth; i <=daysInMonth;i++)
	{
	    Element content = doc.getElementById("pagestructure_0_content_1_EventCalendar_"+nowYear+"_"+nowMonth+"_"+i+"_0");
	    ret.addAll(getEventsForDay(nowMonth, content,i ));
	}
	
	return ret;
    }
    private void getEventsForNextMonth()
    {
	//TODO
    }
    private Set<Event> getEventsForDay(int month, Element content, int myDayOfMonth) throws IOException
    {
	int day = Integer.parseInt(content.getElementById("calendar_number_txt").text());
	Set<Event> ret = new HashSet<>();
	if(1 < content.childNodeSize())
	{
	    Elements activities = content.getElementsByClass("activities");
	    Element get = activities.get(0).child(0);
	    List<Node> childNodes = get.childNodes();
	    for(int i =0; i < get.childNodeSize();i+=4)
	    {
		TextNode timeTN = (TextNode) childNodes.get(i);
		String EventTime = timeTN.text();
		
		Element span = (Element) childNodes.get(i+2);
		String classificationOfEvent = classifications.get(span.className());
		String eventLink = span.getElementsByTag("a").attr("href");
		String title = span.getElementsByTag("a").text();
		
//		Event myEvent = new Event(month,day);
//		myEvent.setTime(EventTime);
//		myEvent.setVenue("Dad's Garage");
//		myEvent.setCatagory("Comedy");
//		myEvent.setSubCatagory(classificationOfEvent);
//		myEvent.setName(name);
//		myEvent.setPrice("$");
//		parseEventPage(myEvent,eventLink);
                
                //Event myEvent = new Event(name, googlePlacesID, "Comedy", "http://www.dadsgarage.com/")
		ret.add(parseEventPage(eventLink, title, EventTime, classificationOfEvent,myDayOfMonth));
	    }
	    
	    
	}	
	return ret;
    }
    private Event parseEventPage(String eventLink, String title, String time, String subClassification, int myDayOfMonth) throws IOException
    {
	String eventPage = getEventPage(eventLink);
	Document EventDoc = Jsoup.parse(eventPage);
	Elements el = EventDoc.getElementById("pagestructure_0_content_1_eventImage").getElementsByAttribute("src");
        
        String price = "$$";
	String imageURL = "http://www.dadsgarage.com/"+el.get(0).attr("src");
	String website = eventPage;
	Element attributes = EventDoc.getElementById("pagestructure_0_content_1_eventBuyTicketsLink");
	if(null == attributes)
	    price = "FREE";
	else
	    website = EventDoc.getElementById("pagestructure_0_content_1_eventBuyTicketsLink").attr("href");
	
	Element elementById = EventDoc.getElementById("pagestructure_0_content_1_eventDescription");
	List<Node> childNodes = elementById.childNodes();
	List<Node> descri = childNodes.get(0).childNodes();
	
	String recursivlyGetdescription = getDescriptionRecursivly(descri);
	String[] split = recursivlyGetdescription.split("\\.", 2);
	
	String shortDescription = split[0]+".";
        String longDescription;
	if(2 == split.length)
	    longDescription = split[1];
	else
	    longDescription = "";
	//this is hacky as shit
	String[] splt = time.split(":");
	LocalTime of = LocalTime.of(Integer.parseInt(splt[0]), Integer.parseInt(splt[1].substring(0, splt[1].indexOf(" "))));
        Event myEvent = new Event(title, googlePlacesID, "Comedy", website, price, LocalDate.of(nowYear, nowMonth, myDayOfMonth), of.plusHours(12));
	myEvent.setShort_description(shortDescription);
	myEvent.setLong_description(longDescription);
	myEvent.setStatus("active");
	
	HashSet<Image> imhs = new HashSet<>();
	imhs.add(new Image("","","",imageURL));
	myEvent.setImages(imhs);
	myEvent.generateHash();
        return myEvent;
    }
    
    
    private String getCalenderPage() throws IOException 
    {
	//becuase we want to set a useragent
	//massage the return String
	//and allow for compresion
	
	String ret = getURLasString("http://www.dadsgarage.com/calendar");
//	Document doc = Jsoup.connect("http://http://www.dadsgarage.com/calendar/").get();
//	String path = "/home/michael/Desktop/FunInAtl/DadsGarageCalendar.html";
//	byte[] encoded = Files.readAllBytes(Paths.get(path));
//	String ret = new String(encoded, StandardCharsets.UTF_8);
	return ret;
    }
    private String getEventPage(String eventLink) throws IOException
    {
//	String path = "/home/michael/Desktop/FunInAtl/PuppyProv.aspx";
//	byte[] encoded = Files.readAllBytes(Paths.get(path));
//	String ret = new String(encoded, StandardCharsets.UTF_8);
//	return ret.replaceAll("src=\"\"", "");
	return getURLasString("http://www.dadsgarage.com"+eventLink.replaceAll(" ", "%20"));
    }
    
    

    private String getDescriptionRecursivly(List<Node> descri)
    {
	String ret = "";
	for( Node i : descri)
	{
	    if(i instanceof TextNode)
	    {
		TextNode tempNode = (TextNode) i;
		ret += tempNode.text();
	    }
	    if(0 < i.childNodeSize())
	    {
		ret += getDescriptionRecursivly(i.childNodes());
	    }
	}
	return ret;
    }
}

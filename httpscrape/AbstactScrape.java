package com.webreadllc.FunInATL.httpscrape;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;

/**
 * @author michael
 */
abstract class AbstactScrape implements Scrape
{
    final int nowYear;
    final int nowMonth;
    final int nowDayofMonth;
    
    final int nowNumberOfDaysInMonth;
    
    final int nextMonthYear;
    final int nextMonth;
    final int nextNumberOfDaysInMonth;

    AbstactScrape()
    {
	LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
	nowMonth = now.getMonthValue();
	nowDayofMonth = now.getDayOfMonth();
	nowYear = now.getYear();
	
	nowNumberOfDaysInMonth = now.getMonth().length(now.toLocalDate().isLeapYear());
	
	LocalDateTime next = now.plusMonths(nowMonth);
	nextMonthYear = next.getYear();
	nextMonth = next.getMonthValue();
	nextNumberOfDaysInMonth = next.getMonth().length(next.toLocalDate().isLeapYear());
	
    }
    
    //i realize this waits for the entire string to come over the wire before starting to parse the json ... for FunInAtl, we have API limits anywyay
    String getURLasString(String urlStr) throws MalformedURLException, IOException
    {	
	URL url = new URL(urlStr);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Cast shouldn't fail
	HttpURLConnection.setFollowRedirects(true);
	//because we are not ashamed!
	conn.setRequestProperty ("User-agent", "FunInATL.com");
	//allow both GZip and Deflate (ZLib) encodings
	conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	String encoding = conn.getContentEncoding();

	// create the appropriate stream wrapper based on
	// the encoding type
	InputStream is = null; 
	if (encoding != null && encoding.equalsIgnoreCase("gzip")){
	    is = new GZIPInputStream(conn.getInputStream());
	}
	else if (encoding != null && encoding.equalsIgnoreCase("deflate")){
	    is = new InflaterInputStream(conn.getInputStream(), new Inflater(true));
	}
	else{
	    is = conn.getInputStream();
	}
	Scanner s = new Scanner(is).useDelimiter("\\A");
	String ret = s.hasNext() ? s.next() : "";
	//for some reason multiple attributes in a tag makes jsoup and unfriendly manwhore
	return ret.replaceAll("src=\"\"", "");
    }
    
    String getGooglePlace(String par) throws IOException
    {
	if(InetAddress.getLocalHost().toString().equals("michael/127.0.0.1"))
	    return "PlacesScrapeFromDev";
	Places.Response<List<Place>> query = Places.textSearch(Params.create().query(par));
	List<Place> result = query.getResult();
	if(result.isEmpty())
	    return "Got zero results, are you over the limit?";
	Place place = result.get(0);
	Place.Id placeId = place.getPlaceId();
	return placeId.getId();
    }
    
}

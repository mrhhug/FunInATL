package com.webreadllc.funinatl.event;

import com.google.common.base.Objects;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author michael
 */
public class Event
{

    private final String title;
    private final String googlePlacesID; //google places ID
    private final String category;  ///garunteed
    private String website; //URL garunteed
    private String price; // ugh
    
    private final LocalDate localDate; // these two i made final for ME!
    private final LocalTime localtime; // these two i made final for ME!
    
    private String subCategory;
    private Set<Image> images;
    private String short_description;
    private String long_description;
    private String status;
    private boolean featured;
    private String eventHash; ///defines an event

    //these are in the constructor because i have garunteed them to Charlie
    public Event(String title, String googlePlacesID, String category, String website, String price, LocalDate localDate, LocalTime localtime)
    {
	this.title = title;
	this.googlePlacesID = googlePlacesID;
	this.category = category;
	this.price = price;
	this.website = website;
	this.localDate = localDate;
	this.localtime = localtime;
	
	//empty string initializers
	this.subCategory = "";
	this.images = new HashSet<>();
	this.short_description = "";
	this.long_description = "";
	this.status = "";
	this.featured = false;
	this.eventHash = "";
    }
    
    public void generateHash()
    {
	this.eventHash = String.valueOf(Objects.hashCode(title));
    }
    
    public void setSubCategory(String subCategory)
    {
	this.subCategory = subCategory;
    }

    public void setImages(Set<Image> images)
    {
	this.images = images;
    }

    public void setShort_description(String short_description)
    {
	this.short_description = short_description;
    }

    public void setLong_description(String long_description)
    {
	this.long_description = long_description;
    }

    public void setStatus(String status)
    {
	this.status = status;
    }

    public void setFeatured(boolean featured)
    {
	this.featured = featured;
    }

    public void setEventHash(String eventHash)
    {
	this.eventHash = eventHash;
    }
    
    public LocalDate getLocalDate()
    {
	return localDate;
    }
    
    public LocalTime getLocalTime()
    {
	return localtime;
    }

    public String getStatus()
    {
	return status;
    }

    public String getTitle()
    {
	return title;
    }

    public String getWebsite()
    {
	return website;
    }

    public void setWebsite(String website)
    {
	this.website = website;
    }

    public String getPrice()
    {
	return price;
    }

    public void setPrice(String price)
    {
	this.price = price;
    }
    
}

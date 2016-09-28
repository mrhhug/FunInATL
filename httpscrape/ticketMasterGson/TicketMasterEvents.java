package com.webreadllc.FunInATL.httpscrape.ticketMasterGson;

import java.util.List;

/**
 * @author michael
 */
public class TicketMasterEvents
{
    private String name;
    private String url;
    private String id;
    private String description;
    private List<TicketMasterImage> images;
    private TicketMasterDates dates;
    private List<TicketMasterClassifications> classifications;
    private List<TicketMasterPriceRanges> priceRanges;
    private TicketMasterLowerEmembedded _embedded;

    public String getName()
    {
	return name;
    }

    public String getUrl()
    {
	return url;
    }

    public String getId()
    {
	return id;
    }
    
    public String getDescription()
    {
	return description;
    }

    public List<TicketMasterImage> getImages()
    {
	return images;
    }

    public TicketMasterDates getDates()
    {
	return dates;
    }

    public List<TicketMasterClassifications> getClassifications()
    {
	return classifications;
    }

    public List<TicketMasterPriceRanges> getPriceRanges()
    {
	return priceRanges;
    }

    public TicketMasterLowerEmembedded getEmbedded()
    {
	return _embedded;
    }
}

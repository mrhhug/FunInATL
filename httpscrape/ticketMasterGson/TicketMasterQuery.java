package com.webreadllc.FunInATL.httpscrape.ticketMasterGson;

/**
 * @author michael
 */
public class TicketMasterQuery
{
    private TicketMasterHighLevelEmbedded _embedded;
    //yeah this will only get the first 500
    //private page page;

    public TicketMasterHighLevelEmbedded getEmbedded()
    {
	return _embedded;
    }
}

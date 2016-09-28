package com.webreadllc.FunInATL.httpscrape.ticketMasterGson;

import java.math.BigDecimal;

/**
 * @author michael
 */
public class TicketMasterPriceRanges
{
    private BigDecimal min;
    private BigDecimal max;
    private String type;

    public BigDecimal getMin()
    {
	return min;
    }

    public BigDecimal getMax()
    {
	return max;
    }

    public String getType()
    {
	return type;
    }
}

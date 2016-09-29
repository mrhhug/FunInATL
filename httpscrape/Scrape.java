package com.webreadllc.FunInATL.httpscrape;

import com.webreadllc.FunInATL.event.Event;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author michael
 */
public interface Scrape
{
    public Set<Event> scrape() throws IOException;
}

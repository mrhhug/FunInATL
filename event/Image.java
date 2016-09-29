package com.webreadllc.FunInATL.event;

/**
 * @author michael
 */
public class Image
{
    private String height;
    private String width;
    private String ratio;
    private String URL;

    public Image(String height, String width, String ratio, String URL)
    {
	this.height = height;
	this.width = width;
	this.ratio = ratio;
	this.URL = URL;
    }

    public Image()
    {
    }

    public void setHeight(String height)
    {
	this.height = height;
    }

    public void setWidth(String width)
    {
	this.width = width;
    }

    public void setRatio(String ratio)
    {
	this.ratio = ratio;
    }

    public void setURL(String URL)
    {
	this.URL = URL;
    }
    
}

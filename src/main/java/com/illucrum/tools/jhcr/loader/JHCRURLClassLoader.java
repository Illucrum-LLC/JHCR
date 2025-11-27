package com.illucrum.tools.jhcr.loader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class JHCRURLClassLoader extends URLClassLoader
{
    private static final String PROTOCOL = "file://";

    public JHCRURLClassLoader (URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    protected void appendToClassPath (String jarName) throws URISyntaxException, MalformedURLException
    {
        try
        {
            URI uri = new URI(PROTOCOL + jarName);

            this.addURL(uri.toURL());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

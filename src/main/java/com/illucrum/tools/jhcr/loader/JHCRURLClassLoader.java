/*
 * Copyright (C) 2025, Illucrum LLC
 *
 * This file is part of JHCR.
 *
 * JHCR is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * JHCR is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with JHCR. If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 */
package com.illucrum.tools.jhcr.loader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class ensures the custom system class loader used by JHCR can implement the
 * {@link com.illucrum.tools.jhcr.loader.JHCRClassLoader#appendToClassPathForInstrumentation(String)}.
 * 
 * @author Szymon Kokot
 * 
 * @see com.illucrum.tools.jhcr.loader.JHCRClassLoader#appendToClassPathForInstrumentation(String)
 */
public class JHCRURLClassLoader extends URLClassLoader
{
    private static final String PROTOCOL = "file://";

    /**
     * Constructs a new JHCRClassLoader for the given URLs.
     * 
     * @param urls the URLs from which to load classes and resources
     * @param parent the parent class loader for delegation
     */
    public JHCRURLClassLoader (URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    /**
     * This method converts the given string to a URL, and calls {@link #addURL(URL)}.
     * 
     * @param jarName The path to the jar file
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    protected void appendToClassPath (String jarName) throws URISyntaxException, MalformedURLException
    {
        try
        {
            URI uri;
            if (jarName.contains("://"))
            {
                uri = new URI(jarName);
            }
            else
            {
                uri = new URI(PROTOCOL + jarName);
            }

            this.addURL(uri.toURL());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

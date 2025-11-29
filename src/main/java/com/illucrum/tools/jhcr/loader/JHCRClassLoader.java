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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * This is a wrapper class loader that allows for that makes use of {@link com.illucrum.tools.jhcr.repo.JHCRRepository} and allows classes overrides.
 * 
 * It falls back on {@link com.illucrum.tools.jhcr.loader.JHCRURLClassLoader} to implement the {@link #appendToClassPathForInstrumentation(String)} method.
 * 
 * @author Szymon Kokot
 * 
 * @see com.illucrum.tools.jhcr.loader.JHCRURLClassLoader
 */
public class JHCRClassLoader extends ClassLoader
{
    private JHCRURLClassLoader urlLoader;

    /**
     * Constructs a new JHCRClassLoader
     * 
     * @param parent
     */
    public JHCRClassLoader (ClassLoader parent)
    {
        super(parent);
        String separator = System.getProperty("path.separator");
        String classPath = System.getProperty("java.class.path");

        if (classPath != null)
        {
            String[] list = classPath.split(separator);

            this.urlLoader = new JHCRURLClassLoader(new URL[] {}, null);

            for (String p : list)
            {
                try
                {
                    if (p.length() == 0)
                    {
                        this.urlLoader.appendToClassPath("./");
                    }
                    else
                    {
                        this.urlLoader.appendToClassPath(p);
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    @Override
    public Class<?> loadClass (String name) throws ClassNotFoundException
    {
        return this.loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException
    {
        JHCRLogger.finest("Loading: " + name + " from system loader...");
        Class<?> result = null;

        try
        {
            result = urlLoader.loadClass(name);
        }
        catch (ClassNotFoundException e)
        {
            JHCRLogger.fine("Class " + name + " not loaded by URL class loader.");
        }

        if (result == null)
        {
            try
            {
                result = super.loadClass(name, resolve);
            }
            catch (ClassNotFoundException e)
            {
                JHCRLogger.fine("Class " + name + " not loaded by parent.");
            }
        }

        if (result == null)
        {
            throw new ClassNotFoundException();
        }
        else if (resolve)
        {
            this.resolveClass(result);
        }

        return result;
    }

    /**
     * Getter method for the {@link com.illucrum.tools.jhcr.loader.JHCRURLClassLoader} in use.
     * @return the url class loader in use
     */
    public JHCRURLClassLoader getURLClassLoader ()
    {
        return this.urlLoader;
    }

    /**
     * This method ensures this custom class loader can be used as system class loader in modern Java applications
     * 
     * @param jarName
     *            the name of the dependency to be included
     * @throws URISyntaxException
     * @throws MalformedURLException
     * 
     * @see java.lang.instrument.Instrumentation#appendToSystemClassLoaderSearch(JarFile)
     */
    public void appendToClassPathForInstrumentation (String jarName) throws URISyntaxException, MalformedURLException
    {
        this.urlLoader.appendToClassPath(jarName);
    }
}

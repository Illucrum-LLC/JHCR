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

import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.repo.JHCRRepository;

/**
 * Application class loader for JHCR. It allows for class overriding.
 * 
 * @author Szymon Kokot
 */
public class JHCRURLClassLoader extends URLClassLoader
{
    private static final String PROTOCOL = "file://";

    public static ClassLoader customLoader;

    /**
     * Constructs a new JHCRClassLoader for the given URLs.
     * 
     * @param urls
     *            the URLs from which to load classes and resources
     * @param parent
     *            the parent class loader for delegation
     */
    public JHCRURLClassLoader (URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass (String name) throws ClassNotFoundException
    {
        return this.loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException
    {
        JHCRLogger.finest("Loading: " + name);

        Class<?> result = JHCRRepository.get(name);

        if (result != null)
        {
            if (resolve)
            {
                this.resolveClass(result);
            }

            return result;
        }

        JHCRLogger.fine("Class " + name + " not found in repository.");

        try
        {
            result = super.loadClass(name, resolve);
            JHCRRepository.put(name, result);
        }
        catch (ClassNotFoundException e)
        {
            JHCRLogger.fine("Class " + name + " not loaded by super.");
        }

        if (result == null && customLoader != null)
        {
            try
            {
                result = customLoader.loadClass(name);
            }
            catch (Exception e)
            {
                JHCRLogger.fine("Class " + name + " not loaded by custom loader.");
            }
        }

        if (result == null && JHCRClassLoader.parent != null)
        {
            try
            {
                result = JHCRClassLoader.parent.loadClass(name);
            }
            catch (Exception e)
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
     * Just a wrapper for the {@link java.lang.ClassLoader#defineClass(String, byte[], int, int)}
     * 
     * @param name
     *            Name of the class to be defined.
     * @param bytecode
     *            Bytecode of the class to be defined
     * @param off
     *            The start offset of the class data
     * @param end
     *            Length of the class data
     * @return the class returned by {@link java.lang.ClassLoader#defineClass(String, byte[], int, int)}
     */
    public Class<?> defineClassWrapper (String name, byte[] bytecode, int off, int len)
    {
        return super.defineClass(name, bytecode, off, len);
    }

    /**
     * This method converts the given string to a URL, and calls {@link #addURL(URL)}.
     * 
     * @param jarName
     *            The path to the jar file
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
            e.printStackTrace();
        }
    }
}

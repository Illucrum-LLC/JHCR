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

import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.repo.JHCRRepository;

/**
 * This is a wrapper class loader that allows for that makes use of {@link com.illucrum.tools.jhcr.repo.JHCRRepository} and allows classes overrides.
 * 
 * @author Szymon Kokot
 */
public class JHCRClassLoader extends ClassLoader
{
    private JHCRURLClassLoader urlLoader;

    /**
     * Creates a new class loader calling super with no arguments.
     */
    public JHCRClassLoader ()
    {
        super();
        this.urlLoader = new JHCRURLClassLoader(new URL[] {}, null);
    }

    public JHCRClassLoader (ClassLoader parent)
    {
        super(parent);
        this.urlLoader = new JHCRURLClassLoader(new URL[] {}, null);
    }

    @Override
    public Class<?> loadClass (String name) throws ClassNotFoundException
    {
        return this.loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException
    {
        Class<?> result = JHCRRepository.get(name);

        if (result != null)
        {
            if (resolve)
            {
                super.resolveClass(result);
            }

            return result;
        }

        JHCRLogger.fine("Class " + name + " not found in repository.");

        try
        {
            result = super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException e)
        {
            JHCRLogger.fine("Class " + name + " not loaded by parent.");
        }

        if (result == null)
        {
            try
            {
                result = urlLoader.loadClass(name);
            }
            catch (ClassNotFoundException e)
            {
                JHCRLogger.fine("Class " + name + " not loaded by URL class loader.");
            }
        }

        if (result != null)
        {
            JHCRRepository.put(name, result);
        }
        else
        {
            throw new ClassNotFoundException();
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

    public void appendToClassPathForInstrumentation (String jarName) throws URISyntaxException, MalformedURLException
    {
        System.out.println("inside appendToClassPathForInstrumentation: " + jarName);
        this.urlLoader.appendToClassPath(jarName);
    }
}

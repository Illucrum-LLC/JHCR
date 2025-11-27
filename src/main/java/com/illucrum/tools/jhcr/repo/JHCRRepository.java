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
 * You should have received a copy of the GNU General Public License along with JHCR. If not, see <https://www.gnu.org/licenses/>.
 */
package com.illucrum.tools.jhcr.repo;

import java.util.Map.Entry;
import java.util.WeakHashMap;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * JHCRRepository is a static class that is meant to store all the classes that could be reloaded in the future.
 * 
 * Each class stored is identified by it's defining class loader object and class' fully qualified name.
 * 
 * @author Szymon Kokot
 */
public class JHCRRepository
{
    private static final WeakHashMap<String, Class<?>> classes = new WeakHashMap<>();

    /**
     * This method calls {@link #put(ClassLoader, Class, String)} with <code> clazz.getName() </code> as class name.
     * 
     * @see #put(ClassLoader, Class<?>, String)
     * @param classLoader
     *            classes' to be stored defining class loader
     * @param clazz
     *            the class to be stored
     */
    public static void put (Class<?> clazz)
    {
        put(clazz.getName(), clazz);
    }

    /**
     * It stores a class given it's defining class loader and a name. As the class name is a separate argument, you can overwrite already stored classes.
     * 
     * @param classLoader
     *            classes' to be stored defining class loader
     * @param clazz
     *            the class to be stored
     * @param className
     *            the canonical name of the class to be stored.
     */
    public static void put (String className, Class<?> clazz)
    {
        JHCRLogger.finer("Saving " + className + "...");

        classes.put(className, clazz);
    }

    /**
     * Returns a stored class given it's defining class loader and a name. May return null.
     * 
     * @param classLoader
     *            the defining class loader of the class to be returned
     * @param className
     *            the name of the class to be returned
     * @return returns the class or null if none is found
     */
    public static Class<?> get (String className)
    {
        JHCRLogger.finer("Retrieving " + className + "...");

        return classes.get(className);
    }

    /**
     * Returns a string of the contents of the repository.
     * 
     * @return the contents of the repository.
     */
    public static String staticToString ()
    {
        String result = "JHCRRepository:\n";

        for (Entry<String, Class<?>> e : classes.entrySet())
        {
            result += "\t - " + e.getKey() + " -> " + e.getValue().getName() + "\n";
        }

        return result;
    }
}

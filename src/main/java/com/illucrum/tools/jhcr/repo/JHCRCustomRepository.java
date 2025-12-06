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

import java.util.WeakHashMap;

/**
 * JHCRCustomRepository is a static class that is meant to store all the classes that could be reloaded in the future, and were loaded by a custom class loader.
 * 
 * Each class stored is identified by it's defining class loader object and the class' fully qualified name.
 * 
 * @author Szymon Kokot
 */
public class JHCRCustomRepository
{
    private static WeakHashMap<ClassLoader, WeakHashMap<String, Class<?>>> classes = new WeakHashMap<>();

    /**
     * This method calls {@link #put(ClassLoader, Class, String)} with <code> clazz.getName() </code> as class name.
     * 
     * @see #put(ClassLoader, String, Class<?>)
     * @param classLoader
     *            classes' to be stored defining class loader
     * @param clazz
     *            the class to be stored
     */
    public static synchronized void put (ClassLoader loader, Class<?> clazz)
    {
        put(loader, clazz.getName(), clazz);
    }

    /**
     * It stores a class given it's defining class loader and a name. As the class name is a separate argument, you can overwrite already stored classes.
     * 
     * @param loader
     *            the defining class loader
     * @param className
     *            the canonical name of the class to be stored.
     * @param clazz
     *            the class to be stored
     */
    public static void put (ClassLoader loader, String className, Class<?> clazz)
    {
        WeakHashMap<String, Class<?>> classesMap = classes.get(loader);

        if (classesMap == null)
        {
            classesMap = new WeakHashMap<>();
            classesMap.put(className, clazz);
            classes.put(loader, classesMap);
            return;
        }

        classesMap.put(className, clazz);
    }

    /**
     * Returns a stored class given it's defining class loader and a name. May return null.
     * 
     * @param loader
     *            the defining class loader
     * @param className
     *            the name of the class to be returned
     * @return returns the class or null if none is found
     */
    public static synchronized Class<?> get (ClassLoader loader, String className)
    {
        WeakHashMap<String, Class<?>> classesMap = classes.get(loader);

        return classesMap == null ? null : classesMap.get(className);
    }
}

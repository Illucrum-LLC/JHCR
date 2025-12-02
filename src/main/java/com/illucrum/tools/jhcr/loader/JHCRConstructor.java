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

import java.lang.reflect.Constructor;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * This class is has one static method that is used instead of construtors as part of the bytcode manipulation done by
 * {@link com.illucrum.tools.jhcr.writer.JHCRConstructorRewriter}.
 * 
 * @author Szymon Kokot
 */
public class JHCRConstructor
{
    /**
     * This method is to be used instead of constructors
     * 
     * @param className
     *            the binary class name of the class to be instanciated
     * @param types
     *            Array containing the types of the arguments passed to the constructor
     * @param values
     *            Array containing the values of the arguments passed to the constructor
     * @return resulting object
     */
    public static Object construct (String className, Class<?>[] types, Object[] values)
    {
        JHCRLogger.finest("Construct: " + className);
        try
        {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
            Constructor<?> constructor = clazz.getConstructor(types);
            return constructor.newInstance(values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}

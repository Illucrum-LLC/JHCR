package com.illucrum.tools.jhcr.loader;

import java.lang.reflect.Constructor;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

public class JHCRConstructor
{
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

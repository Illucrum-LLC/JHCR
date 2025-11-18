/*
 * Copyright (C) 2025, Illucrum LLC
 *
 * This file is part of JHCR.
 *
 * JHCR is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package com.illucrum.tools.jhcr;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.objectweb.asm.ClassReader;

import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.watcher.JHCRAlterationListener;

/**
 * JHCRThread is a thread started from {@link com.illucrum.tools.jhcr.JHCRAgent#premain(String, Instrumentation)} and is the main class of the HCR.
 * 
 * @author Szymon Kokot
 * 
 * @see com.illucrum.intershop.jhcr.JHCRAgent
 */
public class JHCRThread extends Thread
{
    private final static String FILE_EXTENSION = ".class";
    private final static Instrumentation instrumentation = JHCRAgent.instrumentation;
    private final Map<String, String> preferences;

    /**
     * 
     * @param preferences
     *            Map with parsed args
     */
    public JHCRThread (Map<String, String> preferences)
    {
        this.preferences = preferences;
    }

    /**
     * The main method of the thread. It initializes the monitor to watch the .class files from all cartridges included in the project
     * 
     * <p>
     * This method:
     * <ol>
     * <li>Reads the string path from preferences. If it can't, it tries the user.dir system property.</li>
     * <li>Creates the observer if the path is valid.</li>
     * <li>Creates the listener by means of the {@link com.illucrum.tools.jhcr.watcher.JHCRAlterationListener} class.</li>
     * <li>Creates the monitor with an interval of 1sec by default or the time specified in the preferences.</li>
     * <li>It starts the monitor</li>
     * </ol>
     * </p>
     * 
     * @see com.illucrum.tools.jhcr.watcher.JHCRAlterationListener
     */
    @Override
    public void run ()
    {
        JHCRLogger.info("Execution started.");

        String pathString = preferences.getOrDefault("jhcr.projectDirectoy", System.getProperty("user.dir"));
        File watchDir;
        FileAlterationObserver observer;

        try
        {
            watchDir = new File(pathString);

            if (!watchDir.exists() || !watchDir.isDirectory())
            {
                JHCRLogger.severe("Specified path is invalid: " + pathString);
                return;
            }

            observer = FileAlterationObserver.builder().setFile(watchDir).get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JHCRLogger.severe("Specified path is invalid: " + pathString);
            return;
        }

        FileAlterationListener listener = new JHCRAlterationListener(FILE_EXTENSION);
        observer.addListener(listener);

        FileAlterationMonitor monitor = new FileAlterationMonitor(getInterval());
        monitor.addObserver(observer);
        try
        {
            monitor.start();
            JHCRLogger.info("Monitoring started: " + pathString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JHCRLogger.severe("Error initializing monitor: " + e);
        }
    }

    private long getInterval ()
    {
        long interval;
        try
        {
            interval = Long.parseLong(preferences.get("jhcr.watcher.interval"));
        }
        catch (Exception e)
        {
            interval = 1000;
            JHCRLogger.fine("Interval set to default value of 1000ms.");
        }

        JHCRLogger.finer("Interval set to " + interval + "ms.");

        return interval;
    }

    /**
     * This method takes the bytecode as a byte array read from a class and attempts redefine it.
     * 
     * <p>
     * If the parameter is null or empty this method does nothing.
     * </p>
     * 
     * @param bytecode
     *            byte array read directly from the .class file.
     */
    public static void overrideClass (byte[] bytecode)
    {
        JHCRLogger.finest("Overriding...");
        if (bytecode == null || bytecode.length == 0)
        {
            JHCRLogger.fine("Override class bytecode null or empty.");
            return;
        }

        // Get the ClassLoader and the class name
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        ClassReader reader = new ClassReader(bytecode);
        String byteInternalName = reader.getClassName();
        String byteBinaryName = byteInternalName.replaceAll("/", ".");

        JHCRLogger.finer("Overriding: " + byteBinaryName);
        // Try to load class normally
        Class<?> clazz = null;
        try
        {
            clazz = classLoader.loadClass(byteBinaryName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JHCRLogger.fine("Could not load class: " + byteBinaryName);
        }

        if (clazz != null)
        {
            // If class was loaded, try to redefine it
            try
            {
                JHCRLogger.finer("Redefining: " + byteBinaryName);
                ClassDefinition classDefinition = new ClassDefinition(clazz, bytecode);
                instrumentation.redefineClasses(classDefinition);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.fine("Could not redefine class: " + byteBinaryName);
            }
        }
    }
}

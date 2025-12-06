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
package com.illucrum.tools.jhcr;

import java.lang.instrument.Instrumentation;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.illucrum.tools.jhcr.loader.JHCRClassLoader;
import com.illucrum.tools.jhcr.loader.JHCRURLClassLoader;
import com.illucrum.tools.jhcr.logger.JHCRFormatter;
import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * Agent for JHCR.
 * 
 * @see com.illucrum.tools.jhcr.JHCRThread
 * 
 * @author SzymonKokot
 */
public class JHCRAgent
{
    public static Instrumentation instrumentation;
    public static Map<String, String> preferences;

    /**
     * When called for the first time, sets the instrumentation, parses arguments and runs the {@link com.illucrum.tools.jhcr.JHCRThread}
     * thread. It also sets up the {@link com.illucrum.tools.jhcr.logger.JHCRLogger}.
     * 
     * <p>
     * When run a more than once, just prints a warning.
     * </p>
     * 
     * @param args
     *            string of arguments
     * @param inst
     *            instrumentation
     * 
     * @see com.illucrum.tools.jhcr.JHCRThread
     * @see com.illucrum.tools.jhcr.logger.JHCRLogger
     */
    public static void premain (String args, Instrumentation inst)
    {
        if (null == instrumentation)
        {
            instrumentation = inst;

            parseArgs(args);

            if (preferences.get("jhcr.logger.dateFormat") != null)
                JHCRFormatter.setDateFormat(new SimpleDateFormat(preferences.get("jhcr.logger.dateFormat")));
            if (preferences.get("jhcr.logger.prefix") != null)
                JHCRFormatter.setPrefix(preferences.get("jhcr.logger.prefix"));
            if (preferences.get("jhcr.logger.template") != null)
                JHCRFormatter.setTemplate(preferences.get("jhcr.logger.template"));

            JHCRLogger.addHandler(preferences.getOrDefault("jhcr.logger.fileName", "JHCRLogger.log"));

            switch (preferences.getOrDefault("jhcr.logger.level", ""))
            {
                case "config":
                    JHCRLogger.setLevel(Level.CONFIG);
                    break;
                case "fine":
                    JHCRLogger.setLevel(Level.FINE);
                    break;
                case "finer":
                    JHCRLogger.setLevel(Level.FINER);
                    break;
                case "finest":
                    JHCRLogger.setLevel(Level.FINEST);
                    break;
                case "info":
                    JHCRLogger.setLevel(Level.INFO);
                    break;
                case "severe":
                    JHCRLogger.setLevel(Level.SEVERE);
                    break;
                case "warning":
                    JHCRLogger.setLevel(Level.WARNING);
                    break;
                default:
                    JHCRLogger.setLevel(Level.ALL);
                    break;
            }

            JHCRLogger.info("Agent " + JHCRAgent.class.getName() + " executed.");

            ClassLoader loader = ClassLoader.getSystemClassLoader();

            if (!(loader instanceof JHCRClassLoader))
            {
                JHCRLogger
                        .fine(
                                "Class loader is not instance of JHCRClassLoader: " + loader.getClass().getCanonicalName() + " "
                                        + loader.getParent().getClass().getCanonicalName() + " " + loader.getParent().getParent());
                return;
            }

            instrumentation.addTransformer(new JHCRTransformer());

            String customLoaderName = preferences.get("jhcr.custom.loader");
            if (customLoaderName != null)
            {
                try
                {
                    Class<?> customLoader = loader.loadClass(customLoaderName);
                    if (customLoader.isInstance(ClassLoader.class))
                    {
                        JHCRURLClassLoader.customLoader = (ClassLoader) customLoader.newInstance();
                    }
                    else
                    {
                        JHCRLogger.fine("Custom class loader isn't instance of ClassLoder");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    JHCRLogger.fine("Failed loading custom class loader " + customLoaderName);
                }
            }

            Thread jhcr = new JHCRThread();
            jhcr.setDaemon(false);
            jhcr.start();
        }
        else
        {
            JHCRLogger.warning("Agent " + JHCRAgent.class.getName() + " executed once more!");
        }
    }

    private static void parseArgs (String args)
    {
        preferences = new HashMap<>();
        preferences.put("jhcr.suffix", "$JHCR$");

        if (args == null)
        {
            JHCRLogger.fine("No args passed");
            return;
        }

        for (String arg : args.split(";"))
        {
            String[] pair = arg.split("=", 2);
            if (pair.length == 2)
            {
                preferences.put(pair[0].trim(), pair[1].trim());
            }
        }
    }
}

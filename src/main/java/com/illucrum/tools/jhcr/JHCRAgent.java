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
 * You should have received a copy of the GNU General Public License along with JHCR. If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 */
package com.illucrum.tools.jhcr;

import java.lang.instrument.Instrumentation;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.illucrum.tools.jhcr.logger.JHCRFormatter;
import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * Basic agent for JHCR. It starts the {@link com.illucrum.tools.jhcr.JHCRThread}, which is the main class for the JHCR.
 * 
 * @see com.illucrum.tools.jhcr.JHCRThread
 * 
 * @author SzymonKokot
 */
public class JHCRAgent
{
    public static Instrumentation instrumentation;

    /**
     * Basic premain method. When run for the first time, sets the instrumentation and runs the {@link com.illucrum.tools.jhcr.JHCRThread} thread. I also sets up the {@link com.illucrum.tools.jhcr.logger.JHCRLogger}.
     * 
     * <p>
     * When run a more than once, it just prints a warning.
     * </p>
     * 
     * @param args
     * @param inst
     */
    public static void premain (String args, Instrumentation inst)
    {
        if (null == instrumentation)
        {
            instrumentation = inst;

            Map<String, String> preferences = parseArgs(args);

            if (preferences.get("jhcr.logger.dateFormat") != null)
                JHCRFormatter.setDateFormat(new SimpleDateFormat(preferences.get("jhcr.logger.dateFormat")));
            if (preferences.get("jhcr.logger.prefix") != null)
                JHCRFormatter.setPrefix(preferences.get("jhcr.logger.prefix"));
            if (preferences.get("jhcr.logger.template") != null)
                JHCRFormatter.setTemplate(preferences.get("jhcr.logger.template"));

            JHCRLogger.addHandler(preferences.getOrDefault("jhcr.logger.fileName", "JHCRLogger.log"));

            switch (preferences.getOrDefault("jhcr.logger.level", "")) {
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
            Thread jhcr = new JHCRThread(preferences);
            jhcr.setDaemon(false);
            jhcr.start();
        }
        else
        {
            JHCRLogger.warning("Agent " + JHCRAgent.class.getName() + " executed once more!");
        }
    }

    private static Map<String, String> parseArgs (String args)
    {
        Map<String, String> preferences = new HashMap<>();

        if(args == null) {
            JHCRLogger.fine("No args passed");
            return preferences;
        }

        for (String arg : args.split(";"))
        {
            String[] pair = arg.split("=", 2);
            if (pair.length == 2)
            {
                preferences.put(pair[0].trim(), pair[1].trim());
            }
        }

        return preferences;
    }
}

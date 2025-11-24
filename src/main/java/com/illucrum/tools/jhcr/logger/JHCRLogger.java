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
package com.illucrum.tools.jhcr.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger for the JHCR. Enables printing logs in a separate file.
 * 
 * <p>Note, that before using the logger, you should call the {@link this#addHandler(String)} method.</p>
 * 
 * @author Szymon Kokot
 */
public class JHCRLogger {
    private static final Logger LOGGER = Logger.getLogger(JHCRLogger.class.getName());

    /**
     * Prints a message on the config level.
     * 
     * @param msg the message to print
     */
    public static void config(String msg) {
        LOGGER.config(msg);
    }

    /**
     * Prints a message on the fine level.
     * 
     * @param msg the message to print
     */
    public static void fine(String msg) {
        LOGGER.fine(msg);
    }

    /**
     * Prints a message on the finer level.
     * 
     * @param msg the message to print
     */
    public static void finer(String msg) {
        LOGGER.finer(msg);
    }

    /**
     * Prints a message on the finest level.
     * 
     * @param msg the message to print
     */
    public static void finest(String msg) {
        LOGGER.finest(msg);
    }

    /**
     * Prints a message on the info level.
     * 
     * @param msg the message to print
     */
    public static void info(String msg) {
        LOGGER.info(msg);
    }

    /**
     * Prints a message on the severe level.
     * 
     * @param msg the message to print
     */
    public static void severe(String msg) {
        LOGGER.severe(msg);
    }

    /**
     * Prints a message on the warning level.
     * 
     * @param msg the message to print
     */
    public static void warning(String msg) {
        LOGGER.warning(msg);
    }

    /**
     * Wrapper for the {@link java.util.logging.Logger#setLevel(Level)} method
     * 
     * @see java.util.logging.Logger#setLevel(Level)
     * 
     * @param level the new level value
     */
    public static void setLevel(Level level) {
        LOGGER.setLevel(level);
    }

    /**
     * Adds a file handler to the logger with the specified name. Should be executed before using the logger.
     * 
     * @param fileName the name of the log file
     */
    public static void addHandler(String fileName) {
        try {
            FileHandler fileHandler = new FileHandler(fileName, true);
            fileHandler.setFormatter(new JHCRFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);

            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

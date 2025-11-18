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
package com.illucrum.tools.jhcr.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JHCRFormatter extends Formatter
{
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
    private static String prefix = "JHCR";
    private static String template =  "[%s] %s: %s: %s\n";

    @Override
    public String format (LogRecord record)
    {
        String timeStamp = dateFormat.format(new Date());
        return String.format(template, timeStamp, prefix, record.getLevel().toString(), record.getMessage());
    }

    /**
     * Getter for the date format used when printing logs.
     * 
     * @return current date format
     */
    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Setter for the date format used when printing logs. Any changes apply immediately for following logs.
     * 
     * @param format the format to be used.
     */
    public static void setDateFormat(SimpleDateFormat format) {
        dateFormat = format;
    }

    /**
     * Getter for the prefix used when printing logs.
     * 
     * @return current prefix
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * Setter for the prefix used when printing logs. Any changes apply immediately for following logs.
     * 
     * @param pref the prefix to be used.
     */
    public static void setPrefix(String pref) {
        prefix = pref;
    }

    /**
     * Getter for the template used when printing logs.
     * 
     * @return current template
     */
    public static String getTemplate() {
        return template;
    }

    /**
     * Setter for the template used when printing logs. Any changes apply immediately for following logs.
     * 
     * @param tpl the template to be used.
     */
    public static void setTemplate(String tpl) {
        template = tpl;
    }
}

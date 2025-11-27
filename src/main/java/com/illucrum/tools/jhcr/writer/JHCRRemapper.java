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
package com.illucrum.tools.jhcr.writer;

import org.objectweb.asm.commons.Remapper;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * This class renames the class of a byte array to make sure it can be defined under some other name when it already have been defined and it's structure changed.
 * 
 * @author Szymon Kokot
 */
public class JHCRRemapper extends Remapper
{
    private final String oldName;
    private String newName;

    /**
     * Only constructor that should be used.
     * 
     * @param oldName original name of the class being renamed.
     * @param suffix suffix to be used when renaming. If null, it will keep it's default value: $JHCR$
     */
    public JHCRRemapper(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String map (String name)
    {
        if (name.equals(this.oldName))
        {
            JHCRLogger.finer("Renaming " + name + " to " + this.newName);
            return this.newName;
        }

        return name;
    }
}

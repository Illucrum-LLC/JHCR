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
 * This remapper modifies the class name in a bytecode.
 * 
 * @author Szymon Kokot
 */
public class JHCRRemapper extends Remapper
{
    private String oldName;
    private String newName;

    /**
     * Constructs an empty JHCRRemmaper. Before use, the original name and the new name should be set with {@link #setOldName(String)} and {@link #setNewName(String)}.
     */
    public JHCRRemapper ()
    {
    }

    /**
     * Constructs a new {@link com.illucrum.tools.jhcr.writer.JHCRRemapper}.
     * 
     * @param oldName
     *            original name of the class being renamed.
     * @param newName
     *            suffix to be used when renaming. If null, it will keep it's default value: $JHCR$
     */
    public JHCRRemapper (String oldName, String newName)
    {
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

    /**
     * Setter method for the original class name
     * 
     * @param oldName original class name
     */
    public void setOldName (String oldName)
    {
        this.oldName = oldName;
    }

    /**
     * Setter method for the new class name
     * 
     * @param newName new class name
     */
    public void setNewName (String newName)
    {
        this.newName = newName;
    }
}

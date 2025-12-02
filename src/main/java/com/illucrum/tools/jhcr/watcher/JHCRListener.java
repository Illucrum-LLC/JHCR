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
package com.illucrum.tools.jhcr.watcher;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * This class is responsible of keeping track of what files are being changed. When it detects a file change, it triggers the class redefinition or override.
 * 
 * @author Szymon Kokot
 */
public class JHCRListener extends FileAlterationListenerAdaptor
{
    private final String fileExtension;

    /**
     * Constructs a new {@link com.illucrum.tools.jhcr.watcher.JHCRListener}
     * @param fileExtension
     */
    public JHCRListener (String fileExtension)
    {
        this.fileExtension = fileExtension;
    }

    @Override
    public void onFileCreate (File file)
    {
        String fileName = file.getName();

        if (fileName.endsWith(this.fileExtension))
        {
            JHCRLogger.finest("File created...");
            Path path = Paths.get(file.getAbsolutePath());
            JHCRLogger.finer("Reading: " + file.getAbsolutePath());

            try
            {
                byte[] compiled = Files.readAllBytes(path);
                JHCROverrider.overrideClass(compiled);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.warning("Error loading a new file: " + file.getAbsolutePath());
            }
        }
    }

    @Override
    public void onFileChange (File file)
    {
        String fileName = file.getName();
        if (fileName.endsWith(this.fileExtension))
        {
            JHCRLogger.finest("File modified...");
            Path path = Paths.get(file.getAbsolutePath());
            JHCRLogger.finer("Reading: " + file.getAbsolutePath());

            try
            {
                byte[] compiled = Files.readAllBytes(path);
                JHCROverrider.overrideClass(compiled);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.warning("Error loading a mmodified file: " + file.getAbsolutePath());
            }
        }
    }
}

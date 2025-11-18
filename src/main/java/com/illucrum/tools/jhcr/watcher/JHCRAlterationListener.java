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
package com.illucrum.tools.jhcr.watcher;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.illucrum.tools.jhcr.JHCRThread;
import com.illucrum.tools.jhcr.logger.JHCRLogger;

public class JHCRAlterationListener extends FileAlterationListenerAdaptor
{
    private final String fileExtension;

    public JHCRAlterationListener(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public void onFileCreate (File file)
    {
        JHCRLogger.finest("File created...");
        String fileName = file.getName();
        Path path = Paths.get(file.getAbsolutePath());
        JHCRLogger.finer("Reading: " + file.getAbsolutePath());

        if (fileName.endsWith(this.fileExtension))
        {
            try
            {
                byte[] compiled = Files.readAllBytes(path);
                JHCRThread.overrideClass(compiled);
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
        JHCRLogger.finest("File modified...");
        String fileName = file.getName();
        Path path = Paths.get(file.getAbsolutePath());
        JHCRLogger.finer("Reading: " + file.getAbsolutePath());

        if (fileName.endsWith(this.fileExtension))
        {
            try
            {
                byte[] compiled = Files.readAllBytes(path);
                JHCRThread.overrideClass(compiled);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.warning("Error loading a mmodified file: " + file.getAbsolutePath());
            }
        }
    }
}

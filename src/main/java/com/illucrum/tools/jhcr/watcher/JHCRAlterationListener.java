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

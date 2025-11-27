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

import java.lang.instrument.ClassDefinition;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;

import com.illucrum.tools.jhcr.JHCRAgent;
import com.illucrum.tools.jhcr.loader.JHCRClassLoader;
import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.repo.JHCRRepository;
import com.illucrum.tools.jhcr.writer.JHCRRemapper;

/**
 * This class holds the method that is responsible of redefinition or override of modified classes.
 * 
 * @author Szymon Kokot
 */
public class JHCROverrider
{
    private static final String SUFFIX = "$JHCR$";
    private static long counter = -1;

    /**
     * This method takes the bytecode as a byte array and attempts redefine it. If not possible, it attempts to override the original.
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
        ClassReader reader = new ClassReader(bytecode);
        String byteInternalName = reader.getClassName();
        String byteBinaryName = byteInternalName.replaceAll("/", ".");

        JHCRClassLoader loader = (JHCRClassLoader) ClassLoader.getSystemClassLoader();

        if (loader == null)
        {
            JHCRLogger.fine("No class Loader set. Can't override...");
            return;
        }

        JHCRLogger.finer("Overriding: " + byteBinaryName);

        Class<?> clazz = null;
        try
        {
            clazz = loader.loadClass(byteBinaryName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JHCRLogger.fine("Could not load class: " + byteBinaryName);
            return;
        }

        if (clazz != null)
        {
            // If class was loaded, try to redefine it
            try
            {
                JHCRLogger.finer("Redefining: " + byteBinaryName);
                ClassDefinition classDefinition = new ClassDefinition(clazz, bytecode);
                JHCRAgent.instrumentation.redefineClasses(classDefinition);
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.fine("Could not redefine class: " + byteBinaryName);
            }

            JHCRLogger.finer("Overriding: " + byteBinaryName);

            counter++;
            String suffix = JHCRAgent.preferences.getOrDefault("jhcr.suffix", SUFFIX);
            String newInternalName = byteInternalName + suffix + counter;
            String newBinaryName = byteBinaryName + suffix + counter;

            JHCRRemapper remapper = new JHCRRemapper(byteInternalName, newInternalName);

            ClassWriter writer = new ClassWriter(reader, 0);
            ClassVisitor visitor = new ClassRemapper(writer, remapper);

            reader.accept(visitor, 0);
            byte[] newBytecode = writer.toByteArray();

            JHCRLogger.finest("New internal: " + newInternalName + "; New binary: " + newBinaryName);

            Class<?> newClazz = loader.defineClassWrapper(newBinaryName, newBytecode, 0, newBytecode.length);

            JHCRRepository.put(byteBinaryName, newClazz);

            JHCRLogger.finest(JHCRRepository.staticToString());
        }
    }
}

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
import com.illucrum.tools.jhcr.loader.JHCRCustomLoader;
import com.illucrum.tools.jhcr.loader.JHCRURLClassLoader;
import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.repo.JHCRCustomRepository;
import com.illucrum.tools.jhcr.repo.JHCRRepository;
import com.illucrum.tools.jhcr.writer.JHCRRemapper;

/**
 * This class holds the method that is responsible of redefinition or override of modified classes.
 * 
 * @author Szymon Kokot
 */
public class JHCROverrider
{
    private static long counter = -1;

    /**
     * This method takes the bytecode as a byte array and attempts redefine the class it contains. If not possible, it attempts to override the original class.
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

        ClassReader reader = new ClassReader(bytecode);
        String byteInternalName = reader.getClassName();
        String byteBinaryName = byteInternalName.replaceAll("/", ".");

        JHCRURLClassLoader loader = ((JHCRClassLoader) ClassLoader.getSystemClassLoader()).getURLClassLoader();

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
            JHCRLogger.fine("Could not load class: " + byteBinaryName);
            return;
        }

        if (clazz != null)
        {
            String classBinaryName = clazz.getCanonicalName();
            String classInternalName = classBinaryName.replaceAll("\\.", "/");

            JHCRRemapper remapper = new JHCRRemapper(byteInternalName, classInternalName);

            ClassWriter writer = new ClassWriter(reader, 0);
            ClassVisitor visitor = new ClassRemapper(writer, remapper);
            byte[] newBytecode;

            if (!classBinaryName.equals(byteBinaryName))
            {
                reader.accept(visitor, 0);
                newBytecode = writer.toByteArray();
            }
            else
            {
                newBytecode = bytecode;
            }

            try
            {
                JHCRLogger.finer("Redefining: " + classBinaryName);
                ClassDefinition classDefinition = new ClassDefinition(clazz, newBytecode);
                JHCRAgent.instrumentation.redefineClasses(classDefinition);
                return;
            }
            catch (Exception e)
            {
                JHCRLogger.fine("Could not redefine class: " + byteBinaryName);
            }

            JHCRLogger.finer("Overriding: " + byteBinaryName);

            counter++;
            reader = new ClassReader(bytecode);
            writer = new ClassWriter(reader, 0);
            visitor = new ClassRemapper(writer, remapper);

            String suffix = JHCRAgent.preferences.get("jhcr.suffix");
            String newInternalName = byteInternalName + suffix + counter;
            String newBinaryName = byteBinaryName + suffix + counter;

            JHCRLogger.finest("New internal: " + newInternalName + "; New binary: " + newBinaryName);

            remapper.setNewName(newInternalName);
            reader.accept(visitor, 0);
            newBytecode = writer.toByteArray();

            JHCRCustomLoader customLoader = (JHCRCustomLoader) clazz.getClassLoader();

            try
            {
                Class<?> newClazz = customLoader.defineClassWrapper(newBinaryName, newBytecode, 0, newBytecode.length);

                if (loader.equals(customLoader))
                {
                    JHCRRepository.put(byteBinaryName, newClazz);
                }
                else
                {
                    JHCRCustomRepository.put(byteBinaryName, newClazz);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.fine("Error performing override.");
            }

        }
    }
}

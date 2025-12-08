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
 * You should have received a copy of the GNU General Public License along with JHCR. If not, see <https://www.gnu.org/licenses/>.
 */
package com.illucrum.tools.jhcr;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import com.illucrum.tools.jhcr.loader.JHCRCustomLoader;
import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.writer.JHCRClassVisitor;

/**
 * Transformer for JHCR. It triggers bytecode manipulation, for classes loaded by {@link com.illucrum.tools.jhcr.loader.JHCRURLClassLoader}.
 * 
 * @author Szymon Kokot
 * 
 * @see com.illucrum.tools.jhcr.writer.JHCRClassVisitor
 */
public class JHCRTransformer implements ClassFileTransformer
{
    @Override
    public byte[] transform (ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException
    {
        if (loader instanceof JHCRCustomLoader)
        {
            JHCRLogger.finer("Transforming: " + className);

            try
            {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                ClassVisitor classVisitor = new JHCRClassVisitor(classWriter);

                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

                return classWriter.toByteArray();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JHCRLogger.fine("Error on class visiting: " + className + ": " + e.getMessage());
            }
        }

        return classfileBuffer;
    }
}

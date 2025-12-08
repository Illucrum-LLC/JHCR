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

import java.util.regex.Pattern;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

import com.illucrum.tools.jhcr.JHCRAgent;
import com.illucrum.tools.jhcr.logger.JHCRLogger;
import com.illucrum.tools.jhcr.vars.JHCRVariables;

/**
 * This class is responsible of rewriting constructor calls to call {@link com.illucrum.tools.jhcr.loader.JHCRConstructor#construct(String, Class<?>[],
 * Object[])} instead and correct overriden classes super names
 * 
 * @author Szymon Kokot
 */
public class JHCRClassVisitor extends ClassVisitor implements JHCRVariables
{
    private final String suffix;
    private String superName = null;
    private boolean loader = false;

    /**
     * Calls super ({@link org.objectweb.asm.ClassVisitor#ClassVisitor(int, ClassVisitor)}) with ASM9 opcode and the same class visitor.
     * 
     * @param classVisitor
     * 
     * @see org.objectweb.asm.ClassVisitor#ClassVisitor(int, ClassVisitor)
     */
    public JHCRClassVisitor (ClassVisitor classVisitor)
    {
        super(API, classVisitor);
        this.suffix = JHCRAgent.preferences.get("jhcr.suffix");
    }

    @Override
    public void visit (int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        JHCRLogger.finer("Class visit: " + name + " " + signature + " " + superName);
        String finalSuperName = superName;
        String[] finalInterfaces = null;

        if (superName.equals(LOADER_NAME) || superName.equals(URL_LOADER_NAME))
        {
            finalInterfaces = new String[interfaces.length + 1];
            finalInterfaces[interfaces.length] = CUSTOM_LOADER_NAME;

            this.loader = true;
            for (int i = 0; i < interfaces.length; i++)
            {
                if (interfaces[i].equals(CUSTOM_LOADER_NAME))
                {
                    finalInterfaces = null;
                    JHCRLogger.finest("Interface: " + interfaces[i]);
                    this.loader = false;
                    break;
                }

                finalInterfaces[i] = interfaces[i];
            }
        }

        if (name.contains(this.suffix))
        {
            finalSuperName = name.replaceAll(Pattern.quote(this.suffix) + ".*", "");
            this.superName = finalSuperName;
            JHCRLogger.finer("Changing " + name + " super name " + superName + " to " + finalSuperName);
        }

        JHCRLogger.finer("Final interface added: " + (finalInterfaces == null ? "null" : finalInterfaces[interfaces.length]));

        super.visit(version, access, name, signature, finalSuperName, finalInterfaces == null ? interfaces : finalInterfaces);
    }

    @Override
    public MethodVisitor visitMethod (int access, String name, String desc, String signature, String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        AnalyzerAdapter analyzer = new AnalyzerAdapter(name, access, name, desc, mv);
        if (this.superName != null && name.equals("<init>"))
        {
            JHCRLogger.finer("Class visit method to change type: " + name + " " + desc + " " + signature);
            return new JHCRTypeRewriter(analyzer, this.superName);
        }

        if (this.loader && name.equals(LOAD_NAME) && desc.equals(LOAD_DESC))
        {
            JHCRLogger.finer("Class visit method to modify loadClass");
            return new JHCRLoadRewriter(analyzer);
        }

        if (!this.loader)
        {
            JHCRLogger.finer("Class visit method to rewrite constructors: " + name + " " + desc + " " + signature);
            return new JHCRInitRewriter(analyzer, name);
        }

        return mv;
    }

    @Override
    public void visitEnd ()
    {
        if (this.loader)
        {
            MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, DEFINE_WRAPPER_NAME, DEFINE_WRAPPER_DESC, null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitVarInsn(Opcodes.ILOAD, 3);
            mv.visitVarInsn(Opcodes.ILOAD, 4);
            mv
                    .visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            "java/lang/ClassLoader",
                            "defineClass",
                            "(Ljava/lang/String;[BII)Ljava/lang/Class;",
                            false);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }

        super.visitEnd();
    }
}

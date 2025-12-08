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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

/**
 * Method visitor responsible for bytecode manipulation in the {@link java.lang.ClassLoader#loadClass(String, boolean)} methods of custom class loaders.
 * 
 * @author Szymon Kokot
 */
public class JHCRLoadRewriter extends MethodVisitor
{
    private static final String CUSTOM_REPO_NAME = "com/illucrum/tools/jhcr/repo/JHCRCustomRepository";
    private static final String CUSTOM_REPO_GET_NAME = "get";
    private static final String CUSTOM_REPO_GET_DESC = "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;";
    private static final String CUSTOM_REPO_PUT_NAME = "put";
    private static final String CUSTOM_REPO_PUT_DESC = "(Ljava/lang/String;Ljava/lang/Class;)V";

    /**
     * Constructs a new JHCRLoadRewriter.
     * 
     * @param mv
     */
    public JHCRLoadRewriter (AnalyzerAdapter mv)
    {
        super(Opcodes.ASM9, mv);
    }

    @Override
    public void visitCode ()
    {
        super.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, CUSTOM_REPO_NAME, CUSTOM_REPO_GET_NAME, CUSTOM_REPO_GET_DESC, false);
        mv.visitInsn(Opcodes.DUP);

        Label ifNull = new Label();
        mv.visitJumpInsn(Opcodes.IFNULL, ifNull);
        mv.visitInsn(Opcodes.ARETURN);
        
        mv.visitLabel(ifNull);
        mv.visitInsn(Opcodes.POP);
    }

    @Override
    public void visitInsn (int opcode)
    {
        if (opcode == Opcodes.ARETURN)
        {
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.SWAP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, CUSTOM_REPO_NAME, CUSTOM_REPO_PUT_NAME, CUSTOM_REPO_PUT_DESC, false);
        }

        super.visitInsn(opcode);
    }
}

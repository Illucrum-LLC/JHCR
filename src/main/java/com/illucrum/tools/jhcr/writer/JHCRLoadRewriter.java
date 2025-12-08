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

import com.illucrum.tools.jhcr.vars.JHCRVariables;

/**
 * Method visitor responsible for bytecode manipulation in the {@link java.lang.ClassLoader#loadClass(String, boolean)} methods of custom class loaders.
 * 
 * @author Szymon Kokot
 */
public class JHCRLoadRewriter extends MethodVisitor implements JHCRVariables
{
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

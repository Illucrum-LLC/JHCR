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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

/**
 * This method visitor is responsible for setting proper super name for overriden classes
 * 
 * @author Szymon Kokot
 */
public class JHCRTypeRewriter extends MethodVisitor
{
    private final String superName;

    /**
     * Constructs a new {@link com.illucrum.tools.jhcr.writer.JHCRTypeRewriter}
     * 
     * @param mv Analyzer adapter to use
     * @param superName Correct superName
     */
    public JHCRTypeRewriter (AnalyzerAdapter mv, String superName)
    {
        super(Opcodes.ASM9, mv);
        this.superName = superName;
    }

    @Override
    public void visitMethodInsn (int opcode, String owner, String name, String desc, boolean itf)
    {
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>"))
        {
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, this.superName, "<init>", "()V", false);
        }
        else
        {
            mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}

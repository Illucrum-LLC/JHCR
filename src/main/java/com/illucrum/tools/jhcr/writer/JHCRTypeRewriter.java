package com.illucrum.tools.jhcr.writer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

public class JHCRTypeRewriter extends MethodVisitor
{
    private final String superName;

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
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, this.superName, "<init>", "()V", false);
        }
        else
        {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}

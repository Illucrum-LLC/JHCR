package com.illucrum.tools.jhcr.writer;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

public class JHCRLoadRewriter extends MethodVisitor
{
    private static final String CUSTOM_REPO_NAME = "com.illucrum.tools.jhcr.repo.JHCRCustomRepository";
    private static final String CUSTOM_REPO_GET_NAME = "get";
    private static final String CUSTOM_REPO_GET_DESC = "(Ljava.lang.ClassLoader;Ljava.lang.String;)Ljava.lang.Class;";
    private static final String CUSTOM_REPO_PUT_NAME = "put";
    private static final String CUSTOM_REPO_PUT_DESC = "(Ljava.lang.ClassLoader;Ljava.lang.String;Ljava.lang.Class;)V";

    public JHCRLoadRewriter (AnalyzerAdapter mv)
    {
        super(Opcodes.ASM9, mv);
    }

    @Override
    public void visitCode ()
    {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv
                .visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        CUSTOM_REPO_NAME,
                        CUSTOM_REPO_GET_NAME,
                        CUSTOM_REPO_GET_DESC,
                        false);
        Label ifNotNull = new Label();
        mv.visitJumpInsn(Opcodes.IFNONNULL, ifNotNull);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv
                .visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        CUSTOM_REPO_NAME,
                        CUSTOM_REPO_GET_NAME,
                        CUSTOM_REPO_GET_DESC,
                        false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitLabel(ifNotNull);

        super.visitCode();
    }

    @Override
    public void visitInsn (int opcode)
    {
        if (opcode == Opcodes.ARETURN)
        {
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.SWAP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.SWAP);

            mv
                    .visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            CUSTOM_REPO_NAME,
                            CUSTOM_REPO_PUT_NAME,
                            CUSTOM_REPO_PUT_DESC,
                            false);
            mv.visitInsn(Opcodes.ARETURN);
        }

        super.visitInsn(opcode);
    }
}

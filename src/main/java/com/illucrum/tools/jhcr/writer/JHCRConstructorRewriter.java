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
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;

import com.illucrum.tools.jhcr.logger.JHCRLogger;

/**
 * Method visitor for JHCR. It adds code to the {@link java.lang.ClassLoader#loadClass(String, boolean)} and
 * {@link java.net.URLClassLoader#loadClass(String, boolean)} methods.
 * 
 * At the beggining the equivalent of:
 * <code>
 * if(JHCRRepository.get(this, name) != null)
 *  return JHCRRepository.get(this, name);
 * </code>
 * 
 * At the end the equivalent of:
 * <code>
 * JHCRRepository.put(this, clazz);
 * return clazz;
 * </code>
 * Where clazz is the class to be returned.
 * 
 * @author Szymon Kokot
 */
class JHCRConstructorRewriter extends MethodVisitor
{
    private final boolean constructor;
    private int currentLine = -1;
    private boolean newHanging = false;

    public JHCRConstructorRewriter (AnalyzerAdapter mv, String methodName)
    {
        super(Opcodes.ASM9, mv);
        this.constructor = methodName.equals("<init>");
    }

    @Override
    public void visitLineNumber (int line, Label start)
    {
        this.currentLine = line;
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitTypeInsn (int opcode, String type)
    {
        JHCRLogger.finer("Method type insn: " + opcode + " " + type);
        if (opcode == Opcodes.NEW && !constructor && !type.equals("java/lang/Object"))
        {
            JHCRLogger.finer("NEW opcode, not in constructor, not Object");
            this.newHanging = true;
            return;
        }

        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitInsn (int opcode)
    {
        JHCRLogger.finer("Method insn: " + opcode);
        if (opcode == Opcodes.DUP && !constructor && this.newHanging)
        {
            JHCRLogger.finer("Skipping DUP");
            return;
        }

        super.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn (int opcode, String owner, String name, String desc, boolean itf)
    {
        JHCRLogger.finer("Method visit: " + owner + " " + name + " " + desc);
        if (!constructor && opcode == Opcodes.INVOKESPECIAL && name.equals("<init>") && this.newHanging)
        {
            JHCRLogger.finer("Rewriting constructor...");
            this.newHanging = false;
            this.rewriteConstructor(owner, desc);
            return;
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void rewriteConstructor (String owner, String desc)
    {
        if (this.currentLine != -1)
        {
            Label synthetic = new Label();
            super.visitLabel(synthetic);
            super.visitLineNumber(this.currentLine, synthetic);
        }

        JHCRLogger.finest("owner: " + owner + "; desc: " + desc);
        mv.visitLdcInsn(owner.replace('/', '.'));

        Type[] argTypes = Type.getArgumentTypes(desc);

        this.pushInt(mv, argTypes.length);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class");

        for (int i = 0; i < argTypes.length; i++)
        {
            mv.visitInsn(Opcodes.DUP);
            this.pushInt(mv, i);
            this.pushClassLiteral(mv, argTypes[i]);
            mv.visitInsn(Opcodes.AASTORE);
        }

        this.pushInt(mv, argTypes.length);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");

        for (int i = 0; i < argTypes.length; i++)
        {
            mv.visitInsn(Opcodes.DUP);
            this.pushInt(mv, i);
            this.box(mv, argTypes[i]);
            mv.visitInsn(Opcodes.AASTORE);
        }

        mv
                .visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "com/illucrum/tools/jhcr/loader/JHCRConstructor",
                        "construct",
                        "(Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;",
                        false);

        mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
    }

    private void pushInt (MethodVisitor mv, int i)
    {
        if (i >= -1 && i <= 5)
        {
            mv.visitInsn(Opcodes.ICONST_0 + i);
        }
        else if (i <= 127)
        {
            mv.visitIntInsn(Opcodes.BIPUSH, i);
        }
        else
        {
            mv.visitLdcInsn(i);
        }
    }

    private void pushClassLiteral (MethodVisitor mv, Type t)
    {
        switch (t.getSort())
        {
            case Type.BOOLEAN:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.BYTE:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.CHAR:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.DOUBLE:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.FLOAT:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.INT:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.LONG:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
                break;
            case Type.SHORT:
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
                break;
        }
    }

    private void box (MethodVisitor mv, Type t)
    {
        if (t.getSort() == Type.OBJECT || t.getSort() == Type.ARRAY)
        {
            return;
        }

        Type boxType = this.getBoxedType(t);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, boxType.getInternalName(), "valueOf", "(" + t.getDescriptor() + ")" + boxType.getDescriptor(), false);
    }

    private Type getBoxedType (Type t)
    {
        switch (t.getSort())
        {
            case Type.BOOLEAN:
                return Type.getType(Boolean.class);
            case Type.BYTE:
                return Type.getType(Byte.class);
            case Type.CHAR:
                return Type.getType(Character.class);
            case Type.DOUBLE:
                return Type.getType(Double.class);
            case Type.FLOAT:
                return Type.getType(Float.class);
            case Type.INT:
                return Type.getType(Integer.class);
            case Type.LONG:
                return Type.getType(Long.class);
            case Type.SHORT:
                return Type.getType(Short.class);
        }

        throw new IllegalArgumentException("Unsuported primitive: " + t);
    }
}

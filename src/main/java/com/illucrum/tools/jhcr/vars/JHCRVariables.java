package com.illucrum.tools.jhcr.vars;

import org.objectweb.asm.Opcodes;

public interface JHCRVariables {
    public final static int API = Opcodes.ASM9;
    public final static String LOADER_NAME = "java/lang/ClassLoader";
    public final static String URL_LOADER_NAME = "java/net/URLClassLoader";
    public final static String CUSTOM_LOADER_NAME = "com/illucrum/tools/jhcr/loader/JHCRCustomLoader";
    public final static String LOAD_NAME = "loadClass";
    public final static String LOAD_DESC = "(Ljava/lang/String;Z)Ljava/lang/Class;";
    public final static String DEFINE_WRAPPER_NAME = "defineClassWrapper";
    public final static String DEFINE_WRAPPER_DESC = "(Ljava/lang/String;[BII)Ljava/lang/Class;";
    
    public final static String CLASS_NAME = "java/lang/Class";
    public final static String CLASS_DESC = "Ljava/lang/Class;";
    public final static String OBJECT_NAME = "java/lang/Object";
    public final static String CONSTRUCTOR_NAME = "com/illucrum/tools/jhcr/loader/JHCRConstructor";
    public final static String CONSTRUCT_NAME = "construct";
    public final static String CONSTRUCT_DESC = "(Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;";
    
    public final static String CUSTOM_REPO_NAME = "com/illucrum/tools/jhcr/repo/JHCRCustomRepository";
    public final static String CUSTOM_REPO_GET_NAME = "get";
    public final static String CUSTOM_REPO_GET_DESC = "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;";
    public final static String CUSTOM_REPO_PUT_NAME = "put";
    public final static String CUSTOM_REPO_PUT_DESC = "(Ljava/lang/String;Ljava/lang/Class;)V";
    
    public final static String FILE_EXTENSION = ".class";
    
    public final static String PROTOCOL = "file://";
}

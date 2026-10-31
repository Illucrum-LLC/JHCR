package com.illucrum.tools.jhcr.vars;

import org.objectweb.asm.Opcodes;

/**
 * Interface holding basic constants
 */
public interface JHCRVariables
{
  /** ASM version used */
  public final static int API = Opcodes.ASM9;
  /** {@link java.lang.ClassLoader} qualified name */
  public final static String LOADER_NAME = "java/lang/ClassLoader";
  /** {@link java.net.URLClassLoader} qualified name */
  public final static String URL_LOADER_NAME = "java/net/URLClassLoader";
  /** {@link com.illucrum.tools.jhcr.loader.JHCRCustomLoader} qualified name */
  public final static String CUSTOM_LOADER_NAME = "com/illucrum/tools/jhcr/loader/JHCRCustomLoader";
  /** {@link java.lang.ClassLoader#loadClass(String, boolean)} method name */
  public final static String LOAD_NAME = "loadClass";
  /** {@link java.lang.ClassLoader#loadClass(String, boolean)} method description */
  public final static String LOAD_DESC = "(Ljava/lang/String;Z)Ljava/lang/Class;";
  /** {@link com.illucrum.tools.jhcr.loader.JHCRCustomLoader#defineClassWrapper(String, byte[], int, int)} method name */
  public final static String DEFINE_WRAPPER_NAME = "defineClassWrapper";
  /** {@link com.illucrum.tools.jhcr.loader.JHCRCustomLoader#defineClassWrapper(String, byte[], int, int)} method name */
  public final static String DEFINE_WRAPPER_DESC = "(Ljava/lang/String;[BII)Ljava/lang/Class;";

  /** {@link java.lang.Class} qualified name */
  public final static String CLASS_NAME = "java/lang/Class";
  /** {@link java.lang.Class} class description */
  public final static String CLASS_DESC = "Ljava/lang/Class;";
  /** {@link java.lang.Object} qualified name */
  public final static String OBJECT_NAME = "java/lang/Object";
  /** {@link com.illucrum.tools.jhcr.loader.JHCRConstructor} qualified name */
  public final static String CONSTRUCTOR_NAME = "com/illucrum/tools/jhcr/loader/JHCRConstructor";
  /** {@link com.illucrum.tools.jhcr.loader.JHCRConstructor#construct(String, Class[], Object[])} method name */
  public final static String CONSTRUCT_NAME = "construct";
  /** {@link com.illucrum.tools.jhcr.loader.JHCRConstructor#construct(String, Class[], Object[])} method description */
  public final static String CONSTRUCT_DESC = "(Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;";

  /** {@link com.illucrum.tools.jhcr.repo.JHCRCustomRepository} qualified name */
  public final static String CUSTOM_REPO_NAME = "com/illucrum/tools/jhcr/repo/JHCRCustomRepository";
  /** {@link com.illucrum.tools.jhcr.repo.JHCRCustomRepository#get(ClassLoader, String)} method name */
  public final static String CUSTOM_REPO_GET_NAME = "get";
  /** {@link com.illucrum.tools.jhcr.repo.JHCRCustomRepository#get(ClassLoader, String)} method description */
  public final static String CUSTOM_REPO_GET_DESC = "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;";
  /** {@link com.illucrum.tools.jhcr.repo.JHCRCustomRepository#put(String, Class)} method name */
  public final static String CUSTOM_REPO_PUT_NAME = "put";
  /** {@link com.illucrum.tools.jhcr.repo.JHCRCustomRepository#put(String, Class)} method description */
  public final static String CUSTOM_REPO_PUT_DESC = "(Ljava/lang/String;Ljava/lang/Class;)V";

  /** Class file extension */
  public final static String FILE_EXTENSION = ".class";

  /** file protocol */
  public final static String PROTOCOL = "file://";
}

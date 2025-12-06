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
package com.illucrum.tools.jhcr.loader;

/**
 * This interface should be implemented by any custom class loaders. If a class loader does not, the transformer should take care of it.
 * 
 * <p>This is so it is possible to define new classes calling directly the {@link java.lang.ClassLoader#defineClass(String, byte[], int, int)}</p>
 * 
 * @author Szymon Kokot
 */
public interface JHCRCustomLoader
{
    /**
     * Just a wrapper for the {@link java.lang.ClassLoader#defineClass(String, byte[], int, int)}
     * 
     * @param name
     *            Name of the class to be defined.
     * @param bytecode
     *            Bytecode of the class to be defined
     * @param off
     *            The start offset of the class data
     * @param end
     *            Length of the class data
     * @return the class returned by {@link java.lang.ClassLoader#defineClass(String, byte[], int, int)}
     */
    Class<?> defineClassWrapper (String name, byte[] bytecode, int off, int len);
}

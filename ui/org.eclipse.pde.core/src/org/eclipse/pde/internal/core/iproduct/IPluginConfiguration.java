/*******************************************************************************
 * Copyright (c) 2008, 2009 Code 9 Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Code 9 Corporation - initial API and implementation
 *     Bartosz Michalik <bartosz.michalik@gmail.com>
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - bug 265931
 *******************************************************************************/
package org.eclipse.pde.internal.core.iproduct;

/**
 * PluginConfiguration description defines plug-in start level, and other properties
 * that can be used during product launching/building
 */
public interface IPluginConfiguration extends IProductObject {
	public static final String P_AUTO_START = "autoStart"; //$NON-NLS-1$
	public static final String P_START_LEVEL = "startLevel"; //$NON-NLS-1$

	String getId();

	void setId(String id);

	void setAutoStart(boolean autostart);

	boolean isAutoStart();

	void setStartLevel(int startLevel);

	int getStartLevel();

}

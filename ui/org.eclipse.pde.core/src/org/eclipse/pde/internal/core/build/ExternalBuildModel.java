/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.build;

import java.io.*;
import java.net.*;

import org.eclipse.core.runtime.*;

public class ExternalBuildModel extends BuildModel {
	private String fInstallLocation;

	public ExternalBuildModel(String installLocation) {
		fInstallLocation = installLocation;
	}

	public String getInstallLocation() {
		return fInstallLocation;
	}

	public boolean isEditable() {
		return false;
	}

	public void load() {
		try {
			URL url = null;
			File file = new File(getInstallLocation());
			if (file.isFile() && file.getName().endsWith(".jar")) {
				url = new URL("jar:file:" + file.getAbsolutePath() + "!/build.properties"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				url = new URL("file:" + file.getAbsolutePath() + Path.SEPARATOR + "build.properties");
			}
			InputStream stream = url.openStream();
			load(stream, false);
			stream.close();
		} catch (IOException e) {
			fBuild = new Build();
			fBuild.setModel(this);
			loaded = true;
		}
	}

	protected void updateTimeStamp() {
		updateTimeStamp(getLocalFile());
	}

	private File getLocalFile() {
		File file = new File(getInstallLocation());
		return (file.isFile()) ? file : new File(file, "build.properties");		
	}

	public boolean isInSync() {
		return true;
	}
}

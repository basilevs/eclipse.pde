package org.eclipse.pde.internal.ui.editor;

import java.io.*;
import java.util.zip.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.internal.ui.*;


public class JarEntryFile extends PlatformObject implements IStorage {
	
	private ZipFile fZipFile;
	private String fEntryName;

	public JarEntryFile(ZipFile zipFile, String entryName) {
		fZipFile = zipFile;
		fEntryName = entryName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		try {
			ZipEntry zipEntry = fZipFile.getEntry(fEntryName);
			return fZipFile.getInputStream(zipEntry);
		} catch (Exception e){
			throw new CoreException(new Status(IStatus.ERROR, PDEPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		return new Path(fEntryName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		return getFullPath().lastSegment();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(ZipFile.class))
			return fZipFile;
		return super.getAdapter(adapter);
	}

}

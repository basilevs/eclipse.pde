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
package org.eclipse.pde.internal.ui.model;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;
import org.eclipse.pde.core.*;
import org.eclipse.pde.internal.core.*;

/**
 * @author melhem
 *
 */
public abstract class AbstractEditingModel extends PlatformObject implements IEditingModel, IModelChangeProviderExtension {
	private ArrayList fListeners = new ArrayList();
	protected boolean fReconciling;
	protected boolean fInSync = true;
	protected boolean fLoaded = false;
	protected boolean fDisposed;
	protected long fTimestamp;
	private transient NLResourceHelper fNLResourceHelper;
	private IDocument fDocument;
	private boolean fDirty;
	private String fCharset;
	private IResource fUnderlyingResource;
	private String fInstallLocation;
	private boolean fStale;
	
	public AbstractEditingModel(IDocument document, boolean isReconciling) {
		fDocument = document;
		fReconciling = isReconciling;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#dispose()
	 */
	public void dispose() {
		if (fNLResourceHelper != null) {
			fNLResourceHelper.dispose();
			fNLResourceHelper = null;
		}
		fDisposed = true;
		fListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#getResourceString(java.lang.String)
	 */
	public String getResourceString(String key) {
		if (key == null || key.length() == 0)
			return ""; //$NON-NLS-1$
		
		if (fNLResourceHelper == null) 
			fNLResourceHelper = createNLResourceHelper();
		
		return (fNLResourceHelper == null) ? key : fNLResourceHelper.getResourceString(key);
	}

	protected abstract NLResourceHelper createNLResourceHelper();

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isDisposed()
	 */
	public boolean isDisposed() {
		return fDisposed;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isEditable()
	 */
	public boolean isEditable() {
		return fReconciling;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isLoaded()
	 */
	public boolean isLoaded() {
		return fLoaded;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isInSync()
	 */
	public boolean isInSync() {
		return fInSync;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isValid()
	 */
	public boolean isValid() {
		return isLoaded();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#getTimeStamp()
	 */
	public final long getTimeStamp() {
		return fTimestamp;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#load()
	 */
	public final void load() throws CoreException {
		try {
			load(getInputStream(getDocument()), false);
		} catch (UnsupportedEncodingException e) {
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#reload(java.io.InputStream, boolean)
	 */
	public final void reload(InputStream source, boolean outOfSync)
			throws CoreException {
		load(source, outOfSync);
		fireModelChanged(
				new ModelChangedEvent(this, 
					IModelChangedEvent.WORLD_CHANGED,
					new Object[] {this},
					null));
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isReconcilingModel()
	 */
	public boolean isReconcilingModel() {
		return fReconciling;
	}
	
	public IDocument getDocument() {
		return fDocument;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.neweditor.text.IReconcilingParticipant#reconciled(org.eclipse.jface.text.IDocument)
	 */
	public final void reconciled(IDocument document) {
		if (isReconcilingModel()) {
			try {
				if (isStale()) {
					adjustOffsets(document);
					setStale(false);
				} else {
					reload(getInputStream(document), false);
				}
				if (isDirty())
					setDirty(false);
			} catch (Exception e) {
			} 	
		}
	}
	
	protected abstract void adjustOffsets(IDocument document);
	
	protected InputStream getInputStream(IDocument document) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(document.get().getBytes(getCharset()));
	}
	
	public String getCharset() {
		return fCharset != null ? fCharset : "UTF-8";
	}
	
	public void setCharset(String charset) {
		fCharset = charset;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#addModelChangedListener(org.eclipse.pde.core.IModelChangedListener)
	 */
	public void addModelChangedListener(IModelChangedListener listener) {
		if (!fListeners.contains(listener))
			fListeners.add(listener);
	}
	public void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter) {
		List oldList = (List)fListeners.clone();
		for (int i=0; i<oldList.size(); i++) {
			IModelChangedListener listener = (IModelChangedListener)oldList.get(i);
			if (filter==null || filter.accept(listener)) {
				// add the listener to the target
				target.addModelChangedListener(listener);
				// remove the listener from our list
				fListeners.remove(listener);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#fireModelChanged(org.eclipse.pde.core.IModelChangedEvent)
	 */
	public void fireModelChanged(IModelChangedEvent event) {
		setDirty(event.getChangeType() != IModelChangedEvent.WORLD_CHANGED);
		for (int i = 0; i < fListeners.size(); i++) {
			((IModelChangedListener)fListeners.get(i)).modelChanged(event);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#fireModelObjectChanged(java.lang.Object, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
		fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#removeModelChangedListener(org.eclipse.pde.core.IModelChangedListener)
	 */
	public void removeModelChangedListener(IModelChangedListener listener) {
		fListeners.remove(listener);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IEditable#isDirty()
	 */
	public boolean isDirty() {
		return fDirty;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IEditable#save(java.io.PrintWriter)
	 */
	public void save(PrintWriter writer) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IEditable#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		this.fDirty = dirty;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.model.IEditingModel#isStale()
	 */
	public boolean isStale() {
		return fStale;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.model.IEditingModel#setStale(boolean)
	 */
	public void setStale(boolean stale) {
		fStale = stale;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#getUnderlyingResource()
	 */
	public IResource getUnderlyingResource() {
		return fUnderlyingResource;
	}
	
	public void setUnderlyingResource(IResource resource) {
		fUnderlyingResource = resource;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.plugin.ISharedPluginModel#getInstallLocation()
	 */
	public String getInstallLocation() {
		if (fUnderlyingResource != null)
			return fUnderlyingResource.getProject().getLocation().addTrailingSeparator().toString();
		return fInstallLocation;
	}
	
	public URL getResourceURL(String relativePath) throws MalformedURLException {
		File file = new File(getInstallLocation());
		URL url = null;
		if (file.isFile() && file.getName().endsWith(".jar")) {
			url = new URL("jar:file:" + file.getAbsolutePath() + "!/" + relativePath); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			url = new URL("file:" + file.getAbsolutePath() + Path.SEPARATOR + relativePath);
		}
		return url;
	}
	
	public void setInstallLocation(String location) {
		fInstallLocation = location;
	}


}

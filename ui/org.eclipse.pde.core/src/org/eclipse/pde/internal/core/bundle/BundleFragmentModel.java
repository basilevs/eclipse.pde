/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.bundle;

import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.ibundle.*;

public class BundleFragmentModel
	extends BundlePluginModelBase
	implements IBundleFragmentModel {
	
	public IPluginBase createPluginBase() {
		BundleFragment bfragment = new BundleFragment();
		bfragment.setModel(this);
		return bfragment;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.plugin.IFragmentModel#getFragment()
	 */
	public IFragment getFragment() {
		return (IFragment)getPluginBase();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.plugin.IPluginModelBase#isFragmentModel()
	 */
	public boolean isFragmentModel() {
		return true;
	}
}

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
package org.eclipse.pde.internal.ui.wizards.templates;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.*;

public class PreferencePageTemplate extends PDETemplateSection {
	private static final String NL_TITLE = "PreferencePageTemplate.title"; //$NON-NLS-1$
	private static final String NL_DESC = "PreferencePageTemplate.desc"; //$NON-NLS-1$
	private static final String NL_PACKAGE_NAME =
		"PreferencePageTemplate.packageName"; //$NON-NLS-1$
	private static final String NL_CLASS_NAME = "PreferencePageTemplate.className"; //$NON-NLS-1$
	private static final String NL_PAGE_NAME = "PreferencePageTemplate.pageName"; //$NON-NLS-1$
	private static final String NL_DEFAULT_PAGE_NAME =
		"PreferencePageTemplate.defaultPageName"; //$NON-NLS-1$
	private String mainClassName;
	
	public PreferencePageTemplate() {
		setPageCount(1);
		createOptions();
	}

	public String getSectionId() {
		return "preferences"; //$NON-NLS-1$
	}
	/*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	private void createOptions() {
		// first page
		addOption(
			KEY_PACKAGE_NAME,
			PDEPlugin.getResourceString(NL_PACKAGE_NAME),
			(String) null,
			0);
		addOption(
			"pageClassName", //$NON-NLS-1$
			PDEPlugin.getResourceString(NL_CLASS_NAME),
			"SamplePreferencePage", //$NON-NLS-1$
			0);
		addOption(
			"pageName", //$NON-NLS-1$
			PDEPlugin.getResourceString(NL_PAGE_NAME),
			PDEPlugin.getResourceString(NL_DEFAULT_PAGE_NAME),
			0);
	}

	protected void initializeFields(IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		String id = data.getId();
		initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(id)); 
		mainClassName = id + ".PreferenceClass"; //$NON-NLS-1$
	}
	public void initializeFields(IPluginModelBase model) {
		// In the new extension wizard, the model exists so 
		// we can initialize directly from it
		String pluginId = model.getPluginBase().getId();
		initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(pluginId)); 
		if (model instanceof IPluginModel) {
			IPlugin plugin = (IPlugin) model.getPluginBase();
			mainClassName = plugin.getClassName();
		}
	}

	public String getReplacementString(String fileName, String key) {
		if (key.equals("fullPluginClassName")) //$NON-NLS-1$
			return mainClassName;
		if (key.equals("pluginClassName")) //$NON-NLS-1$
			return getPluginClassName();
		return super.getReplacementString(fileName, key);
	}

	private String getPluginClassName() {
		int dot = mainClassName.lastIndexOf('.');
		if (dot != -1) {
			return mainClassName.substring(dot + 1);
		}
		return mainClassName;
	}

	public boolean isDependentOnParentWizard() {
		return true;
	}

	public void addDefaultOption(boolean val){
		addOption(
			"hasDefault", //$NON-NLS-1$
			null,
			val,
			0);
	}
	
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_PREFERENCE_PAGE);
		page.setTitle(PDEPlugin.getResourceString(NL_TITLE));
		page.setDescription(PDEPlugin.getResourceString(NL_DESC));
		wizard.addPage(page);
		markPagesAdded();
	}

	public void validateOptions(TemplateOption source) {
		if (source.isRequired() && source.isEmpty()) {
			flagMissingRequiredOption(source);
		} else
			resetPageState();
	}

	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.preferencePages"; //$NON-NLS-1$
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginExtension extension = createExtension(getUsedExtensionPoint(), true);
		IPluginModelFactory factory = model.getPluginFactory();

		String fullClassName =
			getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption("pageClassName"); //$NON-NLS-1$ //$NON-NLS-2$

		IPluginElement pageElement = factory.createElement(extension);
		pageElement.setName("page"); //$NON-NLS-1$
		pageElement.setAttribute("id", fullClassName); //$NON-NLS-1$
		pageElement.setAttribute("name", getStringOption("pageName")); //$NON-NLS-1$ //$NON-NLS-2$
		pageElement.setAttribute("class", fullClassName); //$NON-NLS-1$
		extension.add(pageElement);
		if (!extension.isInTheModel())
			plugin.add(extension);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.pde.internal.ui.wizards.templates.PDETemplateSection#formatPackageName(java.lang.String)
     */
    protected String getFormattedPackageName(String id) {
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            return packageName + ".preferences"; //$NON-NLS-1$
        return "preferences"; //$NON-NLS-1$
    }
}

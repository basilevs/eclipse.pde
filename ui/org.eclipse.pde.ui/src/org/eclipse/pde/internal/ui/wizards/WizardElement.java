/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.internal.ui.wizards;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.internal.ui.elements.NamedElement;
import org.eclipse.swt.graphics.Image;
public class WizardElement extends NamedElement {
	public static final String ATT_NAME = "name"; //$NON-NLS-1$
	public static final String TAG_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ATT_ICON = "icon"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String ATT_CLASS = "class"; //$NON-NLS-1$
	public static final String ATT_TEMPLATE = "template"; //$NON-NLS-1$
	public static final String ATT_POINT = "point"; //$NON-NLS-1$
	private String description;
	private IConfigurationElement configurationElement;
	private IConfigurationElement template;
	
	public WizardElement(IConfigurationElement config) {
		super(config.getAttribute(ATT_NAME));
		this.configurationElement = config;
	}
	public Object createExecutableExtension() throws CoreException {
		return configurationElement.createExecutableExtension(ATT_CLASS);
	}
	public IConfigurationElement getConfigurationElement() {
		return configurationElement;
	}
	public String getDescription() {
		if (description == null) {
			IConfigurationElement[] children = configurationElement
					.getChildren(TAG_DESCRIPTION);
			if (children.length > 0) {
				description = expandDescription(children[0].getValue());
			}
		}
		return description;
	}
	/**
	 * We allow replacement variables in description values as well. This is to
	 * allow extension template descriptin reuse in project template wizards.
	 * Tokens in form '%token%' will be evaluated against the contributing
	 * plug-in's resource bundle. As before, to have '%' in the description, one
	 * need to add '%%'.
	 */
	private String expandDescription(String source) {
		if (source == null || source.length() == 0)
			return source;
		if (source.indexOf('%') == -1)
			return source;
		ResourceBundle bundle = configurationElement.getDeclaringExtension()
				.getDeclaringPluginDescriptor().getResourceBundle();
		if (bundle == null)
			return source;
		StringBuffer buf = new StringBuffer();
		boolean keyMode = false;
		int keyStartIndex = -1;
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '%') {
				char c2 = source.charAt(i + 1);
				if (c2 == '%') {
					i++;
					buf.append('%');
					continue;
				} else {
					if (keyMode) {
						keyMode = false;
						String key = source.substring(keyStartIndex, i);
						String value = key;
						try {
							value = bundle.getString(key);
						} catch (MissingResourceException e) {
						}
						buf.append(value);
					} else {
						keyStartIndex = i + 1;
						keyMode = true;
					}
				}
			} else if (!keyMode) {
				buf.append(c);
			}
		}
		return buf.toString();
	}
	public String getID() {
		return configurationElement.getAttribute(ATT_ID);
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public String getTemplateId() {
		return configurationElement.getAttribute(ATT_TEMPLATE);
	}
	public boolean isTemplate() {
		return getTemplateId() != null;
	}
	public IConfigurationElement getTemplateElement() {
		if (template==null) template = findTemplateElement();
		return template;
	}
	private IConfigurationElement findTemplateElement() {
		String templateId = getTemplateId();
		if (templateId==null) return null;
		IConfigurationElement [] templates = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.pde.ui.templates"); //$NON-NLS-1$
		for (int i=0; i<templates.length; i++) {
			IConfigurationElement template = templates[i];
			String id = template.getAttribute("id"); //$NON-NLS-1$
			if (id!=null && id.equals(templateId))
				return template;
		}
		return null;
	}
	public String getContributingId() {
		IConfigurationElement tel = getTemplateElement();
		if (tel==null) return null;
		return tel.getAttribute("contributingId"); //$NON-NLS-1$
	}
}
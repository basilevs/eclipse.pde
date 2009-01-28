/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.launcher;

import java.util.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.build.IPDEBuildConstants;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.ui.IPDEUIConstants;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.swt.widgets.Display;

public class LaunchPluginValidator {

	public static void checkBackwardCompatibility(ILaunchConfiguration configuration, boolean save) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = null;
		if (configuration.isWorkingCopy()) {
			wc = (ILaunchConfigurationWorkingCopy) configuration;
		} else {
			wc = configuration.getWorkingCopy();
		}

		String value = configuration.getAttribute("wsproject", (String) null); //$NON-NLS-1$
		if (value != null) {
			wc.setAttribute("wsproject", (String) null); //$NON-NLS-1$
			if (value.indexOf(';') != -1) {
				value = value.replace(';', ',');
			} else if (value.indexOf(':') != -1) {
				value = value.replace(':', ',');
			}
			value = (value.length() == 0 || value.equals(",")) //$NON-NLS-1$
			? null
					: value.substring(0, value.length() - 1);

			boolean automatic = configuration.getAttribute(IPDELauncherConstants.AUTOMATIC_ADD, true);
			String attr = automatic ? IPDELauncherConstants.DESELECTED_WORKSPACE_PLUGINS : IPDELauncherConstants.SELECTED_WORKSPACE_PLUGINS;
			wc.setAttribute(attr, value);
		}

		String value2 = configuration.getAttribute("extplugins", (String) null); //$NON-NLS-1$
		if (value2 != null) {
			wc.setAttribute("extplugins", (String) null); //$NON-NLS-1$
			if (value2.indexOf(';') != -1) {
				value2 = value2.replace(';', ',');
			} else if (value2.indexOf(':') != -1) {
				value2 = value2.replace(':', ',');
			}
			value2 = (value2.length() == 0 || value2.equals(",")) ? null : value2.substring(0, value2.length() - 1); //$NON-NLS-1$
			wc.setAttribute(IPDELauncherConstants.SELECTED_TARGET_PLUGINS, value2);
		}

		String version = configuration.getAttribute(IPDEUIConstants.LAUNCHER_PDE_VERSION, (String) null);
		boolean newApp = TargetPlatformHelper.usesNewApplicationModel();
		boolean upgrade = !"3.3".equals(version) && newApp; //$NON-NLS-1$
		if (!upgrade)
			upgrade = TargetPlatformHelper.getTargetVersion() >= 3.2 && version == null;
		if (upgrade) {
			wc.setAttribute(IPDEUIConstants.LAUNCHER_PDE_VERSION, newApp ? "3.3" : "3.2a"); //$NON-NLS-1$ //$NON-NLS-2$
			boolean usedefault = configuration.getAttribute(IPDELauncherConstants.USE_DEFAULT, true);
			boolean useFeatures = configuration.getAttribute(IPDELauncherConstants.USEFEATURES, false);
			boolean automaticAdd = configuration.getAttribute(IPDELauncherConstants.AUTOMATIC_ADD, true);
			if (!usedefault && !useFeatures) {
				ArrayList list = new ArrayList();
				if (version == null) {
					list.add("org.eclipse.core.contenttype"); //$NON-NLS-1$
					list.add("org.eclipse.core.jobs"); //$NON-NLS-1$
					list.add(IPDEBuildConstants.BUNDLE_EQUINOX_COMMON);
					list.add("org.eclipse.equinox.preferences"); //$NON-NLS-1$
					list.add("org.eclipse.equinox.registry"); //$NON-NLS-1$
					list.add("org.eclipse.core.runtime.compatibility.registry"); //$NON-NLS-1$
				}
				if (!"3.3".equals(version) && newApp) //$NON-NLS-1$
					list.add("org.eclipse.equinox.app"); //$NON-NLS-1$
				StringBuffer extensions = new StringBuffer(configuration.getAttribute(IPDELauncherConstants.SELECTED_WORKSPACE_PLUGINS, "")); //$NON-NLS-1$
				StringBuffer target = new StringBuffer(configuration.getAttribute(IPDELauncherConstants.SELECTED_TARGET_PLUGINS, "")); //$NON-NLS-1$
				for (int i = 0; i < list.size(); i++) {
					String plugin = list.get(i).toString();
					IPluginModelBase model = PluginRegistry.findModel(plugin);
					if (model == null)
						continue;
					if (model.getUnderlyingResource() != null) {
						if (automaticAdd)
							continue;
						if (extensions.length() > 0)
							extensions.append(","); //$NON-NLS-1$
						extensions.append(plugin);
					} else {
						if (target.length() > 0)
							target.append(","); //$NON-NLS-1$
						target.append(plugin);
					}
				}
				if (extensions.length() > 0)
					wc.setAttribute(IPDELauncherConstants.SELECTED_WORKSPACE_PLUGINS, extensions.toString());
				if (target.length() > 0)
					wc.setAttribute(IPDELauncherConstants.SELECTED_TARGET_PLUGINS, target.toString());
			}
		}

		if (save && (value != null || value2 != null || upgrade))
			wc.doSave();
	}

	private static void addToMap(Map map, IPluginModelBase[] models) {
		for (int i = 0; i < models.length; i++) {
			addToMap(map, models[i]);
		}
	}

	private static void addToMap(Map map, IPluginModelBase model) {
		BundleDescription desc = model.getBundleDescription();
		if (desc != null) {
			String id = desc.getSymbolicName();
			// the reason that we are using a map is to easily check
			// if a plug-in with a certain id is among the plug-ins we are launching with.
			// Therefore, now that we support multiple plug-ins by the same ID,
			// once a particular ID is used up as a key, the rest can be entered
			// with key == id_version, for easy retrieval of values later on,
			// and without the need to create complicated data structures for values.
			if (!map.containsKey(id)) {
				map.put(id, model);
			} else {
				// since other code grabs only the model matching the "id", we want to make
				// sure the model matching the "id" has the highest version (because for singletons
				// the runtime will only resolve the highest version).  Bug 218393
				IPluginModelBase oldModel = (IPluginModelBase) map.get(id);
				String oldVersion = oldModel.getPluginBase().getVersion();
				String newVersion = model.getPluginBase().getVersion();
				if (oldVersion.compareTo(newVersion) < 0) {
					map.put(id + "_" + oldModel.getBundleDescription().getBundleId(), oldModel); //$NON-NLS-1$
					map.put(id, model);
				} else {
					map.put(id + "_" + desc.getBundleId(), model); //$NON-NLS-1$
				}
			}
		}
	}

	private static IPluginModelBase[] getSelectedWorkspacePlugins(ILaunchConfiguration configuration) throws CoreException {

		boolean usedefault = configuration.getAttribute(IPDELauncherConstants.USE_DEFAULT, true);
		boolean useFeatures = configuration.getAttribute(IPDELauncherConstants.USEFEATURES, false);

		IPluginModelBase[] models = PluginRegistry.getWorkspaceModels();

		if (usedefault || useFeatures || models.length == 0)
			return models;

		Collection result = null;
		Map bundles = BundleLauncherHelper.getWorkspaceBundleMap(configuration, null, IPDELauncherConstants.SELECTED_WORKSPACE_PLUGINS);
		result = bundles.keySet();
		return (IPluginModelBase[]) result.toArray(new IPluginModelBase[result.size()]);
	}

	/**
	 * 
	 * @param configuration launchConfiguration to get the attribute value
	 * @param attribute launch configuration attribute to containing plug-in information
	 * @return a TreeSet containing IPluginModelBase objects which are represented by the value of the attribute
	 * @throws CoreException
	 */
	public static Set parsePlugins(ILaunchConfiguration configuration, String attribute) throws CoreException {
		HashSet set = new HashSet();
		String ids = configuration.getAttribute(attribute, (String) null);
		if (ids != null) {
			String[] entries = ids.split(","); //$NON-NLS-1$
			Map unmatchedEntries = new HashMap();
			for (int i = 0; i < entries.length; i++) {
				int index = entries[i].indexOf('@');
				if (index < 0) { // if no start levels, assume default
					entries[i] = entries[i].concat("@default:default"); //$NON-NLS-1$
					index = entries[i].indexOf('@');
				}
				String idVersion = entries[i].substring(0, index);
				int versionIndex = entries[i].indexOf(BundleLauncherHelper.VERSION_SEPARATOR);
				String id = (versionIndex > 0) ? idVersion.substring(0, versionIndex) : idVersion;
				String version = (versionIndex > 0) ? idVersion.substring(versionIndex + 1) : null;
				ModelEntry entry = PluginRegistry.findEntry(id);
				if (entry != null) {
					IPluginModelBase matchingModels[] = attribute.equals(IPDELauncherConstants.SELECTED_TARGET_PLUGINS) ? entry.getExternalModels() : entry.getWorkspaceModels();
					for (int j = 0; j < matchingModels.length; j++) {
						if (matchingModels[j].isEnabled()) {
							// TODO Very similar logic to BundleLauncherHelper
							// the logic here is this (see bug 225644)
							// a) if we come across a bundle that has the right version, immediately add it
							// b) if there's no version, add it
							// c) if there's only one instance of that bundle in the list of ids... add it
							if (version == null || matchingModels[j].getPluginBase().getVersion().equals(version)) {
								set.add(matchingModels[j]);
							} else if (matchingModels.length == 1) {
								if (unmatchedEntries.remove(id) == null) {
									unmatchedEntries.put(id, matchingModels[j]);
								}
							}
						}
					}
				}
			}
			set.addAll(unmatchedEntries.values());
		}
		return set;
	}

	public static IPluginModelBase[] getPluginList(ILaunchConfiguration config) throws CoreException {
		Map map = getPluginsToRun(config);
		return (IPluginModelBase[]) map.values().toArray(new IPluginModelBase[map.size()]);
	}

	public static Map getPluginsToRun(ILaunchConfiguration config) throws CoreException {

		checkBackwardCompatibility(config, true);

		TreeMap map = new TreeMap();
		if (config.getAttribute(IPDELauncherConstants.USE_DEFAULT, true)) {
			addToMap(map, PluginRegistry.getActiveModels());
			return map;
		}

		if (config.getAttribute(IPDELauncherConstants.USEFEATURES, false)) {
			addToMap(map, PluginRegistry.getWorkspaceModels());
			return map;
		}

		addToMap(map, getSelectedWorkspacePlugins(config));

		Map exModels = BundleLauncherHelper.getTargetBundleMap(config, null, IPDELauncherConstants.SELECTED_TARGET_PLUGINS);

		Iterator it = exModels.keySet().iterator();
		while (it.hasNext()) {
			IPluginModelBase model = (IPluginModelBase) it.next();
			String id = model.getPluginBase().getId();
			IPluginModelBase existing = (IPluginModelBase) map.get(id);
			// only allow dups if plug-in existing in map is not a workspace plug-in
			if (existing == null || existing.getUnderlyingResource() == null)
				addToMap(map, model);
		}
		return map;
	}

	public static IProject[] getAffectedProjects(ILaunchConfiguration config) throws CoreException {
		// if restarting, no need to check projects for errors
		if (config.getAttribute(IPDEUIConstants.RESTART, false))
			return new IProject[0];
		ArrayList projects = new ArrayList();
		IPluginModelBase[] models = getSelectedWorkspacePlugins(config);
		for (int i = 0; i < models.length; i++) {
			IProject project = models[i].getUnderlyingResource().getProject();
			if (project.hasNature(JavaCore.NATURE_ID))
				projects.add(project);
		}

		// add fake "Java Search" project
		SearchablePluginsManager manager = PDECore.getDefault().getSearchablePluginsManager();
		IJavaProject proxy = manager.getProxyProject();
		if (proxy != null) {
			projects.add(proxy.getProject());
		}
		return (IProject[]) projects.toArray(new IProject[projects.size()]);
	}

	public static void runValidationOperation(final LaunchValidationOperation op, IProgressMonitor monitor) throws CoreException {
		op.run(monitor);
		if (op.hasErrors()) {
			final int[] result = new int[1];
			final Display display = LauncherUtils.getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					PluginStatusDialog dialog = new PluginStatusDialog(display.getActiveShell());
					dialog.showCancelButton(true);
					dialog.setInput(op.getInput());
					result[0] = dialog.open();
				}
			});
			if (result[0] == IDialogConstants.CANCEL_ID)
				throw new CoreException(Status.CANCEL_STATUS);
		}
	}

}

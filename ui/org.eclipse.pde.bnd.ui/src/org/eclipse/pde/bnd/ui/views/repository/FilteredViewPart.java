/*******************************************************************************
 * Copyright (c) 2010, 2024 bndtools project and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Neil Bartlett <njbartlett@gmail.com> - initial API and implementation
 *     BJ Hargrave <bj@bjhargrave.com> - ongoing enhancements
 *     Peter Kriens <Peter.Kriens@aqute.biz> - ongoing enhancements
 *     Christoph Läubrich - adjust to pde code base
 *******************************************************************************/
package org.eclipse.pde.bnd.ui.views.repository;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.pde.bnd.ui.FilterPanelPart;
import org.eclipse.pde.bnd.ui.Resources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

/**
 * An abstract base class for views that contain a textual filter field at the
 * top, which can be hidden or revealed by toggling a button in the toolbar.
 */
public abstract class FilteredViewPart extends ViewPart {

	private final FilterPanelPart filterPanel = new FilterPanelPart(Resources.getScheduler());

	private Action					filterAction;

	private Composite				stackPanel;
	private StackLayout				stack;
	private Composite				topPanel;
	private Composite				mainPanel;

	/**
	 * Implements {@link ViewPart#createPartControl(Composite)}
	 */
	@Override
	public final void createPartControl(Composite parent) {
		// Create controls
		stackPanel = new Composite(parent, SWT.NONE);
		stack = new StackLayout();
		stackPanel.setLayout(stack);

		topPanel = new Composite(stackPanel, SWT.NONE);

		// Filter panel
		Control filterControl = filterPanel.createControl(topPanel);

		// Main panel
		mainPanel = new Composite(stackPanel, SWT.NONE);
		createMainControl(mainPanel);

		// Layout
		stack.topControl = mainPanel;

		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topPanel.setLayout(layout);

		mainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		filterControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// Toolbar
		createActions();
		fillToolBar(getViewSite().getActionBars()
			.getToolBarManager());
	}

	private void createActions() {
		filterAction = new FilterAction();
	}

	/**
	 * Fill the view toolbar. Subclasses may override but must call
	 * <code>super.fillToolBar</code>
	 *
	 * @param toolBar The toolbar manager supplied by the workbench
	 */
	protected void fillToolBar(IToolBarManager toolBar) {
		toolBar.add(filterAction);
	}

	/**
	 * Create the main content of the view, below the filter bar.
	 *
	 * @param container The parent composite for the main content. Subclasses
	 *            should set an appropriate layout on this composite.
	 */
	protected abstract void createMainControl(Composite container);

	/**
	 * Called when the filter string is modified by the user. Subclasses should
	 * implement this method to apply the filter to the controls they create.
	 *
	 * @param filterString The new filter string, or an empty string ("") if
	 *            there is no filter (e.g., because the user hid the filter
	 *            bar).
	 */
	protected abstract void updatedFilter(String filterString);

	/*
	 * The filter toggle button
	 */
	private class FilterAction extends Action {
		public FilterAction() {
			super("Filter", IAction.AS_CHECK_BOX);
			setImageDescriptor(Resources.getImageDescriptor("/icons/filter.gif"));
		}

		@Override
		public void run() {
			if (filterAction.isChecked()) {
				stack.topControl = topPanel;
				mainPanel.setParent(topPanel);
				updatedFilter(filterPanel.getFilter());
			} else {
				stack.topControl = mainPanel;
				mainPanel.setParent(stackPanel);
				updatedFilter("");
			}
			stackPanel.layout(true, true);
			setFocus();
		}
	}

	@Override
	public void setFocus() {
		if (filterAction.isChecked()) {
			filterPanel.setFocus();
		} else {
			doSetFocus();
		}
	}

	/**
	 * Called when the view receives keyboard focus. Subclasses should implement
	 * to control
	 */
	protected void doSetFocus() {}
}

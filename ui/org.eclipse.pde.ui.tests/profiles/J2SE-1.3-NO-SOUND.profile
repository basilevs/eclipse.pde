###############################################################################
# Copyright (c) 2003, 2005 IBM Corporation and others.
#
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
# 
# Contributors:
#     IBM Corporation - initial API and implementation
###############################################################################
org.osgi.framework.system.packages = \
 javax.accessibility,\
 javax.naming,\
 javax.naming.directory,\
 javax.naming.event,\
 javax.naming.ldap,\
 javax.naming.spi,\
 javax.rmi,\
 javax.rmi.CORBA,\
 javax.swing,\
 javax.swing.border,\
 javax.swing.colorchooser,\
 javax.swing.event,\
 javax.swing.filechooser,\
 javax.swing.plaf,\
 javax.swing.plaf.basic,\
 javax.swing.plaf.metal,\
 javax.swing.plaf.multi,\
 javax.swing.table,\
 javax.swing.text,\
 javax.swing.text.html,\
 javax.swing.text.html.parser,\
 javax.swing.text.rtf,\
 javax.swing.tree,\
 javax.swing.undo,\
 javax.transaction,\
 org.omg.CORBA,\
 org.omg.CORBA_2_3,\
 org.omg.CORBA_2_3.portable,\
 org.omg.CORBA.DynAnyPackage,\
 org.omg.CORBA.ORBPackage,\
 org.omg.CORBA.portable,\
 org.omg.CORBA.TypeCodePackage,\
 org.omg.CosNaming,\
 org.omg.CosNaming.NamingContextPackage,\
 org.omg.SendingContext,\
 org.omg.stub.java.rmi
org.osgi.framework.bootdelegation = \
 javax.*,\
 org.omg.*,\
 sun.*,\
 com.sun.*
org.osgi.framework.executionenvironment = \
 OSGi/Minimum-1.0,\
 OSGi/Minimum-1.1,\
 JRE-1.1,\
 J2SE-1.2,\
 J2SE-1.3-NO-SOUND
osgi.java.profile.name = J2SE-1.3-NO-SOUND
org.eclipse.jdt.core.compiler.compliance=1.3
org.eclipse.jdt.core.compiler.source=1.3
org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.3
org.eclipse.jdt.core.compiler.problem.assertIdentifier=error
org.eclipse.jdt.core.compiler.problem.enumIdentifier=error

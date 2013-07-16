/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.ui.junit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.FailureTrace;
import org.eclipse.jdt.junit.model.ITestSuiteElement;
import org.eclipse.jface.action.Action;
import org.xpect.ui.util.FileUtil;
import org.xpect.ui.util.TestDataUIUtil;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
class CompareAction extends Action {

	private final Multimap<IFile, ITestCaseElement> files2tests;

	public CompareAction(ITestElement ctx) {
		super();
		this.files2tests = getComparableFiles(ctx);
		setText("Compare");
		setToolTipText("Compare test expecation with actual test result.");
	}

	private void collectITestElements(ITestElement ele, List<ITestCaseElement> result) {
		if (ele instanceof ITestCaseElement)
			result.add((ITestCaseElement) ele);
		else if (ele instanceof ITestSuiteElement)
			for (ITestElement child : ((ITestSuiteElement) ele).getChildren())
				collectITestElements(child, result);
	}

	private IFile findFileInWorkspace(URI uri, Map<URI, IFile> cache) {
		URI u = uri.trimFragment();
		IFile file = cache.get(u);
		if (file == null)
			cache.put(u, file = FileUtil.findFileInWorkspace(u));
		return file;
	}

	private Multimap<IFile, ITestCaseElement> getComparableFiles(ITestElement root) {
		List<ITestCaseElement> elements = Lists.newArrayList();
		Map<URI, IFile> cache = Maps.newHashMap();
		Multimap<IFile, ITestCaseElement> result = LinkedHashMultimap.create();
		collectITestElements(root, elements);
		for (ITestCaseElement ele : elements)
			if (isComparable(ele)) {
				String file = TestDataUIUtil.parse(ele).get("file");
				if (file != null) {
					IFile iFile = findFileInWorkspace(URI.createURI(file), cache);
					result.put(iFile, ele);
				}
			}
		return result;
	}

	private boolean isComparable(ITestElement ele) {
		FailureTrace trace = ele.getFailureTrace();
		return trace != null && trace.getExpected() != null && trace.getActual() != null;
	}

	@Override
	public boolean isEnabled() {
		return !files2tests.isEmpty();
	}

	@Override
	public void run() {
		for (IFile file : files2tests.keySet()) {
			Collection<ITestCaseElement> tests = files2tests.get(file);
			FailureCompareEditorInput inp = new FailureCompareEditorInput(file, tests);
			CompareUI.openCompareEditor(inp);
		}
	}
}
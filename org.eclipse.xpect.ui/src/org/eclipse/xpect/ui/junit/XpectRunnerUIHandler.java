package org.eclipse.xpect.ui.junit;

import java.util.Collections;
import java.util.Map;

import org.eclipse.compare.CompareUI;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.internal.junit.model.TestElement;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestSuiteElement;
import org.eclipse.jdt.junit.runners.IRunnerUIHandler;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.xpect.lib.TestDataUtil;
import org.eclipse.xtext.ui.LanguageSpecific;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.util.Strings;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class XpectRunnerUIHandler implements IRunnerUIHandler {

	@Inject
	@LanguageSpecific
	private IURIEditorOpener xpectOpener;

	@Inject
	private IURIEditorOpener globalOpener;

	@Override
	public void contextMenuAboutToShow(ITestElement ctx, IMenuManager menu) {
		CompareAction compareAction = new CompareAction(ctx);
		if (compareAction.isEnabled())
			menu.add(compareAction);
		Map<String, String> parsed = parse(ctx);
		String file = parsed.get("file");
		if (file != null)
			menu.add(new OpenFileAction(xpectOpener, URI.createURI(file), "Go to XPECT", "Show XPECT statement in the Xpect file editor."));
		String method = parsed.get("method");
		if (method != null)
			menu.add(new OpenJavaMethodAction(ctx, method, "Go to Method", "Show Java Method declaration"));
	}

	protected Map<String, String> parse(ITestElement element) {
		if (element instanceof ITestCaseElement)
			return TestDataUtil.decode(((ITestCaseElement) element).getTestMethodName());
		if (element instanceof ITestSuiteElement)
			return TestDataUtil.decode(((ITestSuiteElement) element).getSuiteTypeName());
		return Collections.emptyMap();
	}

	@Override
	public boolean handleDoubleClick(ITestElement ctx) {
		if (ctx instanceof TestElement && ((TestElement) ctx).isComparisonFailure()) {
			FailureCompareEditorInput inp = new FailureCompareEditorInput(ctx);
			CompareUI.openCompareEditor(inp);
			return true;
		}
		Map<String, String> parsed = parse(ctx);
		String file = parsed.get("file");
		if (file != null) {
			new OpenFileAction(xpectOpener, URI.createURI(file), "foo", "bar").run();
			return true;
		}
		return false;
	}

	@Override
	public String getSimpleLabel(ITestElement element) {
		Map<String, String> parsed = parse(element);
		String title = parsed.get("title");
		if (!Strings.isEmpty(title))
			return title;
		return "unknown type:" + element.getClass();
	}

}

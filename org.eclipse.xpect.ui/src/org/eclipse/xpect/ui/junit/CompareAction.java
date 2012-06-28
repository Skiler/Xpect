package org.eclipse.xpect.ui.junit;

import org.eclipse.compare.CompareUI;
import org.eclipse.jdt.internal.junit.model.TestElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jface.action.Action;

@SuppressWarnings("restriction")
class CompareAction extends Action {

	private ITestElement ctx;

	public CompareAction(ITestElement ctx) {
		super();
		this.ctx = ctx;
		setText("Compare");
		setToolTipText("Compare test expecation with actual test result.");
	}

	@Override
	public boolean isEnabled() {
		return ctx instanceof TestElement && ((TestElement) ctx).isComparisonFailure();
	}

	@Override
	public void run() {
		FailureCompareEditorInput inp = new FailureCompareEditorInput(ctx);
		CompareUI.openCompareEditor(inp);
	}
}
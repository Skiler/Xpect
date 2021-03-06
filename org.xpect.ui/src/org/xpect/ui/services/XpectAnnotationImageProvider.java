package org.xpect.ui.services;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.xpect.ui.internal.XpectActivator;

public class XpectAnnotationImageProvider implements IAnnotationImageProvider {

	public static String ANNOTATION_NAME_SUCCESS = "org.xpect.ui.live_test_execution_success_annotation";

	@Override
	public Image getManagedImage(Annotation annotation) {
		if (ANNOTATION_NAME_SUCCESS.equals(annotation.getType())) {
			ImageRegistry imageRegistry = XpectActivator.getInstance().getImageRegistry();
			Image image = imageRegistry.get(ANNOTATION_NAME_SUCCESS);
			if (image == null) {
				image = AbstractUIPlugin.imageDescriptorFromPlugin("org.xpect.ui", "icons/testok.gif").createImage();
				imageRegistry.put(ANNOTATION_NAME_SUCCESS, image);
			}
			return image;
		} else {
			return null;
		}
	}

	@Override
	public String getImageDescriptorId(Annotation annotation) {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		return null;
	}

}

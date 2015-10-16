package org.freeplane.core.ui.menubuilders.ribbon;

import java.awt.Dimension;
import java.net.URL;

import javax.swing.JPanel;

import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.menubuilders.generic.Entry;
import org.freeplane.core.ui.menubuilders.generic.EntryAccessor;
import org.freeplane.core.ui.menubuilders.generic.EntryVisitor;
import org.freeplane.core.ui.menubuilders.generic.ResourceAccessor;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryFooter;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

public class JRibbonApplicationMenuBuilder implements EntryVisitor {
	final ResourceAccessor resourceAccessor;
	
	public JRibbonApplicationMenuBuilder(ResourceAccessor resourceAccessor) {
		super();
		this.resourceAccessor = resourceAccessor;
	}
	@Override
	public void visit(Entry entry) {
		new EntryAccessor().setComponent(entry, initApplicationMenu(entry));
	}
	
	private RibbonApplicationMenuContainerImpl initApplicationMenu(Entry entry) {
		JRibbon ribbon = ((JRibbon)new EntryAccessor().getAncestorComponent(entry));
		final RibbonApplicationMenuContainerImpl container = new RibbonApplicationMenuContainerImpl(ribbon);
		//TODO - replace with resourceAccessor?
		String appName = ResourceController.getResourceController().getProperty("ApplicationName", "Freeplane");
		URL location = ResourceController.getResourceController().getResource("/images/"+appName.trim()+"_app_menu_128.png");
		if (location != null) {
			ResizableIcon icon = ImageWrapperResizableIcon.getIcon(location, new Dimension(32, 32));
			ribbon.setApplicationIcon(icon);
		}
		return container;
	}

	@Override
	public boolean shouldSkipChildren(Entry entry) {
		return false;
	}
}

interface RibbonApplicationMenuContainer {
	public void add(RibbonApplicationMenuEntryPrimary comp);
	
	public void add(SecondaryGroupEntry comp);
	
	public void add(RibbonApplicationMenuEntryFooter comp);
}

class RibbonApplicationMenuContainerImpl implements RibbonApplicationMenuContainer {

	private final JRibbon ribbon;

	public RibbonApplicationMenuContainerImpl(JRibbon ribbon) {
		this.ribbon = ribbon;
	}

	public void add(RibbonApplicationMenuEntryPrimary comp) {
		RibbonApplicationMenu appMenu = cloneMenu();
		appMenu.addMenuEntry(comp);
		ribbon.setApplicationMenu(appMenu);
	}

	public void add(SecondaryGroupEntry comp) {
		throw new RuntimeException("not supported!");
	}
	
	public void add(RibbonApplicationMenuEntryFooter comp) {
		RibbonApplicationMenu appMenu = cloneMenu();
		appMenu.addFooterEntry(comp);
		ribbon.setApplicationMenu(appMenu);
	}
	
	private RibbonApplicationMenu cloneMenu() {
		final RibbonApplicationMenu appMenu = new RibbonApplicationMenu();
		increasePreferredWidth(appMenu);
		RibbonApplicationMenu oldMenu = ribbon.getApplicationMenu();
		if(oldMenu != null) {
			for(RibbonApplicationMenuEntryFooter footer : oldMenu.getFooterEntries()) {
				appMenu.addFooterEntry(footer);
			}
			for(RibbonApplicationMenuEntryPrimary entry : oldMenu.getPrimaryEntries().get(oldMenu.getPrimaryEntries().size() - 1)) {
				appMenu.addMenuEntry(entry);
			}
		}
		return appMenu;
	}

	private void increasePreferredWidth(final RibbonApplicationMenu appMenu) {
		appMenu.setDefaultCallback(new RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback() {
			@Override
			public void menuEntryActivated(JPanel targetPanel) {
				Dimension preferredSize = targetPanel.getPreferredSize();
				targetPanel.setPreferredSize(new Dimension( preferredSize.width * 5 / 3, preferredSize.height));
				appMenu.setDefaultCallback(null);
			}
		});
	}
}
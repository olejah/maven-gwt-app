package com.mycompany.mywebapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class UsingCustomSplitLayoutPanel implements EntryPoint {

	@Override
	public void onModuleLoad() {
		CustomSplitLayoutPanel layoutPanel = new CustomSplitLayoutPanel();
		
		RootLayoutPanel.get().add(layoutPanel);
		
	}

}

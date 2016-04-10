package com.mycompany.mywebapp.client;

import java.util.HashSet;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.DOM;
import com.mycompany.mywebapp.shared.User;
import com.mycompany.mywebapp.shared.UserManager;

public class SelectAllHeader extends Header<Boolean> {
	private static final String CHECKBOX_ID = "selectAllCheckbox";
	private Boolean checked;
	private HashSet<User> userSet;
	private CustomCheckboxCell customCheckBox;
	private InputElement disableCheckboxes;
	
	public SelectAllHeader(CustomCheckboxCell customCheckBox) {
        super(new CheckboxCell());
        this.customCheckBox = customCheckBox;
    }
	
	@Override
    public void render(final Context context, final SafeHtmlBuilder sb) {
		disableCheckboxes = Document.get().getElementById("disableCheckBoxes").cast();
		StringBuilder builder = new StringBuilder("<input type=\"checkbox\" id=\"");
		builder.append(CHECKBOX_ID);
		if(disableCheckboxes != null && disableCheckboxes.isChecked()){
			builder.append("\" disabled=\"true\"");
		}
		builder.append("\"/>");
        sb.appendHtmlConstant(builder.toString());
    }

    @Override
    public Boolean getValue() {
        Element checkboxElem = DOM.getElementById(CHECKBOX_ID);
        return checkboxElem.getPropertyBoolean("checked");
    }

    @Override
    public void onBrowserEvent(final Context context, final Element element, final NativeEvent nativeEvent) {
       this.userSet = customCheckBox.getUserSet();
       InputElement[] customCheckboxes = customCheckBox.getCustomCheckboxesByClassName();
       InputElement selectAllCheckbox = Document.get().getElementById(CHECKBOX_ID).cast();
       checked = selectAllCheckbox.isChecked();
       for (int i = 0; i < customCheckboxes.length; i++){
        	if(checked){
        		customCheckboxes[i].setChecked(true);
        		CustomSplitLayoutPanel.setEnabled(true);
        	} else{
        		customCheckboxes[i].setChecked(false);
        		CustomSplitLayoutPanel.setEnabled(false);
        	}
       }
       customCheckBox.setUserSet(userSet);
       if(checked){
        	for(User user : UserManager.get().getDataProvider().getList()){
    			userSet.add(user);
    		}
       } else {
        	for(User user : UserManager.get().getDataProvider().getList()){
    			userSet.remove(user);
    		}
       }
    }

	public static String getCheckBoxId(){
		return CHECKBOX_ID;
	}
}

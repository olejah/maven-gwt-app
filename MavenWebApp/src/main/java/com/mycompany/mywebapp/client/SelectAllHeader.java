package com.mycompany.mywebapp.client;

import java.util.HashSet;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasValue;
import com.mycompany.mywebapp.shared.User;
import com.mycompany.mywebapp.shared.UserManager;

public class SelectAllHeader extends Header<Boolean> implements HasValue<Boolean>{
	private final String checkboxID = "selectAllCheckbox";
	private Boolean checked;
	private HandlerManager handlerManager;
	private static Boolean flag = false;
	private HashSet<User> userSet;
	private CustomCheckboxCell customCheckBox;
	
	public SelectAllHeader(CustomCheckboxCell customCheckBox) {
        super(new CheckboxCell());
        checked = false;
        this.customCheckBox = customCheckBox;
    }
	
	public SelectAllHeader() {
        super(new CheckboxCell());
        checked = false;
    }
	
	@Override
    public void render(final Context context, final SafeHtmlBuilder sb) {
        String html = "<input type=\"checkbox\" id=\"" + checkboxID + "\"/>";

        sb.appendHtmlConstant(html);
    }

    @Override
    public Boolean getValue() {
        Element checkboxElem = DOM.getElementById(checkboxID);

        return checkboxElem.getPropertyBoolean("checked");

    }

    @Override
    public void onBrowserEvent(final Context context, final Element element, final NativeEvent nativeEvent) {
    	int eventType = Event.as(nativeEvent).getTypeInt();
    	this.userSet = customCheckBox.getUserSet();
        if (eventType == Event.ONCHANGE)
        {
           nativeEvent.preventDefault();
           
           if (flag){
        	   checked = flag;
        	   flag = false;
           }else{
        	   setValue(!checked, true);
           }
          
           NodeList<Element> inputNodeList = Document.get().getElementsByTagName("input");
           
	        for (int i = 0; i < inputNodeList.getLength(); i++){
	        	InputElement input = inputNodeList.getItem(i).cast();
	        	if(checked){
	        		input.setChecked(true);
	        		CustomSplitLayoutPanel.setEnabled(true);
	        	} else{
	        		input.setChecked(false);
	        		CustomSplitLayoutPanel.setEnabled(false);
	        	}
	        	if(input.getId().equals("selectAllCheckbox")){
	        		checked = input.isChecked();
	        	}
	        	customCheckBox.setUserSet(userSet);
	        	
	        }
	        
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
        
    }

	@Override
	public void fireEvent(GwtEvent<?> event) {
		ensureHandlerManager().fireEvent(event);
	}

	 private HandlerManager ensureHandlerManager()
	   {
	      if (handlerManager == null)
	      {
	         handlerManager = new HandlerManager(this);
	      }
	      return handlerManager;
	   }

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return ensureHandlerManager().addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void setValue(Boolean value) {
		checked = value;
	}

	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		 checked = value;
	      if (fireEvents)
	      {
	         ValueChangeEvent.fire(this, value);
	      }
	}
	
	public static void setFlag(Boolean value){
		flag = value;
	}
	
	
}

package com.mycompany.mywebapp.client;

import java.util.HashSet;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.mycompany.mywebapp.shared.User;
import com.mycompany.mywebapp.shared.UserManager;

public class CustomCheckboxCell extends CheckboxCell {
	private static final String CUSTOM_CHECKBOX_CLASS = "customCheckboxClass";
	private InputElement[] customCheckBoxes;
	private HashSet<User> userSet;
	private InputElement disableCheckboxes;

	public CustomCheckboxCell(boolean dependsOnSelection,
			boolean handlesSelection, HashSet<User> users) {
		super(dependsOnSelection, handlesSelection);
		this.userSet = users;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Boolean value,
			NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		int index = context.getIndex();

		InputElement input = parent.getFirstChild().cast();
		customCheckBoxes = getCustomCheckboxesByClassName();
		if (input.isChecked()) {
			userSet.add(UserManager.get().getDataProvider().getList().get(index));
			CustomSplitLayoutPanel.setEnabled(true);
		} else {
			userSet.remove(UserManager.get().getDataProvider().getList().get(index));
		}
		
		if (checkAllSelected(customCheckBoxes)) {
			setHeaderCheckbox();
		} else {
			unselectHeaderCheckBox(customCheckBoxes);
		}

		if (userSet.isEmpty()) {
			CustomSplitLayoutPanel.setEnabled(false);
		}
	}

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		int index = context.getIndex();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		disableCheckboxes = Document.get().getElementById("disableCheckBoxes").cast();
		if (value != null && ((viewData != null) ? viewData : value)) {
			sb.append(generateInputChecked(String.valueOf(UserManager.get()
					.getDataProvider().getList().get(index).getId())));
		} else {
			sb.append(generateInputUnchecked(String.valueOf(UserManager.get()
					.getDataProvider().getList().get(index).getId())));
		}
	}

	public HashSet<User> getUserSet() {
		return userSet;
	}

	public void setUserSet(HashSet<User> userSet) {
		this.userSet = userSet;
	}

	public static interface Handler extends EventHandler {
		void onSelectionChange(SelectionChangeEvent event);
	}

	private static String generateID(String userID) {
		return "customCheckboxId-" + userID;
	}

	private SafeHtml generateInputChecked(String userId) {
		StringBuilder builder = new StringBuilder(
				"<input type=\"checkbox\"").append(" class=\"")
				.append(CUSTOM_CHECKBOX_CLASS).append("\" ").append("id=\"")
				.append(generateID(userId));
		if(disableCheckboxes != null && disableCheckboxes.isChecked()){
			builder.append("\" disabled=\"true\"");
		}
		builder.append("\" tabindex=\"-1\" checked/>");
		return SafeHtmlUtils.fromSafeConstant(builder.toString());
	}

	private SafeHtml generateInputUnchecked(String userId) {
		
		StringBuilder builder = new StringBuilder(
				"<input type=\"checkbox\"").append(" class=\"")
				.append(CUSTOM_CHECKBOX_CLASS).append("\" ").append("id=\"")
				.append(generateID(userId));
		if(disableCheckboxes != null && disableCheckboxes.isChecked()){
			builder.append("\" disabled=\"true\"");
		}
		builder.append("\" tabindex=\"-1\"/>");
		return SafeHtmlUtils.fromSafeConstant(builder.toString());
	}

	private void setHeaderCheckbox() {
		InputElement selectAllInputElement = Document.get().getElementById(SelectAllHeader.getCheckBoxId()).cast();
		selectAllInputElement.setChecked(true);
	}

	private boolean checkAllSelected(InputElement[] elementsByClass) {
		boolean areAllSelected = true;
		for (int i = 0; i < elementsByClass.length; i++) {
			if (!elementsByClass[i].isChecked()) {
				areAllSelected = false;
				break;
			} else {
				areAllSelected = true;
			}
		}
		return areAllSelected;
	}

	private void unselectHeaderCheckBox(InputElement[] elementsByClass) {
		InputElement selectAllInputElement = Document.get().getElementById(SelectAllHeader.getCheckBoxId()).cast();
		for (int i = 0; i < elementsByClass.length; i++) {
			if (!elementsByClass[i].isChecked() && selectAllInputElement.isChecked()) {
				selectAllInputElement.setChecked(false);
				selectAllInputElement.removeAttribute("checked");
			}
		}
	}
	
	public InputElement[] getCustomCheckboxesByClassName(){
		return getInputElementsByClassName(CUSTOM_CHECKBOX_CLASS, Document.get().getParentElement());
	}
	
	public static String getCustomCheckboxClassName(){
		return CUSTOM_CHECKBOX_CLASS;
	}

	private InputElement[] getInputElementsByClassName(String className,
			Element parentElement) {
		JavaScriptObject elements = getElementsByClassNameInternal(className,
				parentElement);
		int length = getArrayLength(elements);
		InputElement[] result = new InputElement[length];
		for (int i = 0; i < length; i++) {
			result[i] = getArrayElement(elements, i).cast();
		}
		return result;
	}

	private static native JavaScriptObject getElementsByClassNameInternal(
			String className, Element parentElement)/*-{
		if ((parentElement && parentElement.getElementsByClassName)
				|| $doc.getElementsByClassName) {
			return (parentElement || $doc).getElementsByClassName(className);
		} else if (!!document.evaluate) {
			var expression = ".//*[contains(concat(' ', @class, ' '), ' "
					+ className + " ')]";
			var results = [];
			var query = $doc.evaluate(expression, parentElement || $doc, null,
					XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
			for (var i = 0, length = query.snapshotLength; i < length; i++)
				results.push(query.snapshotItem(i));
			return results;
		} else {
			var children = (parentElement || $doc.body)
					.getElementsByTagName('*');
			var elements = [], child, pattern = new RegExp("(^|\\s)"
					+ className + "(\\s|$)");
			for (var i = 0, length = children.length; i < length; i++) {
				child = children[i];
				var elementClassName = child.className;
				if (elementClassName.length == 0)
					continue;
				if (elementClassName == className
						|| elementClassName.match(pattern)) {
					elements.push(child);
				}
			}
			return elements;
		}
	}-*/;

	private static native int getArrayLength(JavaScriptObject array)/*-{
		return array.length;
	}-*/;

	private static native Element getArrayElement(JavaScriptObject array,
			int position)/*-{
		return (position >= 0 && position < array.length ? array[position]
				: null);
	}-*/;
}

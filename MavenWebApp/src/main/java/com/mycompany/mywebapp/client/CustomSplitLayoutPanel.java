package com.mycompany.mywebapp.client;

import java.util.HashSet;

import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.mycompany.mywebapp.shared.User;
import com.mycompany.mywebapp.shared.UserManager;

public class CustomSplitLayoutPanel extends Composite {

	private final static String ID_LABEL = "ID";
	private final static String NAME_LABEL = "Name";
	private final static String ROLE_LABEL = "Role";
	private final static String EMAIL_LABEL = "Email: ";
	private final static String SURNAME_LABEL = "Surname: ";
	private final static int[] WHITELISTED_COLUMNS = { 1, 2, 3 };
	
	private final TextArea textArea = new TextArea();
	private final ScrollPanel panel = new ScrollPanel();
	private final DialogBox dialogBox = new DialogBox(true, true);
	
	private InputElement selectAllHeader;
	private InputElement[] customCheckboxes;
	private	User selected;
	private SingleSelectionModel<User> selectionModel = new SingleSelectionModel<User>(User.KEY_PROVIDER);
	private HashSet<User> userSet = new HashSet<User>();
	private CustomCheckboxCell customCheckBox = new CustomCheckboxCell(false, false, userSet);
	
	@UiField(provided = true)
	DataGrid<User> dataGrid;
	@UiField
	HTML emailAndSurname;
	@UiField
	static Button button;
	@UiField
	static CheckBox disableCheckBoxes;
	@UiField
	static Label disableCheckBoxesLabel;
	@UiField
	static CheckBox disableSelection;
	@UiField
	static Label disableSelectionLabel;
	
	public CustomSplitLayoutPanel() {
		dataGrid = new DataGrid<User>();
		dataGrid.setWidth("100%");

		initTableColumns();

		UserManager.get().addDataDisplay(dataGrid);
		initWidget(uiBinder.createAndBindUi(this));

		panel.add(textArea);
		panel.setSize("600px", "200px");
		dialogBox.setWidget(panel);
		dialogBox.center();
		dialogBox.setAutoHideEnabled(true);
		dialogBox.setGlassEnabled(true);
		dialogBox.hide();
		button.setEnabled(false);
		disableCheckBoxes.getElement().getFirstChildElement().setId("disableCheckBoxes");
	}
	
	interface CustomSplitLayoutPanelUiBinder extends
		UiBinder<Widget, CustomSplitLayoutPanel> {
	}
	
	@UiHandler("disableCheckBoxes")
	void disableCheckBoxesHandler(ClickEvent event){
		customCheckboxes = customCheckBox.getCustomCheckboxesByClassName(); 
		selectAllHeader = Document.get().getElementById(SelectAllHeader.getCheckBoxId()).cast();
		if (disableCheckBoxes.isChecked()){
			for(InputElement element : customCheckboxes){
				element.setAttribute("disabled", "true");
			}
			selectAllHeader.setAttribute("disabled", "true");
		} else{
			for(InputElement element : customCheckboxes){
				element.removeAttribute("disabled");
			}
			selectAllHeader.removeAttribute("disabled");
		}
	}
	
	@UiHandler("disableSelection")
	void disableSelection(ClickEvent event){
		int[] disabledWhiteList = {};
		if(disableSelection.isChecked()){
			dataGrid.setSelectionModel(
					selectionModel,
					DefaultSelectionEventManager
							.createCustomManager(new DefaultSelectionEventManager.WhitelistEventTranslator<User>(
									disabledWhiteList)));
		} else{
			dataGrid.setSelectionModel(
					selectionModel,
					DefaultSelectionEventManager
							.createCustomManager(new DefaultSelectionEventManager.WhitelistEventTranslator<User>(
									WHITELISTED_COLUMNS)));
		
		}
	}
	
	@UiHandler("button")
	void handleClick(ClickEvent event) {
		userSet = customCheckBox.getUserSet();
		StringBuilder userInfoBuilder = new StringBuilder();
		for (User user : userSet) {
			userInfoBuilder.append(user.toString());
		}
		String text = userInfoBuilder.toString();
		textArea.setText(text);
		textArea.setSize("800px", "600px");
		dialogBox.setAnimationEnabled(true);
		dialogBox.setText("User info");
		dialogBox.show();
	}

	private static CustomSplitLayoutPanelUiBinder uiBinder = GWT
			.create(CustomSplitLayoutPanelUiBinder.class);

	private void initTableColumns() {

		// Header checkbox
		Header<Boolean> headerCheckbox = new SelectAllHeader(customCheckBox) {
			@Override
			public Boolean getValue() {
				userSet = customCheckBox.getUserSet();
				boolean allSelected = (userSet.size() == UserManager.get()
						.getDataProvider().getList().size());

				return allSelected;
			}
		};

		// Checkbox column.

		final Column<User, Boolean> checkColumn = new Column<User, Boolean>(customCheckBox) {
			@Override
			public Boolean getValue(User object) {
				Boolean isSelected = false;
				boolean allSelected = (userSet.size() == UserManager.get()
						.getDataProvider().getList().size());
				customCheckboxes = customCheckBox.getCustomCheckboxesByClassName();
				selectAllHeader = Document.get().getElementById(SelectAllHeader.getCheckBoxId()).cast();
				selectAllHeader.setChecked(allSelected);
				for (int i = 0; i < customCheckboxes.length; i++) {
					if (customCheckboxes[i].isChecked()) {
						button.setEnabled(true);
						break;
					}
				}
				userSet = customCheckBox.getUserSet();
				if (userSet.isEmpty()) {
					button.setEnabled(false);
					isSelected = false;
				} else {
					if (selected != null) {
						for (User setUser : userSet) {
							if (setUser.equals(object)) {
								button.setEnabled(true);
								isSelected = true;
								break;
							}
						}
					}
				}
				return isSelected;
			}
		};
		dataGrid.addColumn(checkColumn, headerCheckbox);

		// ID Column.
		Column<User, String> idColumn = new Column<User, String>(new TextCell()) {
			@Override
			public String getValue(User object) {
				return String.valueOf(object.getId());
			}
		};
		dataGrid.addColumn(idColumn, SafeHtmlUtils.fromSafeConstant(ID_LABEL));

		// First name.
		Column<User, String> firstNameColumn = new Column<User, String>(
				new TextCell()) {
			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		dataGrid.addColumn(firstNameColumn,SafeHtmlUtils.fromSafeConstant(NAME_LABEL));

		// Role column.
		Column<User, String> roleColumn = new Column<User, String>(
				new SelectionCell(UserManager.get().getRolesList())) {
			@Override
			public String getValue(User object) {
				return object.getRole();
			}
		};
		dataGrid.addColumn(roleColumn,SafeHtmlUtils.fromSafeConstant(ROLE_LABEL));
		dataGrid.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
		
		dataGrid.setSelectionModel(selectionModel,DefaultSelectionEventManager
						.createCustomManager(new DefaultSelectionEventManager.WhitelistEventTranslator<User>(
								WHITELISTED_COLUMNS)));

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
				public void onSelectionChange(SelectionChangeEvent event) {
					selected = selectionModel.getSelectedSet().iterator()
							.next();
					if (selected != null) {
						emailAndSurname.setHTML(new StringBuilder("<div>")
								.append(EMAIL_LABEL)
								.append(selected.getEmail())
								.append("</div>").append("<div>")
								.append(SURNAME_LABEL)
								.append(selected.getSurname())
								.append("</div>").toString());
						selectionModel.setSelected(selected, true);
					}
				}
		});
	}

	public static void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}
}

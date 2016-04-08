package com.mycompany.mywebapp.client;

import java.util.HashSet;

import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
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

	Boolean isSelected;
	User selected;
	SingleSelectionModel<User> selectionModel = new SingleSelectionModel<User>(
			User.KEY_PROVIDER);
	HashSet<User> userSet = new HashSet<User>();
	@UiField(provided = true)
	DataGrid<User> dataGrid;
	@UiField
	HTML emailAndSurname;
	@UiField
	static Button button;
	final TextArea textArea = new TextArea();

	final ScrollPanel panel = new ScrollPanel();
	final DialogBox dialogBox = new DialogBox(true, true);
	CustomCheckboxCell customCheckBox = new CustomCheckboxCell(true, false, userSet);

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

	interface CustomSplitLayoutPanelUiBinder extends
			UiBinder<Widget, CustomSplitLayoutPanel> {
	}

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
	}

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

		final Column<User, Boolean> checkColumn = new Column<User, Boolean>(
				customCheckBox) {
			@Override
			public Boolean getValue(User object) {
				boolean allSelected = (userSet.size() == UserManager.get()
						.getDataProvider().getList().size());
				NodeList<Element> inputNodeList = Document.get()
						.getElementsByTagName("input");
				for (int i = 0; i < inputNodeList.getLength(); i++) {
					InputElement inputElement = inputNodeList.getItem(i).cast();
					if (inputElement.getId().equals("selectAllCheckbox")) {
						inputElement.setChecked(allSelected);
					} else if (inputElement.isChecked()) {
						button.setEnabled(true);
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
								return true;
							}
						}
					}
				}

				return isSelected;
			}
		};
		dataGrid.addColumn(checkColumn, headerCheckbox);
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);

		// ID Column.
		Column<User, String> idColumn = new Column<User, String>(new TextCell()) {
			@Override
			public String getValue(User object) {
				return String.valueOf(object.getId());
			}
		};
		dataGrid.addColumn(idColumn, SafeHtmlUtils.fromSafeConstant(ID_LABEL));
		dataGrid.setColumnWidth(idColumn, 60, Unit.PX);

		// First name.
		Column<User, String> firstNameColumn = new Column<User, String>(
				new TextCell()) {
			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		dataGrid.addColumn(firstNameColumn,
				SafeHtmlUtils.fromSafeConstant(NAME_LABEL));
		dataGrid.setColumnWidth(firstNameColumn, 90, Unit.PX);

		// Role column.
		Column<User, String> roleColumn = new Column<User, String>(
				new SelectionCell(UserManager.get().getRolesList())) {
			@Override
			public String getValue(User object) {
				return object.getRole();
			}
		};
		dataGrid.addColumn(roleColumn,
				SafeHtmlUtils.fromSafeConstant(ROLE_LABEL));
		dataGrid.setColumnWidth(roleColumn, 50, Unit.PX);
		dataGrid.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

		int[] whitelistedColumns = { 1, 2, 3 };
		dataGrid.setSelectionModel(
				selectionModel,
				DefaultSelectionEventManager
						.createCustomManager(new DefaultSelectionEventManager.WhitelistEventTranslator<User>(
								whitelistedColumns)));

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
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

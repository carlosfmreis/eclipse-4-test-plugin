package eu.tardis.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewProjectWizardPage extends WizardPage {

	private Text projectNameText;

	public NewProjectWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Enter a project name.");
	}

	@Override
	public void createControl(Composite parent) {
		setPageComplete(false);

		final Composite composite = new Composite(parent, SWT.APPLICATION_MODAL);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		composite.setLayout(layout);
		setControl(composite);

		new Label(composite, SWT.NONE).setText("Project Name");

		this.projectNameText = new Text(composite, SWT.BORDER);
		this.projectNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.projectNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				final Text widget = (Text) e.widget;
				if (widget.getText().trim().isEmpty()) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}
		});
	}

	public String getProjectName() {
		return this.projectNameText.getText().trim();
	}

}

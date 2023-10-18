package eu.tardis.views;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.osgi.service.prefs.BackingStoreException;

class PreferencesKeys {
	public static final String Text = "tardisTextField";
}

public class SettingsView {

	final IEclipsePreferences preferences;

	public SettingsView() {
		this.preferences = InstanceScope.INSTANCE.getNode("eu.tardis");
	}

	@PostConstruct
	public void createMyCustomView(Composite parent) {
		final String savedText = this.preferences.get(PreferencesKeys.Text, "Lorem ipsum dolor sit amet");

		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		final ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("TaRDIS Settings");

		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);

		final Text textField = new Text(form.getBody(), SWT.BORDER);
		textField.setMessage("Enter your text here...");
		textField.setText(savedText);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button button = new Button(form.getBody(), SWT.PUSH);
		button.setText("Save");
		button.addListener(SWT.Selection, e -> handleSave(textField.getText().trim()));
	}

	private void handleSave(String text) {
		this.preferences.put(PreferencesKeys.Text, text);
		try {
			this.preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

}

package eu.tardis.wizards;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.jface.operation.IRunnableWithProgress;

import eu.tardis.eu.tardis.perspectives.TardisPerspective;
import eu.tardis.natures.TardisNature;
import eu.tardis.wizards.pages.NewProjectWizardPage;

public class NewProjectWizard extends Wizard implements INewWizard {

	private final NewProjectWizardPage page1;

	public NewProjectWizard() {
		setWindowTitle("New TaRDIS Project");
		this.page1 = new NewProjectWizardPage("Create a TaRDIS Project");
		addPage(this.page1);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		final String projectName = this.page1.getProjectName();
		if (projectName.isEmpty()) {
			return false;
		} else {
			final IRunnableWithProgress op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException {
					createProject(projectName, monitor);
				}
			};

			try {
				getContainer().run(true, false, op);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return true;
		}
	}

	private void createProject(String projectName, IProgressMonitor monitor) throws CoreException {
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		project.create(monitor);
		project.open(monitor);

		final IProjectDescription description = project.getDescription();
		final String[] natures = description.getNatureIds();
		final String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = TardisNature.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, monitor);

		final IFolder confFolder = project.getFolder("config");
		confFolder.create(true, true, monitor);

		final IFile configFile = confFolder.getFile("config.conf");
		final String configFileContent = "# Set your configuration here\r\nproject-name=" + projectName;
		final ByteArrayInputStream configFileContentSource = new ByteArrayInputStream(configFileContent.getBytes());
		configFile.create(configFileContentSource, IFile.FORCE, monitor);

		final IFile gitIgnoreFile = project.getFile(".gitignore");
		final String gitIgnoreContent = ".classpath\r\nproject\r\n.settings\r\nbin\r\ntarget\r\nconf";
		final ByteArrayInputStream gitIgnoreContentSource = new ByteArrayInputStream(gitIgnoreContent.getBytes());
		gitIgnoreFile.create(gitIgnoreContentSource, IFile.FORCE, monitor);

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				try {
					workbenchWindow.getWorkbench().showPerspective(TardisPerspective.PERSPECTIVE_ID, workbenchWindow);
				} catch (WorkbenchException e) {
					e.printStackTrace();
				}
			}
		});
	}

}

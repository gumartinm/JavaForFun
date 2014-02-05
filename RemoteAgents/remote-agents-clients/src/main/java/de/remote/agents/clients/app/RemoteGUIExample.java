package de.remote.agents.clients.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RemoteGUIExample {

    private Shell shell;
    private Text textBox;
    private Display display;


    /**
     * @wbp.parser.entryPoint
     */
    public void open() {
        this.display = Display.getDefault();
        this.createContents();
        this.shell.open();
        this.shell.layout();
        while (!this.shell.isDisposed()) {
            if (!this.display.readAndDispatch()) {
                this.display.sleep();
            }
        }
    }

    public void updateTextBox(final String text) {
        this.display.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!RemoteGUIExample.this.textBox.isDisposed())
                    RemoteGUIExample.this.textBox.setText(text);
            }
        });
    }


    private void createContents() {
        this.shell = new Shell();
        this.shell.setSize(450, 300);
        this.shell.setText("Remote GUI");

        this.textBox = new Text(this.shell, SWT.BORDER);
        this.textBox.setBounds(111, 95, 181, 58);

        final Label lblRemoteDate = new Label(this.shell, SWT.NONE);
        lblRemoteDate.setBounds(111, 63, 113, 26);
        lblRemoteDate.setText("Remote Date:");
    }

}

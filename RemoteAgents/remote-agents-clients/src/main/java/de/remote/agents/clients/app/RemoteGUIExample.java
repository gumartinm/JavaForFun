package de.remote.agents.clients.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.remote.agents.services.WriteTextService;

public class RemoteGUIExample {

    private Shell shell;
    private Text textBox;
    private Display display;
    private Label lblTextToSend;
    private Text textToSend;
    private WriteTextService writeTextService;


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

    public void setWriteTextService(final WriteTextService writeTextService) {
        this.writeTextService = writeTextService;
    }

    private void createContents() {
        this.shell = new Shell();
        this.shell.setSize(526, 419);
        this.shell.setText("Remote GUI");

        this.textBox = new Text(this.shell, SWT.BORDER);
        this.textBox.setBounds(80, 66, 181, 58);

        final Label lblRemoteDate = new Label(this.shell, SWT.NONE);
        lblRemoteDate.setBounds(80, 34, 113, 26);
        lblRemoteDate.setText("Remote Date:");

        this.lblTextToSend = new Label(this.shell, SWT.NONE);
        this.lblTextToSend.setText("Text to send:");
        this.lblTextToSend.setBounds(80, 171, 113, 26);

        this.textToSend = new Text(this.shell, SWT.BORDER);
        this.textToSend.setBounds(80, 203, 181, 58);

        final Combo comboSendNumber = new Combo(this.shell, SWT.READ_ONLY);
        comboSendNumber.setItems(new String[] { "1", "20", "30", "40", "500",
                "633", "722", "899" });
        comboSendNumber.setBounds(80, 309, 113, 43);
        comboSendNumber.select(0);

        final Label lblNumberToSend = new Label(this.shell, SWT.NONE);
        lblNumberToSend.setText("Number to send:");
        lblNumberToSend.setBounds(80, 277, 113, 26);

        final Button btnSendtextAndNumber = new Button(this.shell, SWT.NONE);
        btnSendtextAndNumber.setText("SendTextAndNumber");
        btnSendtextAndNumber.setBounds(295, 302, 162, 23);
        btnSendtextAndNumber.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(final MouseEvent e) {
                final String text = RemoteGUIExample.this.textToSend.getText();
                final String item = comboSendNumber.getItem(comboSendNumber.getSelectionIndex());
                RemoteGUIExample.this.writeTextService.setWriteText(text,
                        Integer.valueOf(item));
            }
        });

        final Button btnSendtext = new Button(this.shell, SWT.NONE);
        btnSendtext.setBounds(295, 225, 162, 23);
        btnSendtext.setText("SendText");
        btnSendtext.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(final MouseEvent e) {
                final String text = RemoteGUIExample.this.textToSend.getText();
                RemoteGUIExample.this.writeTextService.setWriteText(text);
            }
        });
    }
}

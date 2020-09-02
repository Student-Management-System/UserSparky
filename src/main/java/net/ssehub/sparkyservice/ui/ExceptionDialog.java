package net.ssehub.sparkyservice.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.ssehub.studentmgmt.sparkyservice_api.ApiException;

public class ExceptionDialog extends JDialog {

    private static final long serialVersionUID = 9183739679619382009L;

    private ExceptionDialog(Window parent, Throwable exception) {
        super(parent);
        
        JPanel content = new JPanel(new BorderLayout(5, 5));
        
        JTextArea exceptionContent = new JTextArea(exceptionToString(exception));
        exceptionContent.setEditable(false);
        exceptionContent.setFont(Font.decode(Font.MONOSPACED));
        exceptionContent.setTabSize(4);
        
        JScrollPane scrollPane = new JScrollPane(exceptionContent);
        content.add(scrollPane, BorderLayout.CENTER);
        
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        content.add(south, BorderLayout.SOUTH);
        
        JButton okButton = new JButton("Ok");
        okButton.addActionListener((event) -> {
            dispose();
        });
        south.add(okButton);
        
        setContentPane(content);
        setModal(true);
        setTitle("Exception");
        setSize(600, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
    }
    
    private String exceptionToString(Throwable exception) {
        StringWriter stacktrace = new StringWriter();
        
        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            stacktrace.append("ApiException").append('\n');
            stacktrace.append("\tcode: ").append(String.valueOf(apiException.getCode())).append('\n');
            stacktrace.append("\trespondeBody:").append('\n');
            for (String line : apiException.getResponseBody().split("\n")) {
                stacktrace.append("\t\t").append(line).append('\n');
            }
        }
        
        PrintWriter tmp = new PrintWriter(stacktrace);
        exception.printStackTrace(tmp);
        tmp.close();
        
        return stacktrace.toString();
    }
    
    public static void showExceptionDialog(Throwable exception, Window parent) {
        Runnable function = () -> {
            ExceptionDialog dialog = new ExceptionDialog(parent, exception);
            dialog.setVisible(true);
        };
        
        if (SwingUtilities.isEventDispatchThread()) {
            function.run();
        } else {
            SwingUtilities.invokeLater(function);
        }
    }
    
}

package net.ssehub.sparkyservice.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import io.gsonfire.GsonFireBuilder;
import net.ssehub.studentmgmt.sparkyservice_api.JSON;

public class LoginDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 5885623597033543895L;
    
    private static final File SETTINGS_FILE = new File("settings.json");
    
    private static final JSON JSON;
    
    static {
        JSON = new JSON();
        Gson gson = new GsonFireBuilder().createGsonBuilder()
                .setPrettyPrinting()
                .create();
        JSON.setGson(gson);
    }

    private JComboBox<String> apiUrl;
    
    private JTextField username;
    
    private JPasswordField password;
    
    public LoginDialog(JFrame parent) {
        super(parent);
        setModal(true);
        setTitle("Login");
        
        LoginSettings settings = loadLoginSettings();
        
        JPanel content = new JPanel(new GridLayout(0, 2, 5, 5));
        
        content.add(new JLabel("API URL:", SwingConstants.TRAILING));
        
        this.apiUrl = new JComboBox<>(settings.getKnownServers());
        this.apiUrl.setEditable(true);
        content.add(this.apiUrl);
        
        content.add(new JLabel("Username:", SwingConstants.TRAILING));
        
        this.username = new JTextField(settings.getUsername(), 10);
        this.username.addActionListener(this);
        content.add(this.username);
        
        content.add(new JLabel("Password:", SwingConstants.TRAILING));
        
        this.password = new JPasswordField(10);
        this.password.addActionListener(this);
        content.add(this.password);
        
        content.add(new JLabel());
        
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(this);
        content.add(okButton);
        
        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
            
        });
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            SwingUtilities.invokeLater(() -> this.password.requestFocusInWindow());
        }
    }
    
    public String getApiUrl() {
        String url = apiUrl.getSelectedItem().toString();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    public String getUsername() {
        return username.getText();
    }
    
    
    public char[] getPassword() {
        return password.getPassword();
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        saveSettings();
        setVisible(false);
    }
    
    private static class LoginSettings {
        
        private String[] knownServers = new String[0];
        
        private String username = "";
        
        public String[] getKnownServers() {
            return knownServers;
        }
        
        public void setKnownServers(String[] knownServers) {
            this.knownServers = knownServers;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
    }
    
    private static LoginSettings loadLoginSettings() {
        LoginSettings result;
        try {
            result = JSON.deserialize(FileUtils.readFileToString(SETTINGS_FILE, StandardCharsets.UTF_8), LoginSettings.class);
        } catch (IOException e) {
            result = new LoginSettings();
        }
        return result;
    }
    
    private void saveSettings() {
        LoginSettings settings = new LoginSettings();
        
        List<String> knownServers = new LinkedList<>();
        for (int i = 0; i < apiUrl.getItemCount(); i++) {
            knownServers.add(apiUrl.getItemAt(i));
        }
        // make sure that selected field is added and the first in the list
        String selected = apiUrl.getSelectedItem().toString();
        knownServers.remove(selected);
        knownServers.add(0, selected);
        
        settings.setKnownServers(knownServers.toArray(new String[knownServers.size()]));
        settings.setUsername(username.getText());
        
        try {
            FileUtils.writeStringToFile(SETTINGS_FILE, JSON.serialize(settings) + "\n", StandardCharsets.UTF_8);
        } catch (IOException e) {
            ExceptionDialog.showExceptionDialog(e, this);
        }
        
    }
    
}

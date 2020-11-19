package net.ssehub.sparkyservice.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.threeten.bp.LocalDate;

import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto.RealmEnum;
import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto.RoleEnum;

public class UserDialog extends JDialog {

    private static final long serialVersionUID = 6777419097542157334L;

    private static final int FIELD_WIDTH = 20;
    
    private boolean submitted;
    
    private JTextField username;
    
    private JTextField fullName;
    
    private JPasswordField password;
    
    private JTextField email;
    
    private JTextField expirationDate;
    
    private JComboBox<RoleEnum> role;
    
    public UserDialog(JFrame parent, UserDto user) {
        super(parent);
        setModal(true);
        setTitle("Create New User");
        
        JPanel content = new JPanel(new GridBagLayout());
        
        GridBagConstraints position = new GridBagConstraints();
        position.insets = new Insets(2, 2, 2, 2);
        position.fill = GridBagConstraints.HORIZONTAL;
        position.gridy = 0;
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        content.add(new JLabel("Username:"), position);
        
        this.username = new JTextField(FIELD_WIDTH);
        if (user != null) {
            this.username.setText(user.getUsername());
            this.username.setEditable(false);
            this.username.setToolTipText("Username of existing user cannot be modified");
        }
        position.anchor = GridBagConstraints.CENTER;
        content.add(this.username, position);
        
        position.gridy++;
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        content.add(new JLabel("Full Name:"), position);
        
        this.fullName = new JTextField(FIELD_WIDTH);
        if (user != null) {
            this.fullName.setText(user.getFullName());
        }
        position.anchor = GridBagConstraints.CENTER;
        content.add(this.fullName, position);
        
        position.gridy++;
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        content.add(new JLabel("Password:"), position);
        
        this.password = new JPasswordField(FIELD_WIDTH);
        this.password.setToolTipText("Overrides existing password if not empty");
        if (user != null && user.getRealm() == RealmEnum.LDAP) {
            this.password.setEditable(false);
            this.password.setToolTipText("Password of users in " + RealmEnum.LDAP.getValue() + " realm cannot be modified");
        }
        position.anchor = GridBagConstraints.CENTER;
        content.add(this.password, position);
        
        position.gridy++;
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        content.add(new JLabel("E-Mail:"), position);
        
        this.email = new JTextField(FIELD_WIDTH);
        if (user != null) {
            this.email.setText(user.getSettings().getEmailAddress());
        }
        position.anchor = GridBagConstraints.CENTER;
        content.add(this.email, position);
        
        position.gridy++;
        
        position.anchor = GridBagConstraints.CENTER;
        content.add(new JLabel("Expiration Date"), position);
        
        this.expirationDate = new JTextField(defaultExpirationDate(), FIELD_WIDTH);
        position.anchor = GridBagConstraints.CENTER;
        content.add(this.expirationDate, position);
        position.gridy++;
        
        if (expirationDate.getText().isEmpty()) {
            System.out.println("Leer");
        }
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        content.add(new JLabel("Role:"), position);
        
        this.role = new JComboBox<>(RoleEnum.values());
        if (user != null) {
            this.role.setSelectedItem(user.getRole());
        }
        position.anchor = GridBagConstraints.CENTER;
        content.add(this.role, position);
        
        position.gridy++;
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        content.add(new JLabel("Realm:"), position);
        
        JTextField realm = new JTextField(FIELD_WIDTH);
        realm.setEditable(false);
        if (user != null) {
            realm.setText(user.getRealm().getValue());
            realm.setToolTipText("Realm of existing user cannot be modified");
        } else {
            realm.setText(RealmEnum.LOCAL.getValue());
            realm.setToolTipText("New users can only be created in realm " + RealmEnum.LOCAL.getValue());
        }
        position.anchor = GridBagConstraints.CENTER;
        content.add(realm, position);
        
        position.gridy++;
        
        position.anchor = GridBagConstraints.BASELINE_TRAILING;
        position.gridx = 1;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 4, 4));
        content.add(buttonPanel, position);
        
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((event) -> {
            dispose();
        });
        buttonPanel.add(cancelButton);
        
        JButton okButton = new JButton("Ok");
        okButton.addActionListener((event) -> {
            int result = JOptionPane.YES_OPTION;
            if (user != null && getPassword() != null) {
                result = JOptionPane.showConfirmDialog(this, "Really override existing password?", "Confirm Password Override",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
            
            int correctExpirationDateFormat = JOptionPane.YES_OPTION;
            // check for correct date format
            if (expirationDate.getText().isEmpty() || !expirationDate.getText().matches("\\d{4}[-]\\d{2}[-]\\d{2}")) {
                correctExpirationDateFormat = JOptionPane.showConfirmDialog(this, 
                        "Uncorrect Date Format, expected Format is YYYY-MM-DD, set Expiration Date to end of semester?", 
                        "Wrong Date Format", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (correctExpirationDateFormat == JOptionPane.YES_OPTION) {
                    expirationDate.setText(defaultExpirationDate());
                }
            } else {
                // check if date exists
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
                format.setLenient(false);
                try {
                    format.parse(expirationDate.getText());
                } catch (ParseException e) {
                    correctExpirationDateFormat = JOptionPane.showConfirmDialog(this, 
                            "The Date " + expirationDate.getText() 
                            + " does not exist, instead set Expiration Date to end of semester?", 
                            "Not existing Date", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            }
            
            if (result == JOptionPane.YES_OPTION && correctExpirationDateFormat == JOptionPane.YES_OPTION) {
                submitted = true;
                dispose();
            }
        });
        buttonPanel.add(okButton);
        
        setContentPane(content);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public String getUsername() {
        return this.username.getText().trim();
    }
    
    public String getFullName() {
        String text = this.fullName.getText().trim();
        return text.isEmpty() ? null : text;
    }
    
    public char[] getPassword() {
        char[] password = this.password.getPassword();
        return password.length > 0 ? password : null;
    }
    
    public String getEmail() {
        String text = this.email.getText().trim();
        return text.isEmpty() ? null : text;
    }
    
    public LocalDate getExpirationDate() {
        LocalDate expirationDate = LocalDate.parse(this.expirationDate.getText());
        return expirationDate;
    }
    
    public RoleEnum getRole() {
        return (RoleEnum) this.role.getSelectedItem();
    }
    
    public boolean isSubmitted() {
        return this.submitted;
    }
    
    private String defaultExpirationDate() {
        String defaultExpirationDate;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        
        if (month >= 3 && month <= 8) {
            defaultExpirationDate = year + 1 + "-09-30";
        } else {
            defaultExpirationDate = year + 1 + "-03-31";
        }
        return defaultExpirationDate;
    }
    
}

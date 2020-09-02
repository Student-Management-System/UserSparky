package net.ssehub.sparkyservice.ui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto.RealmEnum;
import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto.RoleEnum;

public class UserDialog extends JDialog {

    private static final long serialVersionUID = 6777419097542157334L;

    private boolean submitted;
    
    private JTextField username;
    
    private JTextField fullName;
    
    private JPasswordField password;
    
    private JTextField email;
    
    private JComboBox<RoleEnum> role;
    
    public UserDialog(JFrame parent, UserDto user) {
        super(parent);
        setModal(true);
        setTitle("Create New User");
        
        JPanel content = new JPanel(new GridLayout(0, 2, 5, 5));
        
        content.add(new JLabel("Username:", SwingConstants.TRAILING));
        this.username = new JTextField(10);
        if (user != null) {
            this.username.setText(user.getUsername());
            this.username.setEditable(false);
        }
        content.add(this.username);
        
        content.add(new JLabel("Full Name:", SwingConstants.TRAILING));
        this.fullName = new JTextField(10);
        if (user != null) {
            this.fullName.setText(user.getFullName());
            if (user.getRealm() == RealmEnum.LDAP) {
                this.fullName.setEditable(false);
            }
        }
        content.add(this.fullName);
        
        content.add(new JLabel("Password:", SwingConstants.TRAILING));
        this.password = new JPasswordField(10);
        if (user != null && user.getRealm() == RealmEnum.LDAP) {
            this.password.setEditable(false);
        }
        content.add(this.password);
        
        content.add(new JLabel("E-Mail:", SwingConstants.TRAILING));
        this.email = new JTextField(10);
        if (user != null) {
            this.email.setText(user.getSettings().getEmailAddress());
            if (user.getRealm() == RealmEnum.LDAP) {
                this.email.setEditable(false);
            }
        }
        content.add(this.email);
        
        content.add(new JLabel("Role:", SwingConstants.TRAILING));
        this.role = new JComboBox<>(RoleEnum.values());
        if (user != null) {
            this.role.setSelectedItem(user.getRole());
        }
        content.add(this.role);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((event) -> {
            dispose();
        });
        content.add(cancelButton);
        
        JButton okButton = new JButton("Ok");
        okButton.addActionListener((event) -> {
            submitted = true;
            dispose();
        });
        content.add(okButton);
        
        setContentPane(content);
        pack();
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
    
    public RoleEnum getRole() {
        return (RoleEnum) this.role.getSelectedItem();
    }
    
    public boolean isSubmitted() {
        return this.submitted;
    }
    
}

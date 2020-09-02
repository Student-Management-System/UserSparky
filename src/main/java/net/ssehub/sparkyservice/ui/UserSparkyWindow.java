package net.ssehub.sparkyservice.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import net.ssehub.studentmgmt.sparkyservice_api.ApiClient;
import net.ssehub.studentmgmt.sparkyservice_api.ApiException;
import net.ssehub.studentmgmt.sparkyservice_api.api.AuthControllerApi;
import net.ssehub.studentmgmt.sparkyservice_api.api.UserControllerApi;
import net.ssehub.studentmgmt.sparkyservice_api.model.AuthenticationInfoDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.ChangePasswordDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.CredentialsDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.TokenDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto;

public class UserSparkyWindow extends JFrame {

    private static final long serialVersionUID = 4487251343400436988L;
    
    private static final String BASE_TITLE = "UserSparky";
    
    private UserControllerApi userApi;
    
    private JTable userTable;
    
    private UserTableModel userTableModel;
    
    public UserSparkyWindow() {
        ActionListener editUserAction = (event) -> {
            UserDto selectedUser = getSelectedUser();
            if (selectedUser != null) {
                editUser(selectedUser);
            }
        };
        ActionListener deleteUserAction = (event) -> {
            UserDto selectedUser = getSelectedUser();
            if (selectedUser != null) {
                deleteUser(selectedUser);
            }
        };
        
        JPopupMenu userTablePopup = new JPopupMenu();
        
        JMenuItem userTablePopupEditUser = new JMenuItem("Edit user");
        userTablePopupEditUser.addActionListener(editUserAction);
        userTablePopup.add(userTablePopupEditUser);
        
        JMenuItem userTablePopupDeleteUser = new JMenuItem("Delete user");
        userTablePopupDeleteUser.addActionListener(deleteUserAction);
        userTablePopup.add(userTablePopupDeleteUser);
        
        this.userTableModel = new UserTableModel();
        this.userTable = new JTable(this.userTableModel);
        
        this.userTable.setAutoCreateRowSorter(true);
        this.userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.userTable.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Point location = e.getPoint();
                    
                    int row = userTable.rowAtPoint(location);
                    int column = userTable.columnAtPoint(location);
                    
                    if (row != -1) {
                        userTable.changeSelection(row, column, false, false);
                        userTablePopup.show(userTable, e.getX(), e.getY());
                    } else {
                        userTable.clearSelection();
                    }
                }
            }
            
        });
        
        JScrollPane tableScrollPane = new JScrollPane(userTable);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton newUserButton = new JButton("Create new local user");
        controlPanel.add(newUserButton);
        newUserButton.addActionListener((event) -> {
            createNewLocalUser();
        });
        
        JButton editUserButton = new JButton("Edit selected user");
        controlPanel.add(editUserButton);
        editUserButton.addActionListener(editUserAction);
        
        JButton deleteUserButton = new JButton("Delete selected user");
        controlPanel.add(deleteUserButton);
        deleteUserButton.addActionListener(deleteUserAction);
        
        JButton reloadButton = new JButton("Reload user list");
        controlPanel.add(reloadButton);
        reloadButton.addActionListener((event) -> {
            reloadUserTable();
        });
        
        JPanel content = new JPanel(new BorderLayout(5, 5));
        setContentPane(content);
        content.add(tableScrollPane, BorderLayout.CENTER);
        content.add(controlPanel, BorderLayout.SOUTH);
        
        setTitle(BASE_TITLE);
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private UserDto getSelectedUser() {
        UserDto result = null;
        if (userTable.getSelectedColumn() != -1) {
            int modelIndex = userTable.getRowSorter().convertRowIndexToModel(userTable.getSelectedRow());
            result = this.userTableModel.getUserInRow(modelIndex);
        }
        return result;
    }
    
    private void reloadUserTable() {
        this.userTableModel.clearUsers();
        
        try {
            for (UserDto user : userApi.getAllUsers()) {
                this.userTableModel.addUser(user);
            }
        } catch (ApiException e) {
            ExceptionDialog.showExceptionDialog(e, this);
        }
    }
    
    private void deleteUser(UserDto user) {
        int result = JOptionPane.showConfirmDialog(this, "Really delete user " + user.getUsername(), "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                userApi.deleteUser(user.getRealm().getValue(), user.getUsername());
            } catch (ApiException e) {
                ExceptionDialog.showExceptionDialog(e, this);
            }
            
            reloadUserTable();
        }
    }
    
    private void editUser(UserDto user) {
        UserDialog userDialog = new UserDialog(this, user);
        userDialog.setVisible(true);
        
        if (!userDialog.isSubmitted()) {
            return;
        }

        user.setUsername(userDialog.getUsername());
        
        if (userDialog.getFullName() != null) {
            user.setFullName(userDialog.getFullName());
        }
        
        if (userDialog.getPassword() != null) {
            ChangePasswordDto changePassword = new ChangePasswordDto();
            changePassword.setNewPassword(new String(userDialog.getPassword()));
            user.setPasswordDto(changePassword);
        }
        
        if (userDialog.getEmail() != null) {
            user.getSettings().setEmailAddress(userDialog.getEmail());
        }
        
        user.setRole(userDialog.getRole());
        
        try {
            userApi.editUser(user);
            
        } catch (ApiException e) {
            ExceptionDialog.showExceptionDialog(e, this);
        }
        
        reloadUserTable();
    }
    
    private void createNewLocalUser() {
        UserDialog userDialog = new UserDialog(this, null);
        userDialog.setVisible(true);
        
        if (!userDialog.isSubmitted()) {
            return;
        }
        
        try {
            UserDto user = userApi.addLocalUser(userDialog.getUsername());
            
            if (userDialog.getFullName() != null) {
                user.setFullName(userDialog.getFullName());
            }
            
            if (userDialog.getPassword() != null) {
                ChangePasswordDto changePassword = new ChangePasswordDto();
                changePassword.setNewPassword(new String(userDialog.getPassword()));
                user.setPasswordDto(changePassword);
            }
            
            if (userDialog.getEmail() != null) {
                user.getSettings().setEmailAddress(userDialog.getEmail());
            }
            
            user.setRole(userDialog.getRole());
            
            userApi.editUser(user);
            
        } catch (ApiException e) {
            ExceptionDialog.showExceptionDialog(e, this);
        }
        
        reloadUserTable();
    }
    
    public boolean login() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(loginDialog.getApiUrl());
        AuthControllerApi authApi = new AuthControllerApi(apiClient);
        this.userApi = new UserControllerApi(apiClient);
        
        CredentialsDto credentials = new CredentialsDto();
        credentials.setUsername(loginDialog.getUsername());
        credentials.setPassword(new String(loginDialog.getPassword()));
        
        boolean success = false;
        
        try {
            AuthenticationInfoDto authResult = authApi.authenticate(credentials);
            
            TokenDto token = authResult.getToken();
            apiClient.setAccessToken(token.getToken());
            
            reloadUserTable();
            setTitle(BASE_TITLE + " - " + loginDialog.getApiUrl());
            success = true;
        } catch (ApiException | IllegalArgumentException e) {
            ExceptionDialog.showExceptionDialog(e, this);
        }
        
        return success;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserSparkyWindow window = new UserSparkyWindow();
            window.setVisible(true);
            
            boolean success;
            do {
                success = window.login();
            } while (!success);
        });
    }
    
}

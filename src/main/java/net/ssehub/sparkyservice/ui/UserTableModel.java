package net.ssehub.sparkyservice.ui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.ssehub.studentmgmt.sparkyservice_api.model.UserDto;

class UserTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = -3498830195696647078L;

    public enum Column {
        
        USERNAME(0, "Username"),
        FULL_NAME(1, "Full Name"),
        EMAIL(2, "E-Mail"),
        ROLE(3, "Role"),
        REALM(4, "Realm"),
        EXPIRATION_DATE(5, "Expiration Date");
        
        public static final int MAX_INDEX = 5;
        
        private int index;
        
        private String name;
        
        private Column(int index, String name) {
            this.index = index;
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public int getIndex() {
            return index;
        }
        
        public static Column getByIndex(int index) {
            Column column = null;
            for (Column element : values()) {
                if (element.index == index) {
                    column = element;
                    break;
                }
            }
            return column;
        }
        
    }
    
    private List<UserDto> users = new LinkedList<>();
    
    private boolean isValidRow(int rowIndex) {
        return rowIndex >= 0 && rowIndex < users.size();
    }
    
    private boolean isValidColumn(int columnIndex) {
        return columnIndex >= 0 && columnIndex <= Column.MAX_INDEX;
    }
    
    public void addUser(UserDto user) {
        if (users.add(user)) {
            fireTableRowsInserted(users.size() - 1, users.size() - 1);
        }
    }
    
    public void clearUsers() {
        if (!users.isEmpty()) {
            int oldSize = users.size();
            users.clear();
            fireTableRowsDeleted(0, oldSize -1);
        }
    }
    
    public UserDto getUserInRow(int rowIndex) {
        return isValidRow(rowIndex) ? users.get(rowIndex) : null;
    }
    
    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }
    
    @Override
    public Class<String> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return isValidColumn(columnIndex) ? Column.getByIndex(columnIndex).getName() : null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String result = null;
        
        if (isValidRow(rowIndex) && isValidColumn(columnIndex)) {
            UserDto user = users.get(rowIndex);
            switch (Column.getByIndex(columnIndex)) {
            case USERNAME:
                result = user.getUsername();
                break;
            case FULL_NAME:
                result = user.getFullName();
                break;
            case EMAIL:
                result = user.getSettings().getEmailAddress();
                break;
            case ROLE:
                if (user.getRole() != null) {
                    result = user.getRole().getValue();
                }
                break;
            case REALM:
                result = user.getRealm().getValue();
                break;
            case EXPIRATION_DATE:
                if (user.getExpirationDate() != null) {
                    result = user.getExpirationDate().getDayOfMonth() + "." + user.getExpirationDate().getMonthValue() 
                            + "." + user.getExpirationDate().getYear();
                }
            }
        }
        
        return result;
    }
    
}

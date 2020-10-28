package net.ssehub.sparkyservice.ui;

import javax.swing.RowFilter;

import net.ssehub.sparkyservice.ui.UserTableModel.Column;

class SubstringRowFilter extends RowFilter<UserTableModel, Integer> {

    private String substring;
    
    public SubstringRowFilter(String substring) {
        this.substring = substring.toLowerCase();
    }

    private boolean checkAtColumn(Column column, Entry<? extends UserTableModel, ? extends Integer> entry) {
        return entry.getStringValue(column.getIndex()).toLowerCase().contains(substring);
    }

    @Override
    public boolean include(Entry<? extends UserTableModel, ? extends Integer> entry) {
        return checkAtColumn(Column.USERNAME, entry)
                || checkAtColumn(Column.FULL_NAME, entry)
                || checkAtColumn(Column.EMAIL, entry);
    }
    
}

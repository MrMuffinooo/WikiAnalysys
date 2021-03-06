package app.main;

/*
 * Project: Gulden Utilies
 * Class:   de.gulden.util.swing.MapTableModel
 * Version: snapshot-beautyj-1.1
 *
 * Date:    2004-09-29
 *
 * This is a snapshot version of the Gulden Utilities,
 * it is not released as a seperate version.
 *
 * Note:    Contains auto-generated Javadoc comments created by BeautyJ.
 *
 * This is licensed under the GNU Lesser General Public License (LGPL)
 * and comes with NO WARRANTY.
 *
 * Author:  Jens Gulden
 * Email:   amoda@jensgulden.de
 */


import javax.swing.table.AbstractTableModel;
import java.util.Map;

/**
 * Class MapTableModel.
 *
 * @author Jens Gulden
 * @version snapshot-beautyj-1.1
 */
public class MapTableModel extends AbstractTableModel {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * The map.
     */
    protected Map map;

    /**
     * The column names array.
     */
    protected String[] columnNames;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance of MapTableModel.
     */
    public MapTableModel() {
        super();
    }

    /**
     * Creates a new instance of MapTableModel.
     */
    public MapTableModel(Map map) {
        this(map, "No", "Entry", "Value");
    }

    /**
     * Creates a new instance of MapTableModel.
     */
    public MapTableModel(Map map, String no, String keyName, String valueName) {
        this();
        setMap(map);
        setColumnNames(no, keyName, valueName);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the row count.
     */
    public int getRowCount() {
        return map.size();
    }

    /**
     * Returns the column count.
     */
    public int getColumnCount() {
        return 3;
    }

    /**
     * Returns the value at.
     */
    public Object getValueAt(int row, int column) {
        Object[] entries = map.entrySet().toArray();
        Map.Entry entry = (Map.Entry) entries[row];
        if (column == 0) {
            return row + 1;
        } else if (column == 1) {
            return entry.getKey().toString().replaceAll("_", " ");
        } else if (column == 2) {
            return entry.getValue();
        } else {
            throw new IndexOutOfBoundsException("MapTableModel provides a 2-column table, column-index " + column + " is illegal.");
        }
    }

    /**
     * Returns the column name.
     */
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Sets the column names.
     */
    public void setColumnNames(String no, String keyName, String valueName) {
        String[] names = {no, keyName, valueName};
        columnNames = names;
    }

    /**
     * Returns the map.
     */
    public Map getMap() {
        return map;
    }

    /**
     * Sets the map.
     */
    public void setMap(Map _map) {
        map = _map;
    }

} // end MapTableModel
package me.passivepicasso.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;

public class DatabaseManager {

    private static class InvalidUpdateDataException extends Exception {
        private String     message;
        private UpdateType updateType;

        public InvalidUpdateDataException( String message, UpdateType updateType ) {
            this.message = message;
            this.updateType = updateType;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @SuppressWarnings("unused")
        public String getUpdateType() {
            return updateType.toString();
        }
    }

    private static ODB             odb;
    private static DatabaseVersion dv = new DatabaseVersion();
    private static Logger          log;
    private static String          filename;
    private static String          pluginName;

    public static void initialize( String pluginName ) {
        DatabaseManager.pluginName = pluginName;
        filename = pluginName + ".neodatis";
        odb = ODBFactory.open("./plugins/" + pluginName + "/" + filename);
        store(dv);
    }

    public static void setLog( Logger log ) {
        DatabaseManager.log = log;
    }

    static {
        dv.setMajor(0);
        dv.setMinor(1);
        dv.setSub(3);
    }

    public static void close() {
        odb.close();
    }

    public static void open() {
        odb = ODBFactory.open("./plugins/" + pluginName + "/" + filename);
    }

    public static String getFilename() {
        return filename;
    }

    public static File getDatabaseFile() {
        return new File("." + File.pathSeparator + "plugins" + pluginName + File.pathSeparator + filename);
    }

    public static boolean store( Object object ) {
        if (odb.isClosed()) {
            return false;
        }
        log.log(Level.FINER, "[DATABASE] " + pluginName + ".neodatis has been opened.");
        odb.store(object);
        log.log(Level.FINER, "[DATABASE] " + pluginName + ".neodatis has been closed.");
        odb.commit();
        return true;
    }

    public static void store( Object... objects ) {
        for (Object o : objects) {
            store(o);
        }
        odb.commit();
    }

    public static void verifySchema() {
        Objects<DatabaseVersion> dvs = odb.getObjects(DatabaseVersion.class);
        DatabaseVersion stored = dvs.getFirst();
        for (DatabaseVersion dV : dvs) {
            if (dV.compareTo(stored) == 1) {
                stored = dV;
            }
        }
        int diff = dv.compareTo(stored);
        if (diff == -1) {
            log.log(Level.SEVERE, "Your database is a newer version than this pluging, you cannot use this version of the plugin with your current database.");
            log.log(Level.SEVERE, "Two historical versions are saved under your /plugins/CraftBox/dbHistory folder.");
        } else if (diff == 1) {
        }
    }

    public enum UpdateType {
        ADD_FIELD, CHANGE_FIELDTYPE, REMOVE_FIELD, REMOVE_CLASS, CHANGE_FIELDNAME, CHANGE_CLASSNAME
    };

    /**
     * Supply UpdateType.CHANGE_FIELDNAME for updateType to rename a field
     * 
     * @param <T>
     * @param className
     * @param fieldName
     * @param newFieldName
     * @param updateType
     * @throws InvalidUpdateDataException
     */
    public static void updateSchema( String className, String fieldName, String newFieldName, UpdateType updateType ) throws InvalidUpdateDataException {
        if (!UpdateType.CHANGE_FIELDNAME.equals(updateType)) {
            throw new InvalidUpdateDataException("Invalid data provided for UpdateType." + updateType.toString() + ".", updateType);
        }
        updateSchema(className, fieldName, null, updateType, newFieldName);
    }

    /**
     * Supply UpdateType.CHANGE_CLASSNAME for updateType to rename a class
     * 
     * @param className
     * @param newClassName
     * @param updateType
     * @throws InvalidUpdateDataException
     */
    public static void updateSchema( String className, String newClassName, UpdateType updateType ) throws InvalidUpdateDataException {
        if (!UpdateType.CHANGE_CLASSNAME.equals(updateType)) {
            throw new InvalidUpdateDataException("Invalid data provided for UpdateType." + updateType.toString() + ".", updateType);
        }
        updateSchema(className, null, null, updateType, newClassName);
    }

    /**
     * Supply UpdateType.ADD_FIELD for updateType to rename a class
     * Supply UpdateType.CHANGE_FIELDTYPE for updateType to change the class type of a field
     * Supply UpdateType.REMOVE_CLASS to remove a class
     * Supply UpdateType.REMOVE_FIELD to remove a field from a class
     * 
     * @param className
     * @param newClassName
     * @param updateType
     * @throws InvalidUpdateDataException
     */
    public static void updateSchema( String className, String fieldName, Class<?> newType, UpdateType updateType ) throws InvalidUpdateDataException {
        if (UpdateType.CHANGE_CLASSNAME.equals(updateType) || UpdateType.CHANGE_FIELDNAME.equals(updateType)) {
            throw new InvalidUpdateDataException("Invalid data provided for UpdateType." + updateType.toString() + ".", updateType);
        }
        updateSchema(className, fieldName, newType, updateType, null);
    }

    private static void updateSchema( String className, String fieldName, Class<?> clazz, UpdateType updateType, String newFieldName ) {
        try {
            switch (updateType) {
                case CHANGE_CLASSNAME:
                    odb.getRefactorManager().renameClass(className, newFieldName);
                    break;
                case ADD_FIELD:
                    odb.getRefactorManager().addField(className, clazz, fieldName);
                    break;
                case CHANGE_FIELDNAME:
                    odb.getRefactorManager().renameField(className, fieldName, newFieldName);
                    break;
                case CHANGE_FIELDTYPE:
                    odb.getRefactorManager().changeFieldType(className, fieldName, clazz);
                    break;
                case REMOVE_CLASS:
                    odb.getRefactorManager().removeClass(className);
                    break;
                case REMOVE_FIELD:
                    odb.getRefactorManager().removeField(className, fieldName);
                    break;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static <T> List<T> getList( Class<T> type ) {
        log.log(Level.FINER, "[DATABASE] " + pluginName + ".neodatis has been opened.");
        Objects<T> results = odb.getObjects(type);
        log.log(Level.FINER, "[DATABASE] " + pluginName + ".neodatis has been closed.");
        return new ArrayList<T>(results);
    }

    public static <T> Set<T> getSet( Class<T> type ) {
        log.log(Level.FINER, "[DATABASE] " + pluginName + ".neodatis has been opened.");
        Objects<T> results = odb.getObjects(type);
        log.log(Level.FINER, "[DATABASE] " + pluginName + ".neodatis has been closed.");
        return new HashSet<T>(results);
    }

}

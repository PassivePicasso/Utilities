package me.passivepicasso.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import me.passivepicasso.util.SchemaUpdate.UpdateType;
import me.passivepicasso.util.DatabaseVersion;
import me.passivepicasso.util.SchemaUpdate;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;

public class DatabaseManager {

    @SuppressWarnings("unused")
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

        public String getUpdateType() {
            return updateType.toString();
        }
    }

    private static ODB             odb;
    private static DatabaseVersion version;
    private static Logger          log = Logger.getLogger("pPDatabaseManager");
    private static String          filename, pluginName;

    public static int addAll( Object... objects ) {
        if (odb.isClosed()) {
            return 0;
        }
        int i = 0;
        for (Object o : objects) {
            if (store(o)) {
                i++;
            }
        }
        odb.commit();
        return i;
    }

    public static void close() {
        odb.close();
    }

    public static <T> void delete(T object){
        odb.delete(object);
    }

    public static File getDatabaseFile() {
        return new File("." + File.pathSeparator + "plugins" + pluginName + File.pathSeparator + filename);
    }

    public static String getFilename() {
        return filename;
    }

    public static <T> List<T> getList( Class<T> type ) {
        log.finer("[DATABASE] " + pluginName + ".neodatis has been opened.");
        Objects<T> results = odb.getObjects(type);
        log.finer("[DATABASE] " + pluginName + ".neodatis has been closed.");
        return new ArrayList<T>(results);
    }

    public static <T> Set<T> getSet( Class<T> type ) {
        log.finer("[DATABASE] " + pluginName + ".neodatis has been opened.");
        Objects<T> results = odb.getObjects(type);
        log.finer("[DATABASE] " + pluginName + ".neodatis has been closed.");
        return new HashSet<T>(results);
    }

    /**
     * Initialize and open the database.
     *
     * @param pluginName
     */
    public static void initialize( String pluginName, DatabaseVersion version ) throws NullPointerException {
        log.info("Beginning initialization");
        DatabaseManager.pluginName = pluginName;
        DatabaseManager.filename = pluginName + ".neodatis";

        OdbConfiguration.setReconnectObjectsToSession(true);
        OdbConfiguration.setDebugEnabled(true);

        odb = ODBFactory.open("./plugins/" + pluginName + "/" + filename);
        log.info("Database Open");

        DatabaseManager.version = version;

        Objects<DatabaseVersion> versions = odb.getObjects(DatabaseVersion.class);

        int oldVersionCount = 0;
        boolean foundExpectedVersion = false;

        if (versions.size() == 0) {
            log.info("No existing database found, initializing new database.");
            store(DatabaseManager.version);
            odb.commit();
        } else {
            while (versions.hasNext()) {
                DatabaseVersion current = versions.next();
                int result = DatabaseManager.version.compareTo(current);
                if (result == 1) {
                    oldVersionCount++;
                } else if (result == 0) {
                    foundExpectedVersion = true;
                }
            }
            if (oldVersionCount == versions.size()) {
                log.info("[DATABASE] " + pluginName + ".neodatis older than the version this plugin is looking for.");
                log.info("[DATABASE] Checking for SchemaUpdate Database...");
                // TODO Update Database Schema
                log.info("[DATABASE] No SchemaUpdate Database found.");
            } else if ((oldVersionCount == versions.size() - 1) && foundExpectedVersion) {
                log.info("[DATABASE] Database version verified.");
            } else if (oldVersionCount == versions.size() - 2) {
                log.info("[DATABASE] " + pluginName + ".neodatis newer than the version this plugin is looking for.");
            }
        }
    }

    public static void open() {
        odb = ODBFactory.open("./plugins/" + pluginName + "/" + filename);
    }

    public static boolean store( Object object ) {
        if (odb.isClosed()) {
            return false;
        }
        log.finer("[DATABASE] " + pluginName + ".neodatis has been opened.");
        odb.store(object);
        log.finer("[DATABASE] " + pluginName + ".neodatis has been closed.");
        odb.commit();
        return true;
    }

    @SuppressWarnings("unused")
    private static void updateSchema( SchemaUpdate update ) {
        try {
            switch (update.getUpdateType()) {
                case CHANGE_CLASSNAME:
                    odb.getRefactorManager().renameClass(update.getClassName(), update.getNewName());
                    break;
                case ADD_FIELD:
                    odb.getRefactorManager().addField(update.getClassName(), update.getClazz(), update.getFieldName());
                    break;
                case CHANGE_FIELDNAME:
                    odb.getRefactorManager().renameField(update.getClassName(), update.getFieldName(), update.getNewName());
                    break;
                case CHANGE_FIELDTYPE:
                    odb.getRefactorManager().changeFieldType(update.getClassName(), update.getFieldName(), update.getClazz());
                    break;
                case REMOVE_CLASS:
                    odb.getRefactorManager().removeClass(update.getClassName());
                    break;
                case REMOVE_FIELD:
                    odb.getRefactorManager().removeField(update.getClassName(), update.getFieldName());
                    break;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}

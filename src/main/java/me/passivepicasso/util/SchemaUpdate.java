package me.passivepicasso.util;

public class SchemaUpdate {
    public enum UpdateType {
        ADD_FIELD, CHANGE_FIELDTYPE, REMOVE_FIELD, REMOVE_CLASS, CHANGE_FIELDNAME, CHANGE_CLASSNAME
    }

    private String     className, fieldName, newName;
    private Class<?>   clazz;
    private UpdateType updateType;

    public SchemaUpdate() {
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @return the updateType
     */
    public UpdateType getUpdateType() {
        return updateType;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName( String className ) {
        this.className = className;
    }

    /**
     * @param clazz
     *            the clazz to set
     */
    public void setClazz( Class<?> clazz ) {
        this.clazz = clazz;
    }

    /**
     * @param fieldName
     *            the fieldName to set
     */
    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    /**
     * @param newName
     *            the newName to set
     */
    public void setNewName( String newName ) {
        this.newName = newName;
    }

    /**
     * @param updateType
     *            the updateType to set
     */
    public void setUpdateType( UpdateType updateType ) {
        this.updateType = updateType;
    }

}

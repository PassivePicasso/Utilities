package me.passivepicasso.util;

public class SchemaUpdate {
    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    private String   className;
    private String   fieldName;
    private Class<?> clazz;

    public SchemaUpdate( String className, String fieldName, Class<?> clazz ) {
        this.className = className;
        this.fieldName = fieldName;
        this.clazz = clazz;
    }

}

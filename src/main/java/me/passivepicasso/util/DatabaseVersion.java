package me.passivepicasso.util;

public class DatabaseVersion {
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + sub;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatabaseVersion other = (DatabaseVersion) obj;
        if (major != other.major) {
            return false;
        }
        if (minor != other.minor) {
            return false;
        }
        if (sub != other.sub) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param other
     * @return
     *         -1 if other is newer
     *         0 if the version is the same
     *         1 if this is newer
     */
    public int compareTo( DatabaseVersion other ) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.major == other.major) {
            if (this.minor == other.minor) {
                if (this.sub < other.sub) {
                    return -1;
                }
                if (this.sub > other.sub) {
                    return 1;
                }
            } else if (this.minor < other.minor) {
                return -1;
            } else if (this.minor > other.minor) {
                return 1;
            }
        } else if (this.major > other.major) {
            return 1;
        } else if (this.major < other.major) {
            return -1;
        }
        return -1;
    }

    /**
     * @return the major
     */
    public int getMajor() {
        return major;
    }

    /**
     * @param major
     *            the major to set
     */
    public void setMajor( int major ) {
        this.major = major;
    }

    /**
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @param minor
     *            the minor to set
     */
    public void setMinor( int minor ) {
        this.minor = minor;
    }

    /**
     * @return the sub
     */
    public int getSub() {
        return sub;
    }

    /**
     * @param sub
     *            the sub to set
     */
    public void setSub( int sub ) {
        this.sub = sub;
    }

    private int major;
    private int minor;
    private int sub;
}

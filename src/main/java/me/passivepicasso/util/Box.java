package me.passivepicasso.util;

import java.util.HashSet;

public class Box {

    private int x;
    private int y;
    private int z;
    private int width, height, length;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;

        Box box = (Box) o;

        if (height != box.height) return false;
        if (length != box.length) return false;
        if (width != box.width) return false;
        if (x != box.x) return false;
        return y == box.y && z == box.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + length;
        return result;
    }

    public Box() {

    }

    public void setZ(int z) {
        this.z = z;
    }

    public HashSet<Box> getRoomParts() {
        HashSet<Box> parts = new HashSet<Box>();
        parts.add(this);
        parts.addAll(intersectingBoxes);
        return parts;
    }

    public void setIntersectingBoxes(HashSet<Box> intersectingBoxes) {
        this.intersectingBoxes = intersectingBoxes;
    }

    /**
     * x y and z represent the minimum corner of the box
     *
     * @param x
     * @param y
     * @param z
     * @param height
     * @param width
     * @param length
     */
    @SuppressWarnings({"JavaDoc"})
    public Box(int x, int y, int z, int height, int width, int length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public int getMinZ() {
        return z;
    }

    public int getMaxZ() {
        return z + length;
    }

    public int getMinY() {
        return y;
    }

    public int getMaxY() {
        return y + height;
    }

    public int getMinX() {
        return x;
    }

    public int getMaxX() {
        return x + width;
    }

    public boolean isOrphan() {
        return intersectingBoxes.isEmpty();
    }

    HashSet<Box> intersectingBoxes = new HashSet<Box>();

    public boolean intersects(Box box) {
        if (intersectingBoxes.contains(box)) {
            return true;
        }

        if (getMaxX() >= box.x && x <= box.getMaxX()) {
            if (getMaxZ() >= box.z && z <= box.getMaxZ()) {
                if (getMaxY() >= box.y && y <= box.getMaxY()) {
                    intersectingBoxes.add(box);
                    return true;
                }
            }
        }
        return false;
    }
}

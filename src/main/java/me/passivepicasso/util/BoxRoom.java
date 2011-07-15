package me.passivepicasso.util;

import java.util.HashSet;

public class BoxRoom {

    private int x;
    private int y;
    private int z;
    private int width, height, length;

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    private boolean lit = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoxRoom)) {
            return false;
        }

        BoxRoom boxRoom = (BoxRoom) o;

        if (height != boxRoom.height) {
            return false;
        }
        if (length != boxRoom.length) {
            return false;
        }
        if (width != boxRoom.width) {
            return false;
        }
        if (x != boxRoom.x) {
            return false;
        }
        return y == boxRoom.y && z == boxRoom.z;
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

    public BoxRoom() {

    }

    public void setZ(int z) {
        this.z = z;
    }

    public HashSet<BoxRoom> getRoomParts() {
        HashSet<BoxRoom> parts = new HashSet<BoxRoom>();
        parts.add(this);
        parts.addAll(intersectingBoxRooms);
        return parts;
    }

    private void setIntersectingBoxRooms(HashSet<BoxRoom> intersectingBoxRooms) {
        this.intersectingBoxRooms = intersectingBoxRooms;
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
    public BoxRoom(int x, int y, int z, int height, int width, int length) {
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
        return intersectingBoxRooms.isEmpty();
    }

    HashSet<BoxRoom> intersectingBoxRooms = new HashSet<BoxRoom>();

    public boolean intersects(BoxRoom boxRoom) {
        if (intersectingBoxRooms.contains(boxRoom)) {
            return true;
        }

        if ((getMaxX() >= boxRoom.x && getMaxX() <= boxRoom.getMaxX()) || (x >= boxRoom.x && x <= boxRoom.getMaxX())) {
            if ((getMaxY() >= boxRoom.y && getMaxY() <= boxRoom.getMaxY()) || (y >= boxRoom.y && y <= boxRoom.getMaxY())) {
                if ((getMaxZ() >= boxRoom.z && getMaxZ() <= boxRoom.getMaxZ()) || (z >= boxRoom.z && z <= boxRoom.getMaxZ())) {
                    intersectingBoxRooms.addAll(boxRoom.getRoomParts());
                    boxRoom.intersectingBoxRooms.addAll(this.getRoomParts());
                    return true;
                }
            }
        }
        return false;
    }
}

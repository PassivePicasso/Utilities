package me.passivepicasso.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.*;

import static ch.lambdaj.Lambda.filter;

/**
 * Warning, you must dispose this class in order to prevent a memory leak.
 * use BlockMatrixNode.dispose()
 *
 * @author Tobias
 */
public class BlockMatrixNode {
    public enum Axis {
        X, Y, Z
    }

    // X, Y, Z
    private final HashMap<Block, BlockMatrixNode> matrixNodes;
    private final int x;
    private final int y;
    private final int z;
    private final Block block;
    private BlockMatrixNode nextX;
    private BlockMatrixNode previousX;
    private BlockMatrixNode nextY;
    private BlockMatrixNode previousY;
    private BlockMatrixNode nextZ;
    private BlockMatrixNode previousZ;
    private boolean isComplete;
    private Set<Integer> filter;

    public BlockMatrixNode(Block block, HashMap<Block, BlockMatrixNode> matrixNodes) {
        this.matrixNodes = matrixNodes;
        isComplete = false;
        this.block = block;
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        if (!matrixNodes.containsKey(block)) {
            matrixNodes.put(block, this);
            if (this.matrixNodes.containsKey(block.getRelative(1, 0, 0))) {
                BlockMatrixNode nextX = this.matrixNodes.get(block.getRelative(1, 0, 0));
                setSouth(nextX);
                nextX.setNorth(this);
            }
            if (this.matrixNodes.containsKey(block.getRelative(-1, 0, 0))) {
                BlockMatrixNode previousX = this.matrixNodes.get(block.getRelative(-1, 0, 0));
                setNorth(previousX);
                previousX.setSouth(this);
            }
            if (this.matrixNodes.containsKey(block.getRelative(0, 1, 0))) {
                BlockMatrixNode nextY = this.matrixNodes.get(block.getRelative(0, 1, 0));
                setUp(nextY);
                nextY.setDown(this);
            }
            if (this.matrixNodes.containsKey(block.getRelative(0, -1, 0))) {
                BlockMatrixNode previousY = this.matrixNodes.get(block.getRelative(0, -1, 0));
                setDown(previousY);
                previousY.setUp(this);
            }
            if (this.matrixNodes.containsKey(block.getRelative(0, 0, 1))) {
                BlockMatrixNode nextZ = this.matrixNodes.get(block.getRelative(0, 0, 1));
                setWest(nextZ);
                nextZ.setEast(this);
            }
            if (this.matrixNodes.containsKey(block.getRelative(0, 0, -1))) {
                BlockMatrixNode previousZ = this.matrixNodes.get(block.getRelative(0, 0, -1));
                setEast(previousZ);
                previousZ.setWest(this);
            }
        } else {
            BlockMatrixNode existing = matrixNodes.get(block);
            try {
                for (Field field : BlockMatrixNode.class.getFields()) {
                    field.set(this, field.get(existing));
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addDown() {
        if (previousY == null) {
            if (filter != null && filter.contains(block.getFace(BlockFace.DOWN).getTypeId())) {
                new BlockMatrixNode(block.getFace(BlockFace.DOWN), matrixNodes);
            } else if (filter == null) {
                new BlockMatrixNode(block.getFace(BlockFace.DOWN), matrixNodes);
            }
        }
        return previousY != null;
    }

    public boolean addEast() {
        if (previousZ == null) {
            if (filter != null && filter.contains(block.getFace(BlockFace.EAST).getTypeId())) {
                new BlockMatrixNode(block.getFace(BlockFace.EAST), matrixNodes);
            } else if (filter == null) {
                new BlockMatrixNode(block.getFace(BlockFace.EAST), matrixNodes);
            }
        }
        return previousZ != null;
    }

    public boolean addNorth() {
        if (nextX == null) {
            if (filter != null && filter.contains(block.getFace(BlockFace.NORTH).getTypeId())) {
                new BlockMatrixNode(block.getFace(BlockFace.NORTH), matrixNodes);
            } else if (filter == null) {
                new BlockMatrixNode(block.getFace(BlockFace.NORTH), matrixNodes);
            }
        }
        return nextX != null;
    }

    public boolean addSouth() {
        if (nextX == null) {
            if (filter != null && filter.contains(block.getFace(BlockFace.SOUTH).getTypeId())) {
                new BlockMatrixNode(block.getFace(BlockFace.SOUTH), matrixNodes);
            } else if (filter == null) {
                new BlockMatrixNode(block.getFace(BlockFace.SOUTH), matrixNodes);
            }
        }
        return nextX != null;
    }

    public boolean addUp() {
        if (nextY == null) {
            if (filter != null && filter.contains(block.getFace(BlockFace.UP).getTypeId())) {
                new BlockMatrixNode(block.getFace(BlockFace.UP), matrixNodes);
            } else if (filter == null) {
                new BlockMatrixNode(block.getFace(BlockFace.UP), matrixNodes);
            }
        }
        return nextY != null;
    }

    public boolean addWest() {
        if (nextZ == null) {
            if (filter != null && filter.contains(block.getFace(BlockFace.WEST).getTypeId())) {
                new BlockMatrixNode(block.getFace(BlockFace.WEST), matrixNodes);
            } else if (filter == null) {
                new BlockMatrixNode(block.getFace(BlockFace.WEST), matrixNodes);
            }
        }
        return nextZ != null;
    }

    public void complete() {
        isComplete = true;
        if (hasNorth() && !previousX.isComplete) {
            previousX.complete();
        }
        if (hasEast() && !previousZ.isComplete) {
            previousZ.complete();
        }
        if (hasSouth() && !nextX.isComplete) {
            nextX.complete();
        }
        if (hasWest() && !nextZ.isComplete) {
            nextZ.complete();
        }
        if (hasDown() && !previousY.isComplete) {
            previousY.complete();
        }
        if (hasUp() && !nextY.isComplete) {
            nextY.complete();
        }
    }

    public void dispose() {
        if (!matrixNodes.isEmpty()) {
            matrixNodes.clear();
        }
        if (hasNorth()) {
            previousX.dispose();
        }
        if (hasEast()) {
            previousZ.dispose();
        }
        if (hasSouth()) {
            nextX.dispose();
        }
        if (hasWest()) {
            nextZ.dispose();
        }
        if (hasNorth()) {
            setNorth(null);
        }
        if (hasEast()) {
            setEast(null);
        }
        if (hasSouth()) {
            setSouth(null);
        }
        if (hasWest()) {
            setWest(null);
        }
        if (hasUp()) {
            setUp(null);
        }
        if (hasDown()) {
            setDown(null);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BlockMatrixNode other = (BlockMatrixNode) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    public boolean equalsX(int x) {
        return this.x == x;
    }

    public boolean equalsY(int y) {
        return this.y == y;
    }

    public boolean equalsZ(int z) {
        return this.z == z;
    }

    public void fillRadius(int range) {
        if (range == 0) {
            return;
        }
        if (nextY == null) {
            if (addUp()) {
                if (nextY != null && distanceSquared(nextY.getBlock()) < range * range) {
                    fillRadius(range - 1);
                }
            }
        }
        if (previousY == null) {
            if (addDown()) {
                if (previousY != null && distanceSquared(previousY.getBlock()) < range * range) {
                    fillRadius(range - 1);
                }
            }
        }
        if (previousX == null) {
            if (addNorth()) {
                if (previousX != null && distanceSquared(previousX.getBlock()) < range * range) {
                    fillRadius(range - 1);
                }
            }
        }
        if (previousZ == null) {
            if (addEast()) {
                if (previousZ != null && distanceSquared(previousZ.getBlock()) < range * range) {
                    fillRadius(range - 1);
                }
            }
        }
        if (nextX == null) {
            if (addSouth()) {
                if (nextX != null && distanceSquared(nextX.getBlock()) < range * range) {
                    fillRadius(range - 1);
                }
            }
        }
        if (nextZ == null) {
            if (addWest()) {
                if (nextZ != null && distanceSquared(nextZ.getBlock()) < range * range) {
                    fillRadius(range - 1);
                }
            }
        }
    }

    public void floodFill() {
        floodFill(this);
    }

    public void floodFill(BlockMatrixNode next) {
        if (next == null) {
            return;
        }
        if (next.nextY == null && next.addUp()) {
            next.floodFill(nextY);
        }
        if (next.previousY == null && next.addDown()) {
            next.floodFill(previousY);
        }
        if (next.previousZ == null && next.addEast()) {
            next.floodFill(previousZ);
        }
        if (next.nextZ == null && next.addWest()) {
            next.floodFill(nextZ);
        }
        if (next.previousX == null && next.addNorth()) {
            next.floodFill(previousX);
        }
        if (next.nextX == null && next.addSouth()) {
            next.floodFill(nextX);
        }
    }


    public Block getBlock() {
        return block.getWorld().getBlockAt(x, y, z);
    }

    public ArrayList<Block> getBlockMatrix() {
        return new ArrayList<Block>(matrixNodes.keySet());
    }

    public HashSet<BlockMatrixNode> getBlockMatrixNodes() {
        return new HashSet<BlockMatrixNode>(matrixNodes.values());
    }


    public HashMap<Block, BlockMatrixNode> getMatrixNodes() {
        return matrixNodes;
    }

    /**
     * @param axis  either X or Y
     * @param value either Y or Z
     * @return set of block long the given plane
     */
    public Set<BlockMatrixNode> getBlockPlane(final Axis axis, final int value) {
        Matcher<BlockMatrixNode> onAxis = new BaseMatcher<BlockMatrixNode>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            public boolean matches(Object item) {
                if (item.getClass().equals(BlockMatrixNode.class)) {
                    BlockMatrixNode node = (BlockMatrixNode) item;
                    if (filter == null || filter.contains(node.getBlock().getTypeId())) {
                        switch (axis) {
                            case X:
                                return node.equalsX(value);
                            case Y:
                                return node.equalsY(value);
                            case Z:
                                return node.equalsZ(value);
                        }
                    }
                }
                return false;
            }
        };
        return new HashSet<BlockMatrixNode>(filter(onAxis, matrixNodes.values()));
    }

    public BlockMatrixNode getDown() {
        return previousY;
    }

    public BlockMatrixNode getEast() {
        return previousZ;
    }

    public Set<Material> getFilter() {
        if (filter == null) {
            return new HashSet<Material>();
        }
        HashSet<Material> result = new HashSet<Material>();
        for (int id : filter) {
            result.add(Material.getMaterial(id));
        }
        return result;
    }

    public HashSet<Block> getFilteredExternalAdjacentBlocks() {
        HashSet<Block> blocks = new HashSet<Block>();
        for (BlockFace face : EnumSet.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
            Block b = getBlock().getFace(face);
            if (filter.contains(b.getTypeId()) && !matrixNodes.containsKey(b) && !block.equals(b)) {
                blocks.add(b);
            }
        }
        return blocks;
    }

    public BlockMatrixNode getMatrixNode(Block block) {
        return matrixNodes.get(block);
    }

    public BlockMatrixNode getNorth() {
        return previousX;
    }

    public BlockMatrixNode getSouth() {
        return nextX;
    }

    public BlockMatrixNode getUp() {
        return nextY;
    }

    public BlockMatrixNode getWest() {
        return nextZ;
    }

    public boolean hasDown() {
        return previousY != null;
    }

    public boolean hasEast() {
        return previousZ != null;
    }

    public boolean hasFilteredDown() {
        return filter != null && previousY != null && filter.contains(previousY.getBlock().getTypeId());
    }

    public boolean hasFilteredEast() {
        return filter != null && previousZ != null && filter.contains(previousZ.getBlock().getTypeId());
    }

    public boolean hasFilteredNorth() {
        return filter != null && previousX != null && filter.contains(previousX.getBlock().getTypeId());
    }

    public boolean hasFilteredSouth() {
        return filter != null && nextX != null && filter.contains(nextX.getBlock().getTypeId());
    }

    public boolean hasFilteredUp() {
        return filter != null && nextY != null && filter.contains(nextY.getBlock().getTypeId());
    }

    public boolean hasFilteredWest() {
        return filter != null && nextZ != null && filter.contains(nextZ.getBlock().getTypeId());
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    public boolean hasNorth() {
        return previousX != null;
    }

    public boolean hasSouth() {
        return nextX != null;
    }

    public boolean hasUp() {
        return nextY != null;
    }

    public boolean hasWest() {
        return nextZ != null;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setDown(BlockMatrixNode previousY) {
        if (filter != null) {
            if (filter.contains(previousY.getBlock().getTypeId())) {
                this.previousY = previousY;
            }
        } else {
            this.previousY = previousY;
        }
    }

    public void setEast(BlockMatrixNode previousZ) {
        if (filter != null) {
            if (filter.contains(previousZ.getBlock().getTypeId())) {
                this.previousZ = previousZ;
            }
        } else {
            this.previousZ = previousZ;
        }
    }

    /**
     * Add an Inclusive Filter, using filtered retrievers will only find blocks of the Material types specified in the filter.
     *
     * @param propogate pass true to populate the filter on all nodes.
     * @param filter    set of Materials to include in all additions to the matrix.
     */
    public void setFilter(Set<Material> filter, boolean propogate) {
        if (this.filter == null) {
            this.filter = new HashSet<Integer>();
        }
        this.filter.clear();
        for (Material m : filter) {
            this.filter.add(m.getId());
        }
        if (propogate) {
            for (BlockMatrixNode node : matrixNodes.values()) {
                node.setFilter(filter, false);
            }
        }
    }

    /**
     * Merges this with matrix, all BlockMatrixNodes contained by matrix will be added to this.
     *
     * @param matrix the BlockMatrixNode to merge,
     * @return a BlockMatrixNode with a clone set of all intersecting nodes. null if there were no intersecting nodes
     */
    public BlockMatrixNode union(BlockMatrixNode matrix) {
        BlockMatrixNode intersectingBlockMatrixNode = null;
        HashMap<Block, BlockMatrixNode> intersectingBlocks = new HashMap<Block, BlockMatrixNode>();
        for (Block block : matrix.getBlockMatrix()) {
            if (!matrixNodes.containsKey(block)) {
                new BlockMatrixNode(block, matrixNodes);
            } else {
                if (intersectingBlockMatrixNode == null) {
                    intersectingBlockMatrixNode = new BlockMatrixNode(block, intersectingBlocks);
                } else {
                    new BlockMatrixNode(block, intersectingBlocks);
                }
            }
        }
        return intersectingBlockMatrixNode;
    }

    /**
     * Add an Inclusive Filter, using filtered retrievers will only find blocks of the Material types specified in the filter.
     * setFilter will set the filter of ALL NODES in the Matrix
     *
     * @param filter set of Materials to include in all additions to the matrix.
     */
    public void setFilter(Set<Material> filter) {
        setFilter(filter, true);
    }

    public void setNorth(BlockMatrixNode previousX) {
        if (filter != null) {
            if (filter.contains(previousX.getBlock().getTypeId())) {
                this.previousX = previousX;
            }
        } else {
            this.previousX = previousX;
        }
    }

    public void setSouth(BlockMatrixNode nextX) {
        if (filter != null) {
            if (filter.contains(nextX.getBlock().getTypeId())) {
                this.nextX = nextX;
            }
        } else {
            this.nextX = nextX;
        }
    }

    public void setUp(BlockMatrixNode nextY) {
        if (filter != null) {
            if (filter.contains(nextY.getBlock().getTypeId())) {
                this.nextY = nextY;
            }
        } else {
            this.nextY = nextY;
        }
    }

    public void setWest(BlockMatrixNode nextZ) {
        if (filter != null) {
            if (filter.contains(nextZ.getBlock().getTypeId())) {
                this.nextZ = nextZ;
            }
        } else {
            this.nextZ = nextZ;
        }
    }

    public int size() {
        return matrixNodes.size();
    }

    private double distanceSquared(Block target) {
        Location loc = getBlock().getLocation();
        Location tar = target.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        int xd = x - tar.getBlockX();
        int yd = y - tar.getBlockY();
        int zd = z - tar.getBlockZ();
        return xd * xd + yd * yd + zd * zd;
    }
}
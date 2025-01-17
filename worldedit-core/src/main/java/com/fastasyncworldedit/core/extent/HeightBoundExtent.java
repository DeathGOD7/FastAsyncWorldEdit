package com.fastasyncworldedit.core.extent;

import com.fastasyncworldedit.core.limit.FaweLimit;
import com.fastasyncworldedit.core.queue.IChunk;
import com.fastasyncworldedit.core.queue.IChunkGet;
import com.fastasyncworldedit.core.queue.IChunkSet;
import com.fastasyncworldedit.core.regions.RegionWrapper;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.regions.Region;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class HeightBoundExtent extends FaweRegionExtent {

    private final int min;
    private final int max;
    private int lastY = -1;
    private boolean lastResult;

    public HeightBoundExtent(Extent extent, FaweLimit limit, int min, int max) {
        super(extent, limit);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean contains(int x, int z) {
        return true;
    }

    @Override
    public boolean contains(int x, int y, int z) {
        if (y == lastY) {
            return lastResult;
        }
        lastY = y;
        return lastResult = (y >= min && y <= max);
    }

    @Override
    public Collection<Region> getRegions() {
        return Collections.singletonList(
                new RegionWrapper(Integer.MIN_VALUE, Integer.MAX_VALUE, min, max, Integer.MIN_VALUE,
                        Integer.MAX_VALUE
                ));
    }

    @Override
    public IChunkSet processSet(IChunk chunk, IChunkGet get, IChunkSet set) {
        if (trimY(set, min, max, true) | trimNBT(set, this::contains)) {
            return set;
        }
        return null;
    }

    @Override
    public Future<IChunkSet> postProcessSet(IChunk chunk, IChunkGet get, IChunkSet set) {
        return CompletableFuture.completedFuture(set);
    }

}

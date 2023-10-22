package com.lx862.jcm.mod.block.base;

import com.lx862.jcm.mod.block.behavior.VerticalMultiBlock;
import com.lx862.jcm.mod.data.BlockProperties;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public abstract class Vertical3Block extends DirectionalBlock implements VerticalMultiBlock {
    private static final int height = 3;
    public static final IntegerProperty PART = BlockProperties.VERTICAL_PART_3;

    public Vertical3Block(BlockSettings settings) {
        super(settings);
    }

    @Override
    public boolean canPlace(BlockState state, World world, BlockPos pos, ItemPlacementContext ctx) {
        return VerticalMultiBlock.canBePlaced(state, world, pos, ctx, height);
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        VerticalMultiBlock.placeBlock(world, pos, state, new Property<>(PART.data), height);
    }

    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if(VerticalMultiBlock.blockNotValid(world, pos, state, new Property<>(PART.data), height)) {
            return Blocks.getAirMapped().getDefaultState();
        }

        return super.getStateForNeighborUpdate2(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        super.addBlockProperties(properties);
        properties.add(PART);
    }
}

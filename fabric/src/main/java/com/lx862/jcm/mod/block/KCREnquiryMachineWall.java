package com.lx862.jcm.mod.block;

import com.lx862.jcm.mod.block.base.WallAttachedBlock;
import com.lx862.jcm.mod.block.behavior.EnquiryMachineBehavior;
import com.lx862.jcm.mod.data.EnquiryLog;
import com.lx862.jcm.mod.data.Entry;
import com.lx862.jcm.mod.network.gui.EnquiryUpdateGUIPacket;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.util.BlockUtil;
import com.lx862.jcm.mod.util.VoxelUtil;
import org.mtr.mapping.holder.*;
import org.mtr.mod.Init;

import java.util.List;

public class KCREnquiryMachineWall extends WallAttachedBlock implements EnquiryMachineBehavior {
    public KCREnquiryMachineWall(BlockSettings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelUtil.getDirectionalShape16(BlockUtil.getProperty(state, FACING), 3.5, 2.5, 0, 12.5, 13.5, 1);
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        super.onUse2(state, world, pos, player, hand, hit);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onServerUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        enquiry(world, player);
        List<Entry> entries = EnquiryLog.getEntries(player, player.getUuidAsString());
        Networking.sendPacketToClient(player, new EnquiryUpdateGUIPacket(entries));
    }
}

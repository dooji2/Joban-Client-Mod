package com.lx862.jcm.mod.network.block;

import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.util.BlockUtil;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.registry.PacketHandler;

public class PIDSUpdatePacket extends PacketHandler {
    protected final BlockPos blockPos;
    protected final String[] customMessages;
    protected final boolean[] rowHidden;
    protected final String presetId;

    public PIDSUpdatePacket(PacketBuffer packetBuffer) {
        this.blockPos = packetBuffer.readBlockPos();
        int rows = packetBuffer.readVarInt();
        this.customMessages = new String[rows];
        this.rowHidden = new boolean[rows];

        for(int i = 0; i < rows; i++) {
            this.customMessages[i] = packetBuffer.readString();
        }
        for(int i = 0; i < rows; i++) {
            this.rowHidden[i] = packetBuffer.readBoolean();
        }
        this.presetId = packetBuffer.readString();
    }

    public PIDSUpdatePacket(BlockPos blockPos, String[] customMessages, boolean[] rowHidden, String pidsPreset) {
        this.blockPos = blockPos;
        this.customMessages = customMessages;
        this.rowHidden = rowHidden;
        this.presetId = pidsPreset;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(blockPos);
        packetBuffer.writeVarInt(customMessages.length);

        for(String customMessage : customMessages) {
            packetBuffer.writeString(customMessage);
        }
        for(boolean hidePlatform : rowHidden) {
            packetBuffer.writeBoolean(hidePlatform);
        }

        packetBuffer.writeString(presetId);
    }

    @Override
    public void runServerQueued(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        World world = serverPlayerEntity.getEntityWorld();
        BlockEntity be = BlockUtil.getBlockEntityOrNull(world, blockPos);

        if(be != null && be.data instanceof PIDSBlockEntity) {
            ((PIDSBlockEntity)be.data).setData(customMessages, rowHidden, presetId);
        }
    }
}

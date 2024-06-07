package com.lx862.jcm.mod.network.gui;

import com.lx862.jcm.mod.data.EnquiryScreenType;
import com.lx862.jcm.mod.data.TransactionEntry;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnquiryUpdateGUIPacket extends PacketHandler {

    private final EnquiryScreenType type;
    private final List<TransactionEntry> entries;
    private final int entryCount;
    private final int remainingBalance;

    public EnquiryUpdateGUIPacket(PacketBufferReceiver packetBufferReceiver) {
        this.entries = new ArrayList<>();
        this.type = EnquiryScreenType.valueOf(packetBufferReceiver.readString());
        this.remainingBalance = packetBufferReceiver.readInt();

        this.entryCount = packetBufferReceiver.readInt();
        for (int i = 0; i < entryCount; i++) {
            String source = packetBufferReceiver.readString();
            long amount = packetBufferReceiver.readLong();
            long time = packetBufferReceiver.readLong();
            entries.add(new TransactionEntry(source, amount, time));
        }
    }

    public EnquiryUpdateGUIPacket(EnquiryScreenType type, List<TransactionEntry> entries, int remainingBalance) {
        this.type = type;
        this.entries = entries.stream().sorted((a, b) -> (int)(b.time - a.time)).toList();
        this.entryCount = entries.size();
        this.remainingBalance = remainingBalance;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeString(type.toString());
        packetBufferSender.writeInt(remainingBalance);
        packetBufferSender.writeInt(entries.size());

        for (TransactionEntry transactionEntry : entries) {
            packetBufferSender.writeString(transactionEntry.source);
            packetBufferSender.writeLong(transactionEntry.amount);
            packetBufferSender.writeLong(transactionEntry.time);
        }
    }

    @Override
    public void runClient() {
        ClientHelper.openEnquiryScreen(type, entries, remainingBalance);
    }
}
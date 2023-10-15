package com.lx862.jcm.mod.gui;

import com.lx862.jcm.mod.gui.base.BlockConfigurationScreenBase;
import com.lx862.jcm.mod.gui.widget.NumericTextField;
import com.lx862.jcm.mod.network.block.FareSaverUpdatePacket;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.MutableText;

public class FareSaverScreen extends BlockConfigurationScreenBase {
    private final NumericTextField discountTextField;
    public FareSaverScreen(BlockPos blockPos, int discount) {
        super(blockPos);
        this.discountTextField = new NumericTextField(0, 0, 60, 20, 0, 100000, 2, TextUtil.translatable(TextCategory.GUI, "faresaver.currency"));
        this.discountTextField.setValue(discount);
    }

    @Override
    public MutableText getScreenTitle() {
        return TextUtil.translatable(TextCategory.BLOCK, "faresaver");
    }

    @Override
    public void addConfigEntries() {
        addChild(new ClickableWidget(discountTextField));
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "faresaver.discount"), discountTextField);
    }
    @Override
    public void onSave() {
        Networking.sendPacketToServer(new FareSaverUpdatePacket(blockPos, discountTextField.getValue()));
    }
}

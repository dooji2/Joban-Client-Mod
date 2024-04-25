package com.lx862.jcm.mod.render.gui.screen;

import com.lx862.jcm.mod.block.entity.PIDSBlockEntity;
import com.lx862.jcm.mod.data.pids.PIDSManager;
import com.lx862.jcm.mod.network.block.PIDSUpdatePacket;
import com.lx862.jcm.mod.registry.Networking;
import com.lx862.jcm.mod.render.gui.screen.base.BlockConfigScreen;
import com.lx862.jcm.mod.render.gui.widget.ContentItem;
import com.lx862.jcm.mod.render.gui.widget.HorizontalWidgetSet;
import com.lx862.jcm.mod.render.gui.widget.MappedWidget;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Station;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectList;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.CheckboxWidgetExtension;
import org.mtr.mapping.mapper.TextFieldWidgetExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.TextCase;
import org.mtr.mod.InitClient;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.screen.DashboardListItem;
import org.mtr.mod.screen.DashboardListSelectorScreen;

import static org.mtr.mod.screen.PIDSConfigScreen.getPlatformsForList;

public class PIDSScreen extends BlockConfigScreen {
    private final TextFieldWidgetExtension[] customMessagesWidgets;
    private final CheckboxWidgetExtension[] rowHiddenWidgets;
    private final CheckboxWidgetExtension hidePlatformNumber;
    private final ButtonWidgetExtension choosePresetButton;
    private final ButtonWidgetExtension choosePlatformButton;

    private LongAVLTreeSet filteredPlatforms;
    private String presetId;
    public PIDSScreen(BlockPos blockPos, String[] customMessages, boolean[] rowHidden, boolean hidePlatformNumber, String presetId) {
        super(blockPos);
        this.filteredPlatforms = new LongAVLTreeSet();
        this.customMessagesWidgets = new TextFieldWidgetExtension[customMessages.length];
        this.rowHiddenWidgets = new CheckboxWidgetExtension[rowHidden.length];

        for(int i = 0; i < customMessages.length; i++) {
            String customMessage = customMessages[i];
            this.customMessagesWidgets[i] = new TextFieldWidgetExtension(0, 0, 120, 20, "", 100, TextCase.DEFAULT, null, TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.custom_message").getString());
            this.customMessagesWidgets[i].setText2(customMessage);
        }

        for(int i = 0; i < rowHidden.length; i++) {
            boolean rowIsHidden = rowHidden[i];
            this.rowHiddenWidgets[i] = new CheckboxWidgetExtension(0, 0, 120, 20, true, (cb) -> {});
            this.rowHiddenWidgets[i].setChecked(rowIsHidden);
            this.rowHiddenWidgets[i].setMessage2(Text.cast(TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.row_hidden")));
        }

        this.hidePlatformNumber = new CheckboxWidgetExtension(0, 0, 20, 20, true, (cb) -> {});
        this.hidePlatformNumber.setChecked(hidePlatformNumber);

        this.choosePresetButton = new ButtonWidgetExtension(0, 0, 60, 20, TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.choose_preset"), (btn) -> {
            MinecraftClient.getInstance().openScreen(
                new Screen(new PIDSPresetScreen(this.presetId, (str) -> {
                    this.presetId = str;
                }).withPreviousScreen(new Screen(this)))
            );
        });

        this.choosePlatformButton = new ButtonWidgetExtension(0, 0, 60, 20, TextUtil.translatable(TextCategory.GUI, "pids.listview.widget.change_platform"), (btn) -> {
            final Station station = InitClient.findStation(blockPos);
            if (station != null) {
                final ObjectImmutableList<DashboardListItem> platformsForList = getPlatformsForList(station);
                MinecraftClient.getInstance().openScreen(new Screen(new DashboardListSelectorScreen(() -> {
                    MinecraftClient.getInstance().openScreen(new Screen(this));
                }, new ObjectImmutableList<>(platformsForList), filteredPlatforms, false, false)));
            }
        });

        this.presetId = presetId;

        if(MinecraftClient.getInstance().getWorldMapped() != null) {
            BlockEntity be = MinecraftClient.getInstance().getWorldMapped().getBlockEntity(blockPos);
            if(be != null && be.data instanceof PIDSBlockEntity) {
                filteredPlatforms = ((PIDSBlockEntity)be.data).getPlatformIds();
            }
        }
    }

    @Override
    public MutableText getScreenTitle() {
        return TextUtil.translatable(TextCategory.BLOCK, "pids_rv");
    }

    @Override
    public void addConfigEntries() {
        // Preset button
        addChild(new ClickableWidget(choosePresetButton));
        ContentItem presetEntry = new ContentItem(TextUtil.translatable(TextCategory.GUI, "pids.listview.title.pids_preset"), new MappedWidget(choosePresetButton), 26);
        presetEntry.setIconCallback((guiDrawing, startX, startY, width, height) -> {
            PIDSPresetScreen.drawPIDSPreview(PIDSManager.getPreset(presetId), guiDrawing, startX, startY, width, height, true);
        });
        listViewWidget.add(presetEntry);

        // Filter Platform button
        ObjectList<String> platformList = new ObjectArrayList<>();
        for(long filteredPlatform : filteredPlatforms) {
            Platform platform = MinecraftClientData.getInstance().platforms.stream().filter(p -> p.getId() == filteredPlatform).findFirst().orElse(null);
            if(platform == null) {
                platformList.add("?");
            } else {
                platformList.add(platform.getName());
            }
        }
        String platforms = String.join(",", platformList);
        listViewWidget.add(filteredPlatforms.isEmpty() ? TextUtil.translatable(TextCategory.GUI, "pids.listview.title.filtered_platform.nearby") : TextUtil.translatable(TextCategory.GUI, "pids.listview.title.filtered_platform", platforms), new MappedWidget(choosePlatformButton));
        addChild(new ClickableWidget(choosePlatformButton));

        for(int i = 0; i < this.customMessagesWidgets.length; i++) {
            addChild(new ClickableWidget(this.customMessagesWidgets[i]));
            addChild(new ClickableWidget(this.rowHiddenWidgets[i]));

            int w = listViewWidget.getWidth2();
            this.rowHiddenWidgets[i].setWidth2(95);
            this.customMessagesWidgets[i].setWidth2(w - this.rowHiddenWidgets[i].getWidth2() - 18);

            HorizontalWidgetSet widgetSet = new HorizontalWidgetSet();
            widgetSet.addWidget(new MappedWidget(this.customMessagesWidgets[i]));
            widgetSet.addWidget(new MappedWidget(this.rowHiddenWidgets[i]));
            widgetSet.setXYSize(listViewWidget.getX2(), 20, listViewWidget.getWidth2(), 20);
            listViewWidget.add(null, new MappedWidget(widgetSet));
        }

        addChild(new ClickableWidget(hidePlatformNumber));
        listViewWidget.add(TextUtil.translatable(TextCategory.GUI, "pids.listview.title.hide_platform_number"), new MappedWidget(hidePlatformNumber));
    }

    @Override
    public void onSave() {
        String[] customMessages = new String[customMessagesWidgets.length];
        boolean[] rowHidden = new boolean[rowHiddenWidgets.length];

        for(int i = 0; i < customMessagesWidgets.length; i++) {
            customMessages[i] = this.customMessagesWidgets[i].getText2();
        }

        for(int i = 0; i < rowHiddenWidgets.length; i++) {
            rowHidden[i] = this.rowHiddenWidgets[i].isChecked2();
        }

        Networking.sendPacketToServer(new PIDSUpdatePacket(blockPos, filteredPlatforms, customMessages, rowHidden, hidePlatformNumber.isChecked2(), presetId));
    }
}

package com.lx862.jcm.mod.render.gui.screen;

import com.google.gson.*;
import com.lx862.jcm.mod.Constants;
import com.lx862.jcm.mod.data.JCMClientStats;
import com.lx862.jcm.mod.data.pids.preset.JsonPIDSPreset;
import com.lx862.jcm.mod.data.pids.preset.MutableJsonPIDSPreset;
import com.lx862.jcm.mod.data.pids.preset.components.ClockComponent;
import com.lx862.jcm.mod.render.GuiHelper;
import com.lx862.jcm.mod.render.gui.screen.base.ScreenBase;
import com.lx862.jcm.mod.render.gui.screen.base.TitledScreen;
import com.lx862.jcm.mod.resources.JCMResourceManager;

import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*import net.minecraft.resource.ResourcePackManager;*/
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mapping.tool.TextCase;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class VisualEditorScreen extends TitledScreen {
    private String presetId;
    private final File resourcePackFolder;
    private final CheckboxWidgetExtension[] hideRowCheckboxes = new CheckboxWidgetExtension[4];
    private final TextFieldWidgetExtension idTextField;
    private final TextFieldWidgetExtension backgroundTextField;
    private final TextFieldWidgetExtension colorTextField;
    private final CheckboxWidgetExtension showWeatherCheckbox;
    private final CheckboxWidgetExtension showClockCheckbox;
    private final Object2ObjectOpenHashMap<String, String> presetIdToResourcePack = new Object2ObjectOpenHashMap<>();
    private final List<JsonPIDSPreset> presets;
    private MutableJsonPIDSPreset ourPreset;
    private ClientWorld world;
    private final Screen previousScreen;
    public VisualEditorScreen(String presetId, Screen previousScreen) {
        super(false);
        this.resourcePackFolder = new File(org.mtr.mapping.holder.MinecraftClient.getInstance().getRunDirectoryMapped(), "resourcepacks");
        this.presetId = presetId;
        this.presets = new ArrayList<>();
        this.previousScreen = previousScreen;

        loadResourcePacks();

        this.backgroundTextField = new TextFieldWidgetExtension(20, 0, 200, 20, Integer.MAX_VALUE, TextCase.DEFAULT, null, null);
        this.backgroundTextField.setText2(ourPreset.getBackground().getNamespace() + ":" + ourPreset.getBackground().getPath());

        this.idTextField = new TextFieldWidgetExtension(20, 0, 200, 20, Integer.MAX_VALUE, TextCase.DEFAULT, null, null);
        this.idTextField.setText2(ourPreset.getId());

        this.colorTextField = new TextFieldWidgetExtension(20, 0, 200, 20, Integer.MAX_VALUE, TextCase.DEFAULT, null, null);
        this.colorTextField.setText2(Integer.toHexString(ourPreset.getTextColor()));

        this.showWeatherCheckbox = new CheckboxWidgetExtension(20, 0, 200, 20,  TextUtil.translatable(TextCategory.GUI, "pids_preset.pids_editor.weather"), true, isChecked -> {
            ourPreset.setShowWeather(isChecked);
        });

        this.showClockCheckbox = new CheckboxWidgetExtension(20, this.height / 6 + 120, 200, 20, TextUtil.translatable(TextCategory.GUI, "pids_preset.pids_editor.clock"), true, isChecked -> {
            ourPreset.setShowClock(isChecked);
        });
    }

    @Override
    protected void init2() {
        super.init2();

        world = MinecraftClient.getInstance().getWorldMapped();

        // for full pids gui
        // this.addDrawableChild(ButtonWidget.builder(Text.of("<"), button -> {
        // currentCategoryIndex = (currentCategoryIndex - 1 + categories.size()) %
        // categories.size();
        // reloadJSON();
        // })
        // .dimensions(10, 10, 50, 20)
        // .build());
        // this.addDrawableChild(ButtonWidget.builder(Text.of(">"), button -> {
        // currentCategoryIndex = (currentCategoryIndex + 1) % categories.size();
        // reloadJSON();
        // })
        // .dimensions(this.width - 60, 10, 50, 20)
        // .build());

        idTextField.setY2(this.height / 6 + 20);
        addChild(new ClickableWidget(idTextField));

        backgroundTextField.setY2(this.height / 6 + 45);
        addChild(new ClickableWidget(backgroundTextField));

        colorTextField.setY2(this.height / 6 + 70);
        colorTextField.setText2(Integer.toHexString(ourPreset.getTextColor()));
        addChild(new ClickableWidget(colorTextField));

        showWeatherCheckbox.setY2(this.height / 6 + 95);
        addChild(new ClickableWidget(showWeatherCheckbox));

        showClockCheckbox.setY2(this.height / 6 + 120);
        addChild(new ClickableWidget(showClockCheckbox));

        // for full pids gui
        // this.addDrawableChild(ButtonWidget.builder(Text.of("<"), button -> {
        // currentIdIndex = (currentIdIndex - 1 +
        // jsonObject.getAsJsonArray("pids_images").size())
        // % jsonObject.getAsJsonArray("pids_images").size();
        // updateFieldsBasedOnCurrentId();
        // })
        // .dimensions(10, this.height / 6 - 5, 20, 20)
        // .build());
        // this.addDrawableChild(
        // ButtonWidget.builder(Text.of(">"), button -> {
        // currentIdIndex = (currentIdIndex + 1) %
        // jsonObject.getAsJsonArray("pids_images").size();
        // updateFieldsBasedOnCurrentId();
        // })
        // .dimensions(this.width - 30, this.height / 6 - 5, 20, 20)
        // .build());

        int totalWidth = hideRowCheckboxes.length * 30;
        int horizontalGap = (this.width - totalWidth) / (hideRowCheckboxes.length + 1);
        int startX = horizontalGap;

        for (int i = 0; i < hideRowCheckboxes.length; i++) {
            int x = startX + i * (30 + horizontalGap) - 15;
            int y = this.height - 35;
            final int index = i;
            hideRowCheckboxes[i] = new CheckboxWidgetExtension(x, y, 20, 20, TextUtil.translatable(TextCategory.GUI, "pids_preset.pids_editor.hiderow", (i+1)), true, isChecked -> {
                ourPreset.setRowHidden(index, isChecked);
            });
            addChild(new ClickableWidget(hideRowCheckboxes[i]));
        }
        populateCurrentPresetFields();

        if (presetId != null) {
            idTextField.setText2(presetId);
        }
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
        super.render(graphicsHolder, mouseX, mouseY, delta);
        renderPIDSPreview(graphicsHolder, ourPreset);

        // this was for tooltips
        // if (idTextField.isMouseOver(mouseX, mouseY)) {
        // renderWithTooltip(context, Text.of("The name of the template"), mouseX,
        // mouseY);
        // } else if (backgroundTextField.isMouseOver(mouseX, mouseY)) {
        // renderTooltip(matrices, Text.of("The background path of the PID"), mouseX,
        // mouseY);
        // } else if (colorTextField.isMouseOver(mouseX, mouseY)) {
        // renderTooltip(matrices, Text.of("The color of the text shown on the PID"),
        // mouseX, mouseY);
        // }
    }

    @Override
    public MutableText getScreenTitle() {
        return TextUtil.translatable(TextCategory.GUI, "pids_preset.pids_editor.title");
    }

    @Override
    public MutableText getScreenSubtitle() {
        return TextUtil.translatable(TextCategory.GUI, "pids_preset.pids_editor.editingpreset").append(":").append(" ").append(presetId);
    }

    @Override
    public boolean isPauseScreen2() {
        return false;
    }

    @Override
    public void onClose2() {
        super.onClose2();
        MinecraftClient.getInstance().openScreen(
                new Screen(new EditorSaveScreen(ourPreset, presets, presetIdToResourcePack).withPreviousScreen(previousScreen))
        );
    }

    private void populateCurrentPresetFields() {
        idTextField.setText2(ourPreset.getId());
        backgroundTextField.setText2(ourPreset.getBackground().getNamespace() + ":" + ourPreset.getBackground().getPath());
        colorTextField.setText2("#" + Integer.toHexString(ourPreset.getTextColor()));
        showWeatherCheckbox.setChecked(ourPreset.getShowWeather());
        showClockCheckbox.setChecked(ourPreset.getShowClock());

        for(int i = 0; i < 4; i++) {
            hideRowCheckboxes[i].setChecked(ourPreset.isRowHidden(i));
        }
    }

    private void renderPIDSPreview(GraphicsHolder context, JsonPIDSPreset pidsPreset) {
        Identifier backgroundId = pidsPreset.getBackground();
        Identifier frameTexture = new Identifier(Constants.MOD_ID, "textures/editor/frame.png");
        int textColor = pidsPreset.getTextColor();

        GuiDrawing guiDrawing = new GuiDrawing(context);

        int previewWidth = 160;
        int previewHeight = 90;

        int baseWidth = 427;
        double scaleFactor = (double)getWidthMapped() / baseWidth;
        double frameScaleFactor = 1.2;
        double scaledWidth = previewWidth * scaleFactor;
        double scaledHeight = previewHeight * scaleFactor;

        int startX = (getWidthMapped() / 2) + 30;
        int startY = (getHeightMapped() / 4);

        int frameX = startX - (int)(((previewWidth * 0.932 * scaleFactor * frameScaleFactor) - (previewWidth * scaleFactor)) / 2);
        int frameY = startY - (int)((previewHeight * scaleFactor * frameScaleFactor - previewHeight * scaleFactor) / 2);

        context.drawCenteredText("Preview", (int)(startX + (scaledWidth / 2)), startY - 20, 0xFFFFFF);

        context.push();
        context.translate((getWidthMapped() / 2.0) + 30, startY, 0);
        context.scale((float)scaleFactor, (float)scaleFactor, (float)scaleFactor);

        GuiHelper.drawTexture(guiDrawing, frameTexture, frameX, frameY, (int)(previewWidth * 0.932 * scaleFactor * frameScaleFactor), (int)(previewHeight * scaleFactor * frameScaleFactor));
        // drawTexture unaffected by matrices scale, probably bug in mappings
        GuiHelper.drawTexture(guiDrawing, backgroundId, startX, startY, previewWidth * scaleFactor, previewHeight * scaleFactor);
        if (pidsPreset.getShowWeather()) {
            GuiHelper.drawTexture(guiDrawing, new Identifier(Constants.MOD_ID, "textures/block/pids/weather_sunny.png"), startX + 7, startY, 11 * scaleFactor, 11 * scaleFactor);
        }

        if (pidsPreset.getShowClock() && world != null) {
            long timeNow = WorldHelper.getTimeOfDay(world) + 6000;
            long hours = timeNow / 1000;
            long minutes = Math.round((timeNow - (hours * 1000)) / 16.8);
            String timeString = String.format("%02d:%02d", hours % 24, minutes % 60);

            context.drawText(timeString, 130, 5, 0xFFFFFF, false, GraphicsHolder.getDefaultLight());
        }

        {
            context.push();
            context.translate(0, 1, 0);
            // TODO: JSONPIDSPreset.topPadding
            if(true) context.translate(0, 14, 0);

            context.scale(1.4F, 1.4F, 1.4F);
            for(int i = 0; i < 4; i++) {
                if (!pidsPreset.isRowHidden(i)) {
                    context.drawText(TextUtil.translatable(TextCategory.GUI, "pids_preset.pids_editor.station"), 5, (int)(i * 14), textColor, false, GraphicsHolder.getDefaultLight());
                }
            }
            context.pop();
        }

        context.pop();
    }

    private void loadResourcePacks() {
        presets.clear();
//        ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
//        Collection<String> activePackNames = resourcePackManager.getEnabledNames();

        File[] packs = resourcePackFolder.listFiles();
        if(packs == null) return;

        for (File pack : packs) {
            String packName = pack.getName();
            if (pack.isDirectory()) {
                File jsonFile = new File(pack, "assets/jsblock/joban_custom_resources.json");
                if (jsonFile.exists() /*&& activePackNames.contains("file/" + pack.getName())*/) {
                    loadJSON(packName);
                }
            } else if (pack.getName().endsWith(".zip")) {
                if (isZipContainsFile(pack, "assets/jsblock/joban_custom_resources.json") /*&& activePackNames.contains("file/" + pack.getName())*/) {
                    loadJSON(packName);
                }
            }
        }
    }

    private static boolean isZipContainsFile(File zipFile, String filePath) {
        try (ZipFile zf = new ZipFile(zipFile)) {
            ZipEntry entry = zf.getEntry(filePath);
            return entry != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadJSON(String rpName) {
        try {
            File jsonFile = resourcePackFolder.toPath().resolve(rpName).resolve("assets").resolve(Constants.MOD_ID).resolve("joban_custom_resources.json").toFile();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(new FileReader(jsonFile)).getAsJsonObject();
            JsonArray jsonPresets = jsonObject.get("pids_images").getAsJsonArray();

            for(int i = 0; i < jsonPresets.size(); i++) {
                JsonPIDSPreset pidsPreset = JsonPIDSPreset.parse(jsonPresets.get(i).getAsJsonObject());
                presets.add(pidsPreset);
                presetIdToResourcePack.put(pidsPreset.getId(), rpName);

                if(pidsPreset.getId().equals(presetId)) {
                    ourPreset = pidsPreset.toMutable();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
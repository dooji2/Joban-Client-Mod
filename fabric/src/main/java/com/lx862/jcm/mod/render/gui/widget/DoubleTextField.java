package com.lx862.jcm.mod.render.gui.widget;

import com.lx862.jcm.mod.render.RenderHelper;
import com.lx862.jcm.mod.util.JCMLogger;
import com.lx862.jcm.mod.util.TextCategory;
import com.lx862.jcm.mod.util.TextUtil;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.TextFieldWidgetExtension;
import org.mtr.mapping.tool.TextCase;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

/**
 * Text Field Widget that is specifically designed for entering number only
 */
public class DoubleTextField extends TextFieldWidgetExtension implements RenderHelper {
    private final double min;
    private final double max;
    private final String prefix;
    private final double defaultValue;

    public DoubleTextField(int x, int y, int width, int height, double min, double max, double defaultValue, @Nonnull String prefix) {
        super(x, y, width, height, 16, TextCase.LOWER, null, String.valueOf(defaultValue));
        this.min = min;
        this.max = max;
        this.prefix = prefix;
        this.defaultValue = defaultValue;
    }

    public DoubleTextField(int x, int y, int width, int height, double min, double max, double defaultValue, MutableText prefix) {
        this(x, y, width, height, min, max, defaultValue, prefix.getString());
    }

    public DoubleTextField(int x, int y, int width, int height, double min, double max, double defaultValue) {
        this(x, y, width, height, min, max, defaultValue, (String)null);
    }

    @Override
    public boolean charTyped2(char chr, int modifiers) {
        String prevValue = getText2();
        boolean bl = super.charTyped2(chr, modifiers);

        try {
            String newString = getText2();
            double val = Double.parseDouble(newString);
            if(val < min || val > max) {
                JCMLogger.debug("DoubleTextField: Value too large or small");
                setText2(prevValue);
                return false;
            }
        } catch (Exception e) {
            setText2(prevValue);
            return false;
        }

        return bl;
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float tickDelta) {
        super.render(graphicsHolder, mouseX, mouseY, tickDelta);

        if(prefix != null) {
            drawPrefix(graphicsHolder);
        }

        drawUpDownButton(graphicsHolder);
    }

    protected void drawPrefix(GraphicsHolder graphicsHolder) {
        int prefixWidth = GraphicsHolder.getTextWidth(prefix);
        int prefixX = getX2() - prefixWidth;
        int prefixY = getY2() + (getHeight2() / 2) - (9 / 2);

        graphicsHolder.drawText(prefix, prefixX, prefixY, 0xFFFFFFFF, true, MAX_RENDER_LIGHT);
    }

    protected void drawUpDownButton(GraphicsHolder graphicsHolder) {
        MutableText upArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.increment");
        MutableText dnArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.decrement");
        int fontHeight = 9;
        int startY = (height - (fontHeight * 2));
        int upWidth = GraphicsHolder.getTextWidth(upArrow);
        int dnWidth = GraphicsHolder.getTextWidth(dnArrow);
        graphicsHolder.drawText(upArrow, getX2() + width - upWidth - 2, getY2() + startY, 0xFFFFFFFF, false, MAX_RENDER_LIGHT);
        graphicsHolder.drawText(dnArrow, getX2() + width - dnWidth - 2, getY2() + startY + fontHeight, 0xFFFFFFFF, false, MAX_RENDER_LIGHT);
    }

    @Override
    public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
        if(visible && active && isFocused2()) {
            if(amount > 0) {
                increment();
            } else {
                decrement();
            }
        }
        return super.mouseScrolled2(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked2(double mouseX, double mouseY, int button) {
        MutableText upArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.increment");
        MutableText dnArrow = TextUtil.translatable(TextCategory.GUI, "widget.numeric_text_field.decrement");
        int fontHeight = 9;
        int startY = getY2() + (height - (fontHeight * 2)) / 2;
        int upWidth = GraphicsHolder.getTextWidth(upArrow.getString());
        int dnWidth = GraphicsHolder.getTextWidth(dnArrow.getString());

        if(inRectangle(mouseX, mouseY, getX2() + width - upWidth - 2, startY, upWidth, fontHeight)) {
            increment();
        }

        if(inRectangle(mouseX, mouseY, getX2() + width - dnWidth - 2, startY + fontHeight, dnWidth, fontHeight)) {
            decrement();
        }

        return super.mouseClicked2(mouseX, mouseY, button);
    }

    public double getNumber() {
        try {
            return Double.parseDouble(getText2());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void setValue(double value) {
        if(value < min || value > max) return;
        setText2(String.valueOf(value));
    }

    private void increment() {
        try {
            BigDecimal result = new BigDecimal(getText2()).add(new BigDecimal("0.1"));
            setValue(result.doubleValue());
        } catch (Exception e) {
            setValue(defaultValue);
        }
    }

    private void decrement() {
        try {
            BigDecimal result = new BigDecimal(getText2()).subtract(new BigDecimal("0.1"));
            setValue(result.doubleValue());
        } catch (Exception e) {
            setValue(defaultValue);
        }
    }
}
package com.lx862.jcm.mod.network.gui;

import com.lx862.jcm.mod.data.Entry;
import com.lx862.jcm.mod.render.gui.screen.*;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Screen;

import java.util.List;

/**
 * Because Network Packet class is shared between Client and Server, they must not contain any client code
 * This serves as an abstraction to perform client-specific operation.
 */
public final class ClientHelper {

    public static void openButterflyLightScreen(BlockPos blockPos, int secondsToBlink) {
		MinecraftClient.getInstance().openScreen(new Screen(new ButterflyLightScreen(blockPos, secondsToBlink)));
	}

	public static void openFareSaverGUIScreen(BlockPos blockPos, String currency, int discount) {
		MinecraftClient.getInstance().openScreen(new Screen(new FareSaverScreen(blockPos, currency, discount)));
	}

	public static void openPIDSGUIScreen(BlockPos blockPos, String[] customMessages, boolean[] rowHidden, boolean hidePlatformNumber, String presetId) {
		MinecraftClient.getInstance().openScreen(new Screen(new PIDSScreen(blockPos, customMessages, rowHidden, hidePlatformNumber, presetId)));
	}

	public static void openSoundLooperGUIScreen(BlockPos blockPos, BlockPos corner1, BlockPos corner2, String soundId, int soundCategory, float soundVolume, int interval, boolean needRedstone, boolean limitRange) {
		MinecraftClient.getInstance().openScreen(new Screen(new SoundLooperScreen(blockPos, corner1, corner2, soundId, soundCategory, soundVolume, interval, needRedstone, limitRange)));
	}

	public static void openSubsidyMachineGUIScreen(BlockPos blockPos, int pricePerUse,int cooldown) {
		MinecraftClient.getInstance().openScreen(new Screen(new SubsidyMachineScreen(blockPos, pricePerUse, cooldown)));
	}

	public static void openEnquiryScreen(List<Entry> entries) {
		MinecraftClient.getInstance().openScreen(new Screen(new EnquiryScreen(entries)));
	}

	public static void openRVEnquiryScreen(List<Entry> entries) {
		MinecraftClient.getInstance().openScreen(new Screen(new RVEnquiryScreen(entries)));
	}
}

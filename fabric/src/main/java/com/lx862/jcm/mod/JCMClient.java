package com.lx862.jcm.mod;

import com.lx862.jcm.mod.config.ClientConfig;
import com.lx862.jcm.mod.registry.JCMRegistryClient;
import com.lx862.jcm.mod.render.gui.screen.ClientConfigScreen;
import org.mtr.mapping.holder.Screen;
import org.mtr.mapping.holder.ScreenAbstractMapping;

public class JCMClient {
    private static ClientConfig config = new ClientConfig();

    public static void initializeClient() {
        config.read();
        JCMRegistryClient.register();
    }

    public static ScreenAbstractMapping getClientConfigScreen(Screen previousScreen) {
        return new ClientConfigScreen().withPreviousScreen(previousScreen);
    }

    public static ClientConfig getConfig() {
        return config;
    }
}
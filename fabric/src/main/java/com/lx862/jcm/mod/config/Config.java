package com.lx862.jcm.mod.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx862.jcm.mod.util.JCMLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public abstract class Config {
    private Path configPath;

    public Config(Path configPath) {
        this.configPath = configPath;
    }

    public final void read() {
        if(!Files.exists(configPath)) {
            write();
            read();
        } else {
            try {
                JsonObject jsonObject = new JsonParser().parse(String.join("", Files.readAllLines(configPath))).getAsJsonObject();
                fromJson(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
                write();
                JCMLogger.warn("Failed to read config file, config may be left at it's default state.");
            }
        }
    }

    public final void write() {
        try {
            configPath.getParent().toFile().mkdirs();
            Files.write(configPath, Collections.singleton(new GsonBuilder().setPrettyPrinting().create().toJson(toJson())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void reset() {
        fromJson(new JsonObject());
        write();
    }

    protected abstract void fromJson(JsonObject jsonObject);

    protected abstract JsonObject toJson();
}

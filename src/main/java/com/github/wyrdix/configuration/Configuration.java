package com.github.wyrdix.configuration;

import com.github.wyrdix.ItemEditorPlugin;

import java.io.File;
import java.io.IOException;

public abstract class Configuration {

    private final File configFile;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Configuration(File configFile) throws IOException {
        this.configFile = configFile;
        if (!configFile.exists()) {
            configFile.createNewFile();
            writeDefault();
        }
    }

    public Configuration(String path) throws IOException {
        this(new File(ItemEditorPlugin.getInstance().getDataFolder(), path));
    }

    public abstract void read() throws IOException;

    public abstract void writeDefault() throws IOException;

    public File getConfigFile() {
        return configFile;
    }
}

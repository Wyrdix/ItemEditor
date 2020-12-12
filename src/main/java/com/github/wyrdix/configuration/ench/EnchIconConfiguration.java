package com.github.wyrdix.configuration.ench;

import com.github.wyrdix.ItemEditorPlugin;
import com.github.wyrdix.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.logging.Logger;

public class EnchIconConfiguration extends Configuration {
  public EnchIconConfiguration(String path) throws IOException {
    super(path);
  }

  @Override
  public void read() {
    YamlConfiguration config = null;
    try {
      config = YamlConfiguration.loadConfiguration(getConfigFile());
    } catch (Exception e) {
      e.printStackTrace();
    }
    assert config != null;
    Logger logger = ItemEditorPlugin.getInstance().getLogger();
    int success = 0;
    int fail = 0;

    for (EnchantmentIcon value : EnchantmentIcon.values()) {
      if (!config.contains(value.name())) continue;
      EnchIcon icon = null;
      try {
        icon = new EnchIcon.EnchIconImpl(config.getConfigurationSection(value.name()).getValues(false));
      } catch (Exception e) {
        e.printStackTrace();
        logger.warning("Failed to read configuration of EnchIcon : " + value.name());
      } finally {
        if (icon != null) {
          value.set(icon);
          success++;
        } else fail++;
      }
    }
    if (fail == 0) {
      logger.info("Successfully read all configuration of EnchIcon");
    } else {
      logger.info(String.format("Configuration successfully read : %s; fail : %s", success, fail));
    }
  }

  @Override
  public void writeDefault() throws IOException {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(getConfigFile());
    for (EnchantmentIcon value : EnchantmentIcon.values()) {
      config.set(value.name(), value.getDefIcon().serialize());
    }
    config.save(getConfigFile());
  }
}

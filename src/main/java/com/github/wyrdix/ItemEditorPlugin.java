package com.github.wyrdix;

import com.github.wyrdix.configuration.ench.EnchIconConfiguration;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ItemEditorPlugin extends JavaPlugin {

    private InventoryManager invManager;

    @Override
    public void onEnable() {
        getLogger().info("ItemEditor is enabling ...");
        if (getDataFolder().mkdir()) {
            getLogger().info("Create config folder");
        }
        invManager = new InventoryManager(this);
        invManager.init();
        new EditItemCommand().register();
        try {
            new EnchIconConfiguration("enchIcon.yml").read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLogger().info("ItemEditor is now enabled and may work properly !");
    }

  @Override
  public void onDisable() {
    getLogger().info("ItemEditor is disabling ...");
    getLogger().info("ItemEditor is now disabled");
  }

  public static ItemEditorPlugin getInstance(){
    return getPlugin(ItemEditorPlugin.class);
  }

  public InventoryManager getInvManager() {
    return invManager;
  }
}

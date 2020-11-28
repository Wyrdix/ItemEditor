package com.github.wyrdix;

import fr.minuskube.inv.InventoryManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemEditorPlugin extends JavaPlugin {

  private InventoryManager invManager;

  @Override
  public void onEnable() {
    getLogger().info("ItemEditor is enabling ...");
    invManager = new InventoryManager(this);
    invManager.init();
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

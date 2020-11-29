package com.github.wyrdix;

import com.github.wyrdix.inventory.HubEditInventory;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class EditItemCommand implements TabExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }
    Player player = (Player) sender;
    PlayerInventory inv = player.getInventory();
    ItemStack mainHand = inv.getItemInMainHand();
    if(mainHand == null
            || mainHand.getType().equals(Material.AIR)){
      sender.sendMessage("§cPlease take an item in your hand");
      return true;
    }


    HubEditInventory.open(player);
    return true;
  }

  public List<String> onTabComplete(
      CommandSender sender, Command cmd, String alias, String[] args) {
    return Collections.emptyList();
  }

  public void register(){
    PluginCommand cmd = Bukkit.getPluginCommand("edit_item");
    assert cmd != null;
    cmd.setExecutor(this);
    cmd.setTabCompleter(this);
  }
}

package com.github.wyrdix.inventory;

import com.github.wyrdix.ItemEditorPlugin;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HubEditInventory implements InventoryProvider {

  private static final ItemStack DUMMY = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

  private static final ItemStack ENCHANT = new ItemStack(Material.ENCHANTMENT_TABLE);

  private static final ItemStack NAME = new ItemStack(Material.SIGN);

  private static final ItemStack LORE = new ItemStack(Material.BOOK_AND_QUILL);


  private static final SmartInventory INVENTORY;

  private static final BaseComponent[] nameComponents;

  static {
    {
      ComponentBuilder b = new ComponentBuilder("§6Click here to modify the name of the item in your hand");
      b.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/edit_item name"));
      nameComponents = b.create();
    }
    {
      ItemMeta meta = DUMMY.getItemMeta();
      assert meta != null;
      meta.setDisplayName("");
      DUMMY.setItemMeta(meta);
    }
    {
      ItemMeta meta = ENCHANT.getItemMeta();
      assert meta != null;
      meta.setDisplayName("§aEdit Enchantments");
      ENCHANT.setItemMeta(meta);
    }
    {
      ItemMeta meta = NAME.getItemMeta();
      assert meta != null;
      meta.setDisplayName("§aEdit Name");
      NAME.setItemMeta(meta);
    }
    {
      ItemMeta meta = LORE.getItemMeta();
      assert meta != null;
      meta.setDisplayName("§aEdit Lore");
      LORE.setItemMeta(meta);
    }

    INVENTORY = SmartInventory.builder()
            .id("ie:hubEdit")
            .provider(new HubEditInventory())
            .title("§cEdit")
            .manager(ItemEditorPlugin.getInstance().getInvManager()).build();
  }

  public static void open(Player player) {
    INVENTORY.open(player);
  }

  public void init(Player player, InventoryContents contents) {

    contents.fillRect(0,0, INVENTORY.getRows()-1, INVENTORY.getColumns()-1, ClickableItem.empty(DUMMY));

    contents.set(SlotPos.of(3,2), ClickableItem.empty(ENCHANT));
    contents.set(SlotPos.of(2, 6), ClickableItem.empty(NAME));
    contents.set(SlotPos.of(3, 7), ClickableItem.empty(LORE));
  }

  public void update(Player player, InventoryContents contents) {}
}

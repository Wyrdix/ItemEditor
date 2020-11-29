package com.github.wyrdix.inventory;

import com.github.wyrdix.ItemEditorPlugin;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class HubEditInventory extends BukkitRunnable implements InventoryProvider, Listener {

  private static final ItemStack DUMMY = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

  private static final ItemStack ENCHANT = new ItemStack(Material.ENCHANTMENT_TABLE);

  private static final ItemStack NAME = new ItemStack(Material.SIGN);

  private static final ItemStack LORE = new ItemStack(Material.BOOK_AND_QUILL);

  private static final SmartInventory INVENTORY;

  static {
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

    INVENTORY =
            SmartInventory.builder()
                    .id("ie:hubEdit")
                    .provider(new HubEditInventory())
                    .title("§cEdit")
                    .manager(ItemEditorPlugin.getInstance().getInvManager())
                    .build();
  }

  private final Map<Player, Long> name_writers = new HashMap<>();
  private final TextComponent name_writing_comp =
          new TextComponent("§7Write in the chat the name (or && to remove it)");
  private final TextComponent name_writing_stop_comp =
          new TextComponent("§cYou doesn't have written something, action cancelled");

  private HubEditInventory() {
    Bukkit.getPluginManager().registerEvents(this, ItemEditorPlugin.getInstance());
    runTaskTimerAsynchronously(ItemEditorPlugin.getInstance(), 0, 20);
  }

  public static void open(Player player) {
    INVENTORY.open(player);
  }

  public void init(Player player, InventoryContents contents) {

    contents.fillRect(
            0, 0, INVENTORY.getRows() - 1, INVENTORY.getColumns() - 1, ClickableItem.empty(DUMMY));

    PlayerInventory inv = player.getInventory();
    contents.setProperty("item", inv.getItemInMainHand());

    contents.set(SlotPos.of(3, 2), ClickableItem.empty(ENCHANT));
    contents.set(
            SlotPos.of(2, 5),
            ClickableItem.of(
                    NAME,
                    (s) -> {
                      name_writers.put(player, System.currentTimeMillis());
                      INVENTORY.close(player);
                    }));
    contents.set(SlotPos.of(3, 6), ClickableItem.empty(LORE));
  }

  public void update(Player player, InventoryContents contents) {
    contents.set(SlotPos.of(0, 4), ClickableItem.empty(contents.property("item")));
  }

  @EventHandler
  public void slotChange(PlayerItemHeldEvent event) {
    if (!name_writers.containsKey(event.getPlayer())) return;
    name_writers.remove(event.getPlayer());
    event
            .getPlayer()
            .sendMessage("§cThe edition of name was cancelled because you have change your held item");
  }

  @EventHandler(ignoreCancelled = true)
  public void onMessage(AsyncPlayerChatEvent event) {
    if (!name_writers.containsKey(event.getPlayer())) return;
    event.setCancelled(true);
    name_writers.remove(event.getPlayer());
    String custom = ChatColor.translateAlternateColorCodes('&', event.getMessage());
    PlayerInventory inv = event.getPlayer().getInventory();
    ItemStack mainHand = inv.getItemInMainHand();
    ItemMeta meta = mainHand.getItemMeta();
    assert meta != null;
    if (custom.equals("&&")) meta.setDisplayName(null);
    else meta.setDisplayName(custom);
    mainHand.setItemMeta(meta);
    event.getPlayer().sendMessage("§6Name changed");
    INVENTORY.open(event.getPlayer());
  }

  @Override
  public void run() {
    final long millis = System.currentTimeMillis();
    name_writers
            .entrySet()
            .removeIf(
                    (e) -> {
                      if (e.getValue() + 60000 <= millis) {
                        e.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, name_writing_stop_comp);
                        return true;
                      } else return false;
                    });
    for (Player player : name_writers.keySet()) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, name_writing_comp);
    }
  }
}

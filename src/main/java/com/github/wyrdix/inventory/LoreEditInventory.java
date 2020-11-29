package com.github.wyrdix.inventory;

import com.github.wyrdix.ItemEditorPlugin;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LoreEditInventory extends BukkitRunnable implements InventoryProvider, Listener {

    private static final ItemStack DUMMY = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

    private static final ItemStack LORE = new ItemStack(Material.BOOK_AND_QUILL);

    private static final ItemStack PREVIOUS_PAGE = new ItemStack(Material.ARROW);
    private static final String previous_page_temp = "§7Previous page : §6{v}§7";
    private static final ItemStack NEXT_PAGE = new ItemStack(Material.ARROW);
    private static final String next_page_temp = "§7Next page: §6{v}§7";

    private static final ItemStack VALID = new ItemStack(Material.WOOL, 1, (short) 13);
    private static final ItemStack DELETE = new ItemStack(Material.WOOL, 1, (short) 14);

    private static final ItemStack MOVE_UP = new ItemStack(Material.ARROW);
    private static final ItemStack MOVE_DOWN = new ItemStack(Material.ARROW);
    private static final ItemStack INSERT_BEFORE = new ItemStack(Material.PAPER);
    private static final ItemStack INSERT_AFTER = new ItemStack(Material.PAPER);
    private static final ItemStack EDIT = new ItemStack(Material.SIGN);

    private static final ItemStack LORE_LINE = new ItemStack(Material.BOOK);
    private static final String lore_line_temp = "§7Line : §6{v}§7";

    private static final ItemStack LORE_EDIT = new ItemStack(Material.BOOK_AND_QUILL);
    private static final String lore_edit_temp = "§7Click here to add empty lore line number §6{v}§7";
    private static final SmartInventory INVENTORY;

    static {
        {
            ItemMeta meta = VALID.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§aValidate this line");
            VALID.setItemMeta(meta);
        }
        {
            ItemMeta meta = DELETE.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§cDelete this line");
            DELETE.setItemMeta(meta);
        }
        {
            ItemMeta meta = EDIT.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§6Edit this line");
            EDIT.setItemMeta(meta);
        }
        {
            ItemMeta meta = MOVE_UP.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§7Move up this line");
            MOVE_UP.setItemMeta(meta);
        }
        {
            ItemMeta meta = MOVE_DOWN.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§7Move down this line");
            MOVE_DOWN.setItemMeta(meta);
        }
        {
            ItemMeta meta = INSERT_BEFORE.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§7Insert a line before");
            INSERT_BEFORE.setItemMeta(meta);
        }
        {
            ItemMeta meta = INSERT_AFTER.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§7Insert a line after");
            INSERT_AFTER.setItemMeta(meta);
        }

        {
            ItemMeta meta = DUMMY.getItemMeta();
            assert meta != null;
            meta.setDisplayName("");
            DUMMY.setItemMeta(meta);
        }
        {
            ItemMeta meta = LORE.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§aEdit Lore");
            LORE.setItemMeta(meta);
        }
        INVENTORY =
                SmartInventory.builder()
                        .id("ie:loreEdit")
                        .title("§cEdit Lore")
                        .provider(new LoreEditInventory())
                        .manager(ItemEditorPlugin.getInstance().getInvManager())
                        .build();
    }

    private final TextComponent lore_writing_comp =
            new TextComponent("§7Write in the chat the line (or && to remove it)");
    private final TextComponent lore_writing_stop_comp =
            new TextComponent("§cYou doesn't have written something, action cancelled");
    Map<Player, Integer> lore_lines = new HashMap<>();
    Map<Player, Long> lore_writers = new HashMap<>();

    public LoreEditInventory() {
        Bukkit.getPluginManager().registerEvents(this, ItemEditorPlugin.getInstance());
        runTaskTimerAsynchronously(ItemEditorPlugin.getInstance(), 0, 20);
    }

    public static void open(Player player) {
        INVENTORY.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        pagination.setItemsPerPage(14);

        PlayerInventory inv = player.getInventory();
        contents.setProperty("item", inv.getItemInMainHand());

        ItemMeta meta = inv.getItemInMainHand().getItemMeta();
        List<String> lore_list = meta.getLore() == null ? Collections.emptyList() : meta.getLore();

        List<ClickableItem> items = new ArrayList<>();

        contents.fillRect(
                0, 0, INVENTORY.getRows() - 1, INVENTORY.getColumns() - 1, ClickableItem.empty(DUMMY));
        contents.fillRow(3, ClickableItem.empty(DUMMY));
        contents.fillRow(4, ClickableItem.empty(DUMMY));

        contents.set(SlotPos.of(5, 4), ClickableItem.empty(LORE));
        contents.set(SlotPos.of(0, 4), ClickableItem.empty(inv.getItemInMainHand()));

        int index = 0;
        for (String lore : lore_list) {
            ItemStack stack = LORE_LINE.clone();
            ItemMeta itemMeta = stack.getItemMeta();
            if (itemMeta == null) continue;
            itemMeta.setDisplayName(lore_line_temp.replace("{v}", String.valueOf(++index)));
            itemMeta.setLore(Collections.singletonList("§7" + lore));
            stack.setItemMeta(itemMeta);
            final int l_index = index;
            items.add(ClickableItem.of(stack, (e) -> contents.setProperty("line", l_index)));
        }
        {
            ItemStack stack = LORE_EDIT.clone();
            ItemMeta itemMeta = stack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(lore_edit_temp.replace("{v}", String.valueOf(++index)));
            stack.setItemMeta(itemMeta);
            items.add(
                    ClickableItem.of(
                            stack,
                            (e) -> {
                                ArrayList<String> new_lore_list = new ArrayList<>(lore_list);
                                new_lore_list.add("");
                                meta.setLore(new_lore_list);
                                inv.getItemInMainHand().setItemMeta(meta);
                                contents.setProperty("item", inv.getItemInMainHand());
                                INVENTORY.open(player, pagination.last().getPage());
                            }));
        }
        pagination.setItems(items.toArray(new ClickableItem[]{}));

        if (!pagination.isFirst()) {
            ItemStack stack = PREVIOUS_PAGE.clone();
            ItemMeta itemMeta = stack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(
                    previous_page_temp.replace("{v}", String.valueOf(pagination.getPage() - 1)));
            stack.setItemMeta(itemMeta);
            contents.set(
                    SlotPos.of(2, 0),
                    ClickableItem.of(stack, (e) -> INVENTORY.open(player, pagination.previous().getPage())));
        }

        if (!pagination.isLast()) {
            ItemStack stack = NEXT_PAGE.clone();
            ItemMeta itemMeta = stack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(
                    next_page_temp.replace("{v}", String.valueOf(pagination.getPage() + 1)));
            stack.setItemMeta(itemMeta);
            contents.set(
                    SlotPos.of(2, contents.inventory().getColumns() - 1),
                    ClickableItem.of(stack, (e) -> INVENTORY.open(player, pagination.next().getPage())));
        }
        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1);
        iterator.allowOverride(false);

        pagination.addToIterator(iterator);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        ItemStack itemStack = contents.property("item");
        contents.set(SlotPos.of(0, 4), ClickableItem.empty(itemStack));
        Integer line = contents.property("line");
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore_list = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
        int max = lore_list.size();
        if (line == null) {
            lore_lines.remove(player);
            return;
        }
        {
            ItemStack stack = LORE_LINE.clone();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(lore_line_temp.replace("{v}", String.valueOf(line)));
            meta.setLore(Collections.singletonList(lore_list.get(line - 1)));
            stack.setItemMeta(meta);
            contents.set(SlotPos.of(3, 4), ClickableItem.empty(stack));
        }

        lore_lines.put(player, line);
        contents.set(
                SlotPos.of(4, 1),
                ClickableItem.of(
                        DELETE,
                        (e) -> {
                            lore_list.remove(line - 1);
                            itemMeta.setLore(lore_list);
                            itemStack.setItemMeta(itemMeta);
                            contents.property("line", null);
                            INVENTORY.open(player, contents.pagination().getPage());
                        }));
        contents.set(
                SlotPos.of(4, 2),
                ClickableItem.of(
                        INSERT_BEFORE,
                        (e) -> {
                            lore_list.add(line - 1, "");
                            itemMeta.setLore(lore_list);
                            itemStack.setItemMeta(itemMeta);
                            INVENTORY.open(player, contents.pagination().getPage());
                            ItemEditorPlugin.getInstance()
                                    .getInvManager()
                                    .getContents(player)
                                    .ifPresent(s -> s.setProperty("line", line));
                        }));
        if (line > 1)
            contents.set(
                    SlotPos.of(4, 3),
                    ClickableItem.of(
                            MOVE_UP,
                            (e) -> {
                                String current = lore_list.get(line - 1);
                                String upper = lore_list.get(line - 2);
                                lore_list.set(line - 1, upper);
                                lore_list.set(line - 2, current);
                                itemMeta.setLore(lore_list);
                                itemStack.setItemMeta(itemMeta);
                                INVENTORY.open(player, contents.pagination().getPage());
                                ItemEditorPlugin.getInstance()
                                        .getInvManager()
                                        .getContents(player)
                                        .ifPresent(s -> s.setProperty("line", line - 1));
                            }));
        else contents.set(SlotPos.of(4, 3), ClickableItem.empty(DUMMY));
        contents.set(
                SlotPos.of(4, 4),
                ClickableItem.of(
                        EDIT,
                        (e) -> {
                            lore_writers.put(player, System.currentTimeMillis());
                            INVENTORY.close(player);
                        }));
        if (line < max)
            contents.set(
                    SlotPos.of(4, 5),
                    ClickableItem.of(
                            MOVE_DOWN,
                            (e) -> {
                                String current = lore_list.get(line - 1);
                                String lower = lore_list.get(line);
                                lore_list.set(line - 1, lower);
                                lore_list.set(line, current);
                                itemMeta.setLore(lore_list);
                                itemStack.setItemMeta(itemMeta);
                                INVENTORY.open(player, contents.pagination().getPage());
                                ItemEditorPlugin.getInstance()
                                        .getInvManager()
                                        .getContents(player)
                                        .ifPresent(s -> s.setProperty("line", line + 1));
                            }));
        else contents.set(SlotPos.of(4, 5), ClickableItem.empty(DUMMY));
        contents.set(
                SlotPos.of(4, 6),
                ClickableItem.of(
                        INSERT_AFTER,
                        (e) -> {
                            lore_list.add(line, "");
                            itemMeta.setLore(lore_list);
                            itemStack.setItemMeta(itemMeta);
                            INVENTORY.open(player, contents.pagination().getPage());
                            ItemEditorPlugin.getInstance()
                                    .getInvManager()
                                    .getContents(player)
                                    .ifPresent(s -> s.setProperty("line", line));
                        }));
        contents.set(
                SlotPos.of(4, 7),
                ClickableItem.of(
                        VALID,
                        (e) -> {
                            contents.property("line", null);
                            INVENTORY.open(player, contents.pagination().getPage());
                        }));
    }

    @EventHandler(ignoreCancelled = true)
    public void onMessage(AsyncPlayerChatEvent event) {
        if (!lore_writers.containsKey(event.getPlayer())) return;
        event.setCancelled(true);
        lore_writers.remove(event.getPlayer());
        String custom = ChatColor.translateAlternateColorCodes('&', event.getMessage());
        PlayerInventory inv = event.getPlayer().getInventory();
        ItemStack mainHand = inv.getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        int line = lore_lines.get(event.getPlayer());
        if (lore.size() < line) {
            event.getPlayer().sendMessage("§An error has occured, not enough lore lines");
            return;
        }
        if (custom.equals("&&")) {
            lore.remove(line - 1);
        } else lore.set(line - 1, "§7" + custom);
        meta.setLore(lore);
        mainHand.setItemMeta(meta);
        event.getPlayer().sendMessage("§6Line changed");
        INVENTORY.open(event.getPlayer());
    }

    @Override
    public void run() {
        final long millis = System.currentTimeMillis();
        lore_writers
                .entrySet()
                .removeIf(
                        (e) -> {
                            if (e.getValue() + 60000 <= millis) {
                                e.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, lore_writing_stop_comp);
                                return true;
                            } else return false;
                        });
        for (Player player : lore_writers.keySet()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, lore_writing_comp);
        }
    }
}

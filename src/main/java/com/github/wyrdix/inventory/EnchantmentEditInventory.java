package com.github.wyrdix.inventory;

import com.github.wyrdix.ItemEditorPlugin;
import com.github.wyrdix.configuration.ench.EnchantmentIcon;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EnchantmentEditInventory implements InventoryProvider {

  private static final ItemStack DUMMY = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

  private static final ItemStack ENCHANT = new ItemStack(Material.ENCHANTMENT_TABLE);

  private static final ItemStack PREVIOUS_PAGE = new ItemStack(Material.ARROW);
  private static final String previous_page_temp = "§7Previous page : §6{v}§7";
  private static final ItemStack NEXT_PAGE = new ItemStack(Material.ARROW);
  private static final String next_page_temp = "§7Next page: §6{v}§7";

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
    INVENTORY =
            SmartInventory.builder()
                    .id("ie:hubEdit")
                    .provider(new EnchantmentEditInventory())
                    .title("§cEdit")
                    .manager(ItemEditorPlugin.getInstance().getInvManager())
                    .build();
  }

  public static void open(Player player) {
    INVENTORY.open(player, 0);
  }

  /**
   * Source :
   * https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
   *
   * @param value value to convert in roman
   * @return the value in roman system
   * @author André Kramer Orten
   */
  private static String formatToRoman(int value) {
    return String.join("", StringUtils.repeat("I", value))
            .replace("IIIII", "V")
            .replace("IIII", "IV")
            .replace("VV", "X")
            .replace("VIV", "IX")
            .replace("XXXXX", "L")
            .replace("XXXX", "XL")
            .replace("LL", "C")
            .replace("LXL", "XC")
            .replace("CCCCC", "D")
            .replace("CCCC", "CD")
            .replace("DD", "M")
            .replace("DCD", "CM");
  }

  @Override
  public void init(Player player, InventoryContents contents) {
    ItemStack mainHand = player.getInventory().getItemInMainHand();
    contents.setProperty("item", mainHand.clone());
    contents.fillRect(
            SlotPos.of(0, 0),
            SlotPos.of(INVENTORY.getRows() - 1, INVENTORY.getColumns() - 1),
            ClickableItem.empty(DUMMY));
    contents.set(SlotPos.of(0, 4), ClickableItem.empty(contents.property("item")));

    Pagination pagination = contents.pagination();
    pagination.setItemsPerPage(28);

    pagination.setItems(
            Arrays.stream(EnchantmentIcon.values())
                    .map(
                            s -> {
                              ItemStack icon = s.getIcon();
                              ItemMeta meta = icon.getItemMeta();
                              int level = mainHand.getEnchantmentLevel(s.getEnchantment());
                              if (level > 0) {
                                meta.addEnchant(s.getEnchantment(), level, false);
                                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                meta.setDisplayName(meta.getDisplayName() + " " + formatToRoman(level));
                                icon.setItemMeta(meta);
                                icon.setAmount(level);
                              }
                              return ClickableItem.of(
                                      icon,
                                      (e) -> {
                                        int amount = level + (e.isLeftClick() ? 1 : -1);
                                        if (amount < 0) amount = 0;
                                        if (amount == 0) mainHand.removeEnchantment(s.getEnchantment());
                                        else mainHand.addUnsafeEnchantment(s.getEnchantment(), amount);
                                        INVENTORY.open(player, pagination.getPage());
                                      });
                            })
                    .toArray(ClickableItem[]::new));

    if (!pagination.isFirst()) {
      ItemStack stack = PREVIOUS_PAGE.clone();
      ItemMeta itemMeta = stack.getItemMeta();
      assert itemMeta != null;
      itemMeta.setDisplayName(
              previous_page_temp.replace("{v}", String.valueOf(pagination.getPage() - 1)));
      stack.setItemMeta(itemMeta);
      contents.set(
              SlotPos.of(5, 3),
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
              SlotPos.of(5, 6),
              ClickableItem.of(stack, (e) -> INVENTORY.open(player, pagination.next().getPage())));
    }
    SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1);
    iterator.blacklist(SlotPos.of(0, 4));
    iterator.allowOverride(false);

    pagination.addToIterator(iterator);
  }

  @Override
  public void update(Player player, InventoryContents contents) {
    contents.set(SlotPos.of(0, 4), ClickableItem.empty(contents.property("item")));
  }
}

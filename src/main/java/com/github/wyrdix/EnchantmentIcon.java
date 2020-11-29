package com.github.wyrdix;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.enchantments.Enchantment.*;

public enum EnchantmentIcon {
  PROTECTION(PROTECTION_ENVIRONMENTAL, Material.IRON_CHESTPLATE, "§7Protection"),
  FIRE_PROTECTION(PROTECTION_FIRE, Material.FIREBALL, "§7Fire Protection"),
  FEATHER_FALLING(PROTECTION_FALL, Material.HAY_BLOCK, "§7Feather Falling"),
  BLAST_PROTECTION(PROTECTION_EXPLOSIONS, Material.TNT, "§7Blast Protection"),
  PROJECTILE_PROTECTION(PROTECTION_PROJECTILE, Material.ARROW, "§7Projectile Protection"),
  RESPIRATION(OXYGEN, Material.RAW_FISH, 3, "§7Respiration"),
  AQUA_AFFINITY(WATER_WORKER, Material.SPONGE, "§7Aqua Affinity"),
  THORNS(Enchantment.THORNS, Material.CACTUS, "§7Thorns"),
  DEPTH_STRIDER(Enchantment.DEPTH_STRIDER, Material.WATER_LILY, "§7Depth Strider"),
  FROST_WALKER(Enchantment.FROST_WALKER, Material.ICE, "§7Frost Walker"),
  SHARPNESS(Enchantment.DAMAGE_ALL, Material.IRON_SWORD, "§7Sharpness"),
  SMITE(DAMAGE_UNDEAD, Material.BONE, "§7Smite"),
  BANE_OF_ARTHROPODS(DAMAGE_ARTHROPODS, Material.SPIDER_EYE, "§7Bane Of Arthropods"),
  KNOCKBACK(Enchantment.KNOCKBACK, Material.STICK, "§7Knockback"),
  FIRE_ASPECT(Enchantment.FIRE_ASPECT, Material.FIREBALL, "§7Fire Aspect"),
  LOOTING(LOOT_BONUS_MOBS, Material.GOLD_NUGGET, "§7Looting"),
  SWEEPING_EDGE(Enchantment.SWEEPING_EDGE, Material.DIAMOND_SWORD, "§7Sweeping Edge"),
  UNBREAKING(DURABILITY, Material.OBSIDIAN, "§7Unbreaking"),
  POWER(ARROW_DAMAGE, Material.BOW, "§7Power"),
  PUNCH(ARROW_KNOCKBACK, Material.FEATHER, "§7Punch"),
  FLAME(ARROW_FIRE, Material.BLAZE_POWDER, "§7Flame"),
  INFINITY(ARROW_INFINITE, Material.ARROW, "§7Infinity"),
  MENDING(Enchantment.MENDING, Material.EXP_BOTTLE, "§7Mending"),

  EFFICIENCY(DIG_SPEED, Material.SUGAR, "§7Efficiency"),
  SILK_TOUCH(Enchantment.SILK_TOUCH, Material.GLASS, "§7Silk Touch"),
  FORTUNE(LOOT_BONUS_BLOCKS, Material.DIAMOND, "§7Fortune"),
  LUCK_OF_THE_SEA(LUCK, Material.SEA_LANTERN, "§7Luck Of The Sea"),
  LURE(Enchantment.LURE, Material.FISHING_ROD, "§7Lure"),

  CURSE_OF_BINDING(BINDING_CURSE, Material.BOOK_AND_QUILL, "§7Curse Of Binding"),
  CURSE_OF_VANISHING(VANISHING_CURSE, Material.STAINED_GLASS_PANE, 0, "§7Curse Of Vanishing"),
  ;
  private final Enchantment enchantment;
  private final ItemStack icon;

  EnchantmentIcon(Enchantment enchantment, Material material, String name) {
    this(enchantment, material, 0, name);
  }

  EnchantmentIcon(Enchantment enchantment, Material material, int data, String name) {

    this.enchantment = enchantment;
    this.icon = new ItemStack(material, 1, (short) data);
    ItemMeta meta = icon.getItemMeta();
    meta.setDisplayName(name);
    icon.setItemMeta(meta);
  }

  public ItemStack getIcon() {
    return icon.clone();
  }

  public Enchantment getEnchantment() {
    return enchantment;
  }
}

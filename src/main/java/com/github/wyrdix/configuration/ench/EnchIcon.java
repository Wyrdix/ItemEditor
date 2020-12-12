package com.github.wyrdix.configuration.ench;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public interface EnchIcon {

  String name();

  String enchantName();

  Enchantment enchantment();

  org.bukkit.Material material();

  short data();

  default ItemStack getIcon() {
    ItemStack item = new ItemStack(material(), 1, data());
    ItemMeta meta = item.getItemMeta();
    assert meta != null;
    meta.setDisplayName(enchantName());
    item.setItemMeta(meta);
    return item;
  }

  default Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name());
    map.put("enchName", enchantName().replace('§', '&'));
    map.put("enchantment", enchantment().getName());
    map.put("material", material().name());
    map.put("data", data());
    return map;
  }

  class EnchIconImpl implements EnchIcon {

    private final String name;
    private final String enchName;
    private final Enchantment enchantment;
    private final Material material;
    private final short data;

    public EnchIconImpl(
            String name, String enchName, Enchantment enchantment, Material material, short data) {
      this.name = name;
      this.enchName = enchName;
      this.enchantment = enchantment;
      this.material = material;
      this.data = data;
    }

    public EnchIconImpl(Map<String, Object> params) {
      this.name = (String) params.get("name");
      this.enchName = ChatColor.translateAlternateColorCodes('&', (String) params.get("enchName"));
      this.enchantment = Enchantment.getByName((String) params.get("enchantment"));
      this.material = Material.valueOf(((String) params.get("material")));
      this.data = ((Integer) params.get("data")).shortValue();
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public String enchantName() {
      return enchName;
    }

    @Override
    public Enchantment enchantment() {
      return enchantment;
    }

    @Override
    public Material material() {
      return material;
    }

    @Override
    public short data() {
      return data;
    }
  }
}

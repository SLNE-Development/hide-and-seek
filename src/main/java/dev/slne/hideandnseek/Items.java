package dev.slne.hideandnseek;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.Arrays;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * The type Items.
 */
public class Items {

  /**
   * Prepare seeker inventory.
   *
   * @param player the player
   */
  public static void prepareSeekerInventory(Player player) {
    PlayerInventory inventory = player.getInventory();

    ItemStack sword = item(Material.WOODEN_SWORD, 1, 0, Component.text("Schwert"),
        Component.text("Ein Schwert zum Kämpfen"));

    ItemStack bow = enchant(
        item(Material.BOW, 1, 0, Component.text("Bogen"), Component.text("Ein Bogen")),
        Map.of(Enchantment.INFINITY.key(), 1));

    ItemStack arrow = item(Material.ARROW, 1, 0, Component.text("Pfeil"),
        Component.text("Ein Pfeil zum Schießen"));

    ItemStack helmet = dyeLeather(item(Material.LEATHER_HELMET, 1, 0, Component.text("Helm"),
        Component.text("Ein Helm zum Schutz")), Color.AQUA);
    ItemStack chestplate = dyeLeather(
        item(Material.LEATHER_CHESTPLATE, 1, 0, Component.text("Brustplatte"),
            Component.text("Eine Brustplatte zum Schutz")), Color.AQUA);
    ItemStack leggings = dyeLeather(item(Material.LEATHER_LEGGINGS, 1, 0, Component.text("Hose"),
        Component.text("Eine Hose zum Schutz")), Color.AQUA);
    ItemStack boots = dyeLeather(item(Material.LEATHER_BOOTS, 1, 0, Component.text("Schuhe"),
        Component.text("Schuhe zum Schutz")), Color.AQUA);

    inventory.clear();
    inventory.setItem(0, sword);
    inventory.setItem(1, bow);
    inventory.setItem(8, arrow);

    inventory.setHelmet(helmet);
    inventory.setChestplate(chestplate);
    inventory.setLeggings(leggings);
    inventory.setBoots(boots);
  }

  public static ItemStack dyeLeather(ItemStack itemStack, Color color) {
    itemStack.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    return itemStack;
  }

  public static ItemStack enchant(ItemStack itemStack, Map<Key, Integer> enchantments) {
    itemStack.editMeta(meta -> enchantments.forEach(((enchantmentKey, level) -> {
      Enchantment enchantment = RegistryAccess.registryAccess()
          .getRegistry(RegistryKey.ENCHANTMENT).get(enchantmentKey);

      if (enchantment != null) {
        meta.addEnchant(enchantment, level, true);
      }
    })));

    return itemStack;
  }

  /**
   * Item item stack.
   *
   * @param material    the material
   * @param amount      the amount
   * @param durability  the durability
   * @param displayName the display name
   * @param lore        the lore
   * @return the item stack
   */
  public static ItemStack item(Material material, int amount, int durability, Component displayName,
      Component... lore) {
    ItemStack itemStack = new ItemStack(material, amount);

    itemStack.editMeta(Damageable.class, meta -> {
      meta.setDamage(durability);
    });

    itemStack.editMeta(itemMeta -> {
      itemMeta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
      itemMeta.lore(
          Arrays.stream(lore)
              .map(line -> line.decoration(TextDecoration.ITALIC, false).colorIfAbsent(
                  NamedTextColor.GRAY)).toList());
    });

    return itemStack;
  }

}

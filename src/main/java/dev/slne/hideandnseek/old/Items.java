package dev.slne.hideandnseek.old;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * The type Items.
 */
@UtilityClass
public class Items {

  /**
   * Prepare seeker inventory.
   *
   * @param player the player
   */
  public static void prepareSeekerInventory(Player player) {
    final PlayerInventory inventory = player.getInventory();

    final ItemStack sword = unbreakable(item(Material.WOODEN_SWORD, 1, 0, Component.text("Schwert"), Component.text("Ein Schwert zum Kämpfen")));
    final ItemStack bow = unbreakable(enchant(item(Material.BOW, 1, 0, Component.text("Bogen"), Component.text("Ein Bogen")), Object2IntMaps.singleton(Enchantment.INFINITY.key(), 1)));
    final ItemStack arrow = item(Material.ARROW, 1, 0, Component.text("Pfeil"), Component.text("Ein Pfeil zum Schießen"));

    final ItemStack helmet = unbreakable(dyeLeather(item(Material.LEATHER_HELMET, 1, 0, Component.text("Helm"), Component.text("Ein Helm zum Schutz")), Color.AQUA));
    final ItemStack chestplate = unbreakable(dyeLeather(item(Material.LEATHER_CHESTPLATE, 1, 0, Component.text("Brustplatte"), Component.text("Eine Brustplatte zum Schutz")), Color.AQUA));
    final ItemStack leggings = unbreakable(dyeLeather(item(Material.LEATHER_LEGGINGS, 1, 0, Component.text("Hose"), Component.text("Eine Hose zum Schutz")), Color.AQUA));
    final ItemStack boots = unbreakable(dyeLeather(item(Material.LEATHER_BOOTS, 1, 0, Component.text("Schuhe"), Component.text("Schuhe zum Schutz")), Color.AQUA));

    inventory.clear();
    inventory.setItem(0, sword);
    inventory.setItem(1, bow);
    inventory.setItem(8, arrow);

    inventory.setHelmet(helmet);
    inventory.setChestplate(chestplate);
    inventory.setLeggings(leggings);
    inventory.setBoots(boots);
  }

  public ItemStack unbreakable(ItemStack itemStack) {
    itemStack.editMeta(meta -> meta.setUnbreakable(true));
    return itemStack;
  }

  public ItemStack dyeLeather(ItemStack itemStack, Color color) {
    itemStack.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    return itemStack;
  }

  public ItemStack enchant(ItemStack itemStack, Object2IntMap<Key> enchantments) {
    final Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess()
        .getRegistry(RegistryKey.ENCHANTMENT);

    itemStack.editMeta(meta -> enchantments.forEach(((enchantmentKey, level) -> {
      final Enchantment enchantment = enchantmentRegistry.get(enchantmentKey);

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
  public ItemStack item(
      Material material,
      int amount,
      int durability,
      Component displayName,
      Component... lore
  ) {
    final ItemStack itemStack = new ItemStack(material, amount);

    itemStack.editMeta(meta -> {
      if (meta instanceof Damageable damageable) {
        damageable.setDamage(durability);
      }

      meta.displayName(displayName.decorationIfAbsent(TextDecoration.ITALIC, State.FALSE));
      meta.lore(
          Arrays.stream(lore)
              .map(line -> line.decorationIfAbsent(TextDecoration.ITALIC, State.FALSE)
                  .colorIfAbsent(NamedTextColor.GRAY))
              .toList()
      );
    });

    return itemStack;
  }
}

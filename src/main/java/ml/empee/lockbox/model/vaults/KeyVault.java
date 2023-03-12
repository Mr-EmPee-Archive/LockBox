package ml.empee.lockbox.model.vaults;

import ml.empee.itembuilder.ItemBuilder;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.model.decorations.RealisticDecorator;
import ml.empee.lockbox.model.protections.KeyProtection;
import ml.empee.lockbox.utils.helpers.PluginItem;
import ml.empee.lockbox.utils.helpers.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class KeyVault extends Vault {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(KeyVault.class);
  public static final PluginItem ITEM = PluginItem.of(
      plugin, "key_vault", "1",
      ItemBuilder.from(Material.COAL_BLOCK)
          .setName("&9Vault")
          .setLore(Translator.translateBlock("vault-key-lore"))
  );

  public KeyVault(Block block) {
    super(VaultRegistry.Type.KEY, block);
    decorator = new RealisticDecorator(this);
    securitySystem = new KeyProtection(this, null);
  }

  private int computeVaultId() {
    return getLocation().hashCode();
  }

  public Inventory getInventoryTemplate() {
    return Bukkit.createInventory(null, InventoryType.DISPENSER, "Vault");
  }

}

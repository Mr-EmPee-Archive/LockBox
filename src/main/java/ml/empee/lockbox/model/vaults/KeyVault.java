package ml.empee.lockbox.model.vaults;

import lombok.Getter;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.items.VaultItem;
import ml.empee.lockbox.model.decorations.RealisticDecorator;
import ml.empee.lockbox.model.protections.KeyProtection;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.utils.helpers.PluginItem;
import ml.empee.lockbox.utils.helpers.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class KeyVault extends Vault {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(KeyVault.class);
  public static final VaultItem ITEM = new VaultItem(
      plugin, VaultRegistry.Type.KEY,
      ItemBuilder.from(Material.COAL_BLOCK)
          .setName("&9Vault")
          .setLore(Translator.translateBlock("vault-type-key-lore"))
  );

  @Getter
  private final PluginItem key = new PluginItem(
      plugin, "vault_key", getUuid().toString(),
      ItemBuilder.from(Material.TRIPWIRE_HOOK)
          .setName(Translator.translate("vault-item-key-name"))
          .setLore(Translator.translateBlock("vault-item-key-lore"))
  );

  public KeyVault(UUID uuid, Block block) {
    super(uuid, VaultRegistry.Type.KEY, block);
    decorator = new RealisticDecorator(this);
    securitySystem = new KeyProtection(this, key);
  }

  public Inventory getInventoryTemplate() {
    Inventory inventory = Bukkit.createInventory(null, InventoryType.DISPENSER, "Vault");
    inventory.addItem(key.build());
    return inventory;
  }

}

package ml.empee.lockbox.registries.items;

import lombok.Getter;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.utils.helpers.PluginItem;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultItem extends PluginItem {

  @Getter
  private final VaultRegistry.Type type;

  public VaultItem(JavaPlugin plugin, VaultRegistry.Type type, ItemBuilder item) {
    super(plugin, "vault_item", type.name(), item);

    this.type = type;
  }
}

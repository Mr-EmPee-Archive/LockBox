package ml.empee.lockbox.model.protections;

import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.utils.helpers.PluginItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KeyProtection extends VaultProtection {

  private final PluginItem key;

  public KeyProtection(Vault vault, PluginItem key) {
    super(vault);

    this.key = key;
  }

  @Override
  public boolean hasAccess(Player player, ItemStack[] vaultStorage) {
    if(key.isPluginItem(player.getInventory().getItemInMainHand())) {
      return true;
    }

    boolean hasKey = false;
    for(ItemStack item : vaultStorage) {
      if(item != null && key.isPluginItem(item)) {
        hasKey = true;
        break;
      }
    }

    return hasKey;
  }
}

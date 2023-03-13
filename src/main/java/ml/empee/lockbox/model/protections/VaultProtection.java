package ml.empee.lockbox.model.protections;

import lombok.RequiredArgsConstructor;
import ml.empee.lockbox.model.Vault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public abstract class VaultProtection {
  private final Vault vault;

  public abstract boolean hasAccess(Player player, ItemStack[] vaultStorage);
}

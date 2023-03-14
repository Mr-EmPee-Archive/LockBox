package ml.empee.lockbox.registries;

import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.items.VaultItem;
import ml.empee.lockbox.model.vaults.KeyVault;
import ml.empee.lockbox.utils.helpers.PluginItem;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class VaultRegistry implements Bean {

  private final List<VaultItem> items = new ArrayList<>();

  public VaultRegistry() {
    items.add(KeyVault.ITEM);
  }

  public PluginItem findItemByType(@NotNull Type type) {
    return items.stream()
        .filter(i -> i.getType() == type)
        .findFirst().orElseThrow(() -> new UnsupportedOperationException("Vault not implemented!"));
  }
  public Optional<VaultItem> findVaultItem(@Nullable ItemStack item) {
    if (item == null || !item.hasItemMeta()) {
      return Optional.empty();
    }

    return items.stream()
        .filter(e -> e.isPluginItem(item))
        .findFirst();
  }

  public Vault buildVault(UUID uuid, Block block, Type type) {
    switch (type) {
      case KEY -> {
        return new KeyVault(uuid, block);
      }

      default -> throw new UnsupportedOperationException("Vault not existing!");
    }
  }

  public enum Type {
    KEY //PIN, BIOMETRIC
  }
}

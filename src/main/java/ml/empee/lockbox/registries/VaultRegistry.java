package ml.empee.lockbox.registries;

import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.model.vaults.KeyVault;
import ml.empee.lockbox.utils.helpers.PluginItem;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class VaultRegistry implements Bean {

  private final Map<Type, PluginItem> items = new HashMap<>();

  public VaultRegistry() {
    items.put(Type.KEY, KeyVault.ITEM);
  }

  public Collection<PluginItem> getAllVaultsItems() {
    return Collections.unmodifiableCollection(items.values());
  }
  public PluginItem findItemByType(@NotNull Type type) {
    return items.get(type);
  }
  public Optional<Type> findVaultTypeByItem(@Nullable ItemStack item) {
    if (item == null || !item.hasItemMeta()) {
      return Optional.empty();
    }

    return items.entrySet().stream()
        .filter(e -> e.getValue().isPluginItem(item))
        .map(Map.Entry::getKey)
        .findFirst();
  }

  public Vault buildVault(Block block, Type type) {
    switch (type) {
      case KEY -> {
        return new KeyVault(block);
      }

      default -> throw new UnsupportedOperationException("Vault not existing!");
    }
  }

  public enum Type {
    KEY, PIN, BIOMETRIC
  }
}

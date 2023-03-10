package ml.empee.lockbox;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.lockbox.model.Safe.Type;
import ml.empee.lockbox.utils.helpers.PluginItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/** Repository that maps in-game items to lock types **/

@RequiredArgsConstructor
public final class SafeRegistry implements Bean {

  private final Map<Type, PluginItem> registry = new HashMap<>();
  private final JavaPlugin plugin;

  @Override
  public void onStart() {
    registry.put(Type.INSECURE, buildInsecureLock());
    registry.put(Type.SECURE, buildSecureLock());
    registry.put(Type.BIOMETRIC, buildBiometricLock());
    registry.put(Type.CREATIVE, buildCreativeLock());

  }

  public PluginItem getByType(@NotNull Type type) {
    return registry.get(type);
  }

  public Collection<PluginItem> getAll() {
    return Collections.unmodifiableCollection(registry.values());
  }

  @SuppressWarnings("MissingJavaDocMethod")
  public Optional<Type> findLockTypeOf(@Nullable ItemStack item) {
    if (item != null && item.hasItemMeta()) {

      for (Entry<Type, PluginItem> entry : registry.entrySet()) {
        if (entry.getValue().isPluginItem(item)) {
          return Optional.of(entry.getKey());
        }
      }

    }

    return Optional.empty();
  }

  private PluginItem buildInsecureLock() {
    return PluginItem.of(
        plugin, "insecure_lock", "1",
        ItemBuilder.from(Material.TRIPWIRE_HOOK)
            .setName("&eInsecure Lock")
            .setLore("", "   &c&lEasily&7 hackable lock   ", "")
    );
  }

  private PluginItem buildSecureLock() {
    return PluginItem.of(
        plugin, "secure_lock", "1",
        ItemBuilder.from(Material.TRIPWIRE_HOOK)
            .setName("&eSecure Lock")
            .setLore("", "   &7Hackable lock  ", "")
    );
  }

  private PluginItem buildBiometricLock() {
    return PluginItem.of(
        plugin, "biometric_lock", "1",
        ItemBuilder.from(Material.TRIPWIRE_HOOK)
            .setName("&eBiometric Lock")
            .setLore("", "   &6&lHighly&7 difficult hackable lock   ", "")
    );
  }

  private PluginItem buildCreativeLock() {
    return PluginItem.of(
        plugin, "creative_lock", "1",
        ItemBuilder.from(Material.TRIPWIRE_HOOK)
            .setName("&dCreative Lock")
            .setLore("", "   &7Un-hackable lock", "")
    );
  }

}

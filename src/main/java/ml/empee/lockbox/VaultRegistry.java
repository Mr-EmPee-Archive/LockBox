package ml.empee.lockbox;

import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.lockbox.config.PluginConfig;
import ml.empee.lockbox.model.Vault.Type;
import ml.empee.lockbox.utils.helpers.PluginItem;
import ml.empee.lockbox.utils.helpers.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.StreamSupport;

/** Repository that maps in-game items to lock types **/

@RequiredArgsConstructor
public final class VaultRegistry implements Bean {

  private final Map<Type, PluginItem> registry = new HashMap<>();
  private final PluginConfig config;
  private final TranslationManager translationManager;
  private final JavaPlugin plugin;

  @Override
  public void onStart() {
    registry.put(Type.KEY, buildKeyAccessedVault());
  }

  public PluginItem findItemByType(@NotNull Type type) {
    return registry.get(type);
  }

  public Collection<PluginItem> getAllVaultsItems() {
    return Collections.unmodifiableCollection(registry.values());
  }

  @SuppressWarnings("MissingJavaDocMethod")
  public Optional<Type> findVaultTypeByItem(@Nullable ItemStack item) {
    if (item != null && item.hasItemMeta()) {

      for (Entry<Type, PluginItem> entry : registry.entrySet()) {
        if (entry.getValue().isPluginItem(item)) {
          return Optional.of(entry.getKey());
        }
      }

    }

    return Optional.empty();
  }

  private String[] getLoreByType(Type type) {
    String translation = translationManager.getTranslation("vault-" + type.name().toLowerCase() + "-lore", config.getLanguage());
    if(translation.endsWith("\n")) {
      translation += " ";
    }

    return translation.split("\n");
  }

  private PluginItem buildKeyAccessedVault() {
    return PluginItem.of(
        plugin, "key_vault", "1",
        ItemBuilder.from(Material.COAL_BLOCK)
            .setName("&9Vault")
            .setLore(getLoreByType(Type.KEY))
    );
  }

}

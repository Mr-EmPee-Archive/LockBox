package ml.empee.lockbox.repositories;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.utils.ItemSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Marker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class VaultRepository implements Bean {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(VaultRepository.class);
  private static final NamespacedKey idKey = new NamespacedKey(plugin, "id");
  private static final NamespacedKey typeKey = new NamespacedKey(plugin, "type");
  private static final NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");
  private static final NamespacedKey storageKey = new NamespacedKey(plugin, "storage");

  private final VaultRegistry vaultRegistry;

  public Vault saveVault(Block block, VaultRegistry.Type type) {
    if (findVaultAt(block).isPresent()) {
      throw new IllegalArgumentException("Vault already existing at " + block.getLocation());
    }

    UUID uuid = UUID.randomUUID();
    Marker marker = (Marker) block.getWorld().spawnEntity(block.getLocation(), EntityType.MARKER);
    marker.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, uuid.toString());
    marker.getPersistentDataContainer().set(idKey, PersistentDataType.INTEGER, block.getLocation().hashCode());
    marker.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, type.name());
    marker.setPersistent(true);

    return vaultRegistry.buildVault(uuid, block, type);
  }

  public void deleteVault(Vault vault) {
    Marker marker = findDataContainer(vault).orElseThrow(
        () -> new IllegalArgumentException("Unable to find data-container for vault at " + vault.getLocation())
    );

    marker.remove();
  }

  @SneakyThrows
  public Optional<ItemStack[]> getVaultStorage(Vault vault) {
    Marker marker = findDataContainer(vault).orElseThrow(
        () -> new IllegalArgumentException("Unable to find data-container for vault at " + vault.getLocation())
    );

    String encodedInventory = marker.getPersistentDataContainer().get(storageKey, PersistentDataType.STRING);
    if (encodedInventory == null) {
      return Optional.empty();
    }

    return Optional.of(ItemSerializer.inventoryFromBase64(encodedInventory));
  }

  @SneakyThrows
  public void setVaultStorage(Vault vault, ItemStack[] items) {
    Marker marker = findDataContainer(vault).orElseThrow(
        () -> new IllegalArgumentException("Unable to find data-container for vault at " + vault.getLocation())
    );

    String encodedInventory = ItemSerializer.toBase64(items);
    marker.getPersistentDataContainer().set(storageKey, PersistentDataType.STRING, encodedInventory);
  }

  public boolean existsVaultAt(Block block) {
    return findDataContainer(block).isPresent();
  }

  private UUID findVaultUUIDAt(Block block) {
    Marker marker = findDataContainer(block).orElse(null);
    if (marker == null) {
      return null;
    }

    return UUID.fromString(
        marker.getPersistentDataContainer().get(uuidKey, PersistentDataType.STRING)
    );
  }
  private VaultRegistry.Type findVaultTypeAt(Block block) {
    Marker marker = findDataContainer(block).orElse(null);
    if (marker == null) {
      return null;
    }

    return VaultRegistry.Type.valueOf(
        marker.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING)
    );
  }

  public Optional<Vault> findVaultAt(Block block) {
    if (!existsVaultAt(block)) {
      return Optional.empty();
    }

    return Optional.of(
        vaultRegistry.buildVault(
            findVaultUUIDAt(block), block, findVaultTypeAt(block)
        )
    );
  }

  private Optional<Marker> findDataContainer(Vault vault) {
    return findDataContainer(vault.getBlock());
  }
  private Optional<Marker> findDataContainer(Block block) {
    return block.getLocation()
        .getNearbyEntitiesByType(Marker.class, 1).stream()
        .filter(e ->
            Objects.equals(
                block.getLocation().hashCode(), e.getPersistentDataContainer().get(idKey, PersistentDataType.INTEGER)
            )
        ).findFirst();
  }

}

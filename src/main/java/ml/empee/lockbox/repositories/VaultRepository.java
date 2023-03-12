package ml.empee.lockbox.repositories;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.VaultRegistry;
import ml.empee.lockbox.utils.ItemSerializer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Marker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

@RequiredArgsConstructor
public class VaultRepository implements Bean {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(VaultRepository.class);
  private static final NamespacedKey dataContainerId = new NamespacedKey(plugin, "vaultData");
  private static final NamespacedKey storageId = new NamespacedKey(plugin, "vaultStorage");

  private final VaultRegistry vaultRegistry;

  private static String computeVaultId(Vault vault) {
    Location location = vault.getLocation();
    return vault.getType().name() + "T"
        + location.getWorld() + "W"
        + location.getX() + "X"
        + location.getY() + "Y"
        + location.getZ() + "Z";
  }

  public Vault saveVault(Block block, VaultRegistry.Type type) {
    if(findVaultAt(block).isPresent()) {
      throw new IllegalArgumentException("Vault already existing at " + block.getLocation());
    }

    Vault vault = vaultRegistry.buildVault(block, type);

    Marker marker = (Marker) block.getWorld().spawnEntity(block.getLocation(), EntityType.MARKER);
    marker.getPersistentDataContainer().set(dataContainerId, PersistentDataType.STRING, computeVaultId(vault));
    marker.setPersistent(true);

    return vaultRegistry.buildVault(block, type);
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

    String encodedInventory = marker.getPersistentDataContainer().get(storageId, PersistentDataType.STRING);
    if(encodedInventory == null) {
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
    marker.getPersistentDataContainer().set(storageId, PersistentDataType.STRING, encodedInventory);
  }

  public boolean existsVaultAt(Block block) {
    return findDataContainer(block.getLocation()).isPresent();
  }
  private VaultRegistry.Type findVaultTypeAt(Block block) {
    Marker marker = findDataContainer(block.getLocation()).orElse(null);
    if(marker == null) {
      return null;
    }

    String rawType = marker.getPersistentDataContainer().get(dataContainerId, PersistentDataType.STRING);
    return VaultRegistry.Type.valueOf(rawType.split("T")[0]);
  }

  public Optional<Vault> findVaultAt(Block block) {
    VaultRegistry.Type vaultType = findVaultTypeAt(block);
    if(vaultType == null) {
      return Optional.empty();
    }

    return Optional.of(vaultRegistry.buildVault(block, vaultType));
  }

  private Optional<Marker> findDataContainer(Vault vault) {
    return findDataContainer(vault.getLocation());
  }
  private Optional<Marker> findDataContainer(Location location) {
    return location.getNearbyEntitiesByType(Marker.class, 1).stream()
        .filter(e -> e.getPersistentDataContainer().has(dataContainerId))
        .findFirst();
  }

}

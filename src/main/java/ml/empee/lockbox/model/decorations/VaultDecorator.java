package ml.empee.lockbox.model.decorations;

import lombok.RequiredArgsConstructor;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.repositories.VaultRepository;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public abstract class VaultDecorator {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(VaultDecorator.class);
  private static final NamespacedKey decorationId = new NamespacedKey(plugin, "vaultDecoration");

  protected final Vault vault;

  private int computeVaultId() {
    return vault.getLocation().hashCode();
  }

  public abstract void spawnDecorations(BlockFace front);
  public void despawnDecorations() {
    findDecorations().forEach(Entity::remove);
  }

  protected void markAsDecoration(Entity entity) {
    entity.getPersistentDataContainer().set(decorationId, PersistentDataType.INTEGER, computeVaultId());
  }

  public boolean isDecoration(Entity entity) {
    Integer vaultId = entity.getPersistentDataContainer().get(decorationId, PersistentDataType.INTEGER);
    if(vaultId == null) {
      return false;
    }

    return computeVaultId() == vaultId;
  }
  public List<Entity> findDecorations() {
    return vault.getLocation().getNearbyEntities(2.5, 2.5, 2.5).stream()
        .filter(this::isDecoration)
        .toList();
  }
}

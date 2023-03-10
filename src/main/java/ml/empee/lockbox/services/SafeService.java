package ml.empee.lockbox.services;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Safe;
import ml.empee.lockbox.repositories.SafeRepository;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/** Service used for locks managements **/

@RequiredArgsConstructor
public class SafeService implements Bean {

  private final JavaPlugin plugin;
  private final SafeRepository safeRepository;
  private final Logger logger;

  public void createSafe(@NotNull Safe safe) {
    safeRepository.save(safe.toData()).thenRun(
        () -> Bukkit.getScheduler().runTask(plugin, () -> spawnLockEntity(safe))
    );
  }

  public void destroySafe(@NotNull Safe safe) {
    safeRepository.delete(safe.getId()).thenRun(
        () -> Bukkit.getScheduler().runTask(plugin, () -> despawnLockEntity(safe))
    );
  }

  private void spawnLockEntity(Safe safe) {
    Location location = safe.getBlock().getLocation().add(0.5, 0.20, 0.5);
    ArmorStand lockEntity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
    lockEntity.setVisible(false);
    lockEntity.setMarker(true);
    lockEntity.setSmall(true);
    lockEntity.setCollidable(false);
    lockEntity.setGravity(false);

    lockEntity.getPersistentDataContainer().set(
        new NamespacedKey(plugin, "isLock"), PersistentDataType.BYTE, (byte) 1
    );

    lockEntity.getEquipment().setHelmet(
        new ItemStack(Material.YELLOW_CARPET), true
    );
  }

  private void despawnLockEntity(Safe safe) {
    Location location = safe.getBlock().getLocation();
    ArmorStand lockEntity = location.getNearbyEntitiesByType(ArmorStand.class, 1).stream()
        .filter(e -> e.getPersistentDataContainer().has(new NamespacedKey(plugin, "isLock")))
        .findFirst().orElse(null);

    if (lockEntity == null) {
      logger.warning("Tried to remove un-existing lockEntity of %s", safe);
      return;
    }

    lockEntity.remove();
  }

  public void getSafe(@NotNull Block block, Consumer<Optional<Safe>> consumer) {
    safeRepository.findByID(Safe.computeId(block)).thenAccept(
        data -> Bukkit.getScheduler().runTask(plugin, () -> {
          consumer.accept(data.map(Safe::fromRaw));
        })
    );
  }

}

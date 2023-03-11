package ml.empee.lockbox.services;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/** Service used for locks managements **/

@RequiredArgsConstructor
public class VaultService implements Bean {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(VaultService.class);
  private static final NamespacedKey vaultTypeKey = new NamespacedKey(plugin, "vaultType");
  private static final NamespacedKey vaultBlockKey = new NamespacedKey(plugin, "vaultBlock");
  private final Logger logger;

  private static String getBlockId(Block block) {
    return block.getX() + "X" + block.getY() + "Y" + block.getZ() + "Z";
  }

  public Vault createVault(Block block, BlockFace front, Vault.Type type) {
    if(getVaultAt(block).isPresent()) {
      throw new IllegalArgumentException("Vault already existing at " + block.getLocation());
    }

    Vault vault = Vault.buildVault(block, type);
    vault.spawnDecorations(front);

    vault.getData().set(vaultTypeKey, PersistentDataType.STRING, vault.getType().name());
    vault.getData().set(vaultBlockKey, PersistentDataType.STRING, getBlockId(block));

    logger.debug("Vault %s created at %s", type, block.getLocation());
    return vault;
  }

  public void destroyVault(Vault vault) {
    vault.deleteDecorations();
  }

  public Optional<? extends Vault> getVaultAt(Block block) {
    return block.getLocation().add(0.5, 0.5, 0.5).getNearbyEntities(2, 2, 2).stream()
        .filter(e -> e.getPersistentDataContainer().has(vaultTypeKey))
        .filter(e -> e.getPersistentDataContainer()
            .get(vaultBlockKey, PersistentDataType.STRING)
            .equals(getBlockId(block))
        ).map(e -> {
          Vault.Type type = Vault.Type.valueOf(
              e.getPersistentDataContainer().get(vaultTypeKey, PersistentDataType.STRING)
          );

          return Vault.buildVault(block, type);
        }).findFirst();
  }

}

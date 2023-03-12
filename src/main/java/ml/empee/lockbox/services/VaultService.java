package ml.empee.lockbox.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.exceptions.VaultUnauthorizedException;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.model.Vaults;
import ml.empee.lockbox.repositories.VaultRepository;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VaultService implements Bean {

  private final VaultRepository repository;
  private final Logger logger;

  private Cache<Block, Inventory> vaultStorageCache = CacheBuilder.newBuilder().build();

  @Override
  public void onStart() {
    vaultStorageCache = CacheBuilder.newBuilder()
        .removalListener(onCacheExpire())
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build();
  }

  @Override
  public void onStop() {
    vaultStorageCache.invalidateAll();
  }

  private RemovalListener<Block, Inventory> onCacheExpire() {
    return event -> {
      Vault vault = findVaultAt(event.getKey()).orElse(null);
      if(vault != null) {
        repository.setVaultStorage(vault, event.getValue().getStorageContents());
      } else {
        logger.debug("Unable to save content of cached vault at " + event.getKey().getLocation());
      }
    };
  }

  public Vault createVault(Block block, BlockFace front, Vaults.Type type) {
    Vault vault = Vaults.buildVault(block, type);

    vault.spawnDecorations(front);
    repository.createVault(vault);
    return vault;
  }
  public void destroyVault(Vault vault) {
    vault.despawnDecorations();

    vaultStorageCache.invalidate(vault.getBlock());
    repository.deleteVault(vault);
  }

  public Optional<Vault> findVaultAt(Block block) {
    Vaults.Type vaultType = repository.getVaultTypeAt(block).orElse(null);
    if(vaultType == null) {
      return Optional.empty();
    }

    return Optional.of(Vaults.buildVault(block, vaultType));
  }

  public void openVault(Vault vault, Player player) throws VaultUnauthorizedException {
    if(!vault.hasAccess(player)) {
      throw new VaultUnauthorizedException();
    }

    player.openInventory(
        getVaultInventory(vault)
    );
  }

  @SneakyThrows
  public Inventory getVaultInventory(Vault vault) {
    return vaultStorageCache.get(vault.getBlock(), () -> {
      Inventory inventory = vault.getInventoryTemplate();
      inventory.setContents(repository.getVaultStorage(vault));
      return inventory;
    });
  }

}

package ml.empee.lockbox.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.exceptions.VaultUnauthorizedException;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.registries.VaultRegistry;
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
        Logger.debug("Unable to save content of cached vault at " + event.getKey().getLocation());
      }
    };
  }

  public Vault createVault(Block block, BlockFace front, VaultRegistry.Type type) {
    Vault vault = repository.saveVault(block, type);
    vault.spawnDecorations(front);
    return vault;
  }
  public void destroyVault(Vault vault) {
    vault.despawnDecorations();
    repository.deleteVault(vault);
    vaultStorageCache.invalidate(vault.getBlock());
  }

  public Optional<Vault> findVaultAt(Block block) {
    return repository.findVaultAt(block);
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
      repository.getVaultStorage(vault).ifPresent(inventory::setContents);
      return inventory;
    });
  }

}

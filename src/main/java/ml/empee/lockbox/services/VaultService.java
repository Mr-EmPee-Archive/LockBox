package ml.empee.lockbox.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import lombok.SneakyThrows;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.exceptions.VaultUnauthorizedException;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.model.Vaults;
import ml.empee.lockbox.repositories.VaultRepository;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VaultService implements Bean {

  private final VaultRepository repository;
  private final Cache<Block, Inventory> vaultStorageCache;

  public VaultService(VaultRepository repository) {
    this.repository = repository;

    //Necessary to do in constructor bc the removal listener is using a reference to the repository
    vaultStorageCache = CacheBuilder.newBuilder()
        .removalListener(onCacheExpire())
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build();
  }

  private RemovalListener<Block, Inventory> onCacheExpire() {
    return event -> {
      if(event.getCause() == RemovalCause.EXPLICIT) {
        return;
      }

      Vault vault = findVaultAt(event.getKey()).orElseThrow(
          () -> new IllegalStateException("Unable to save storage, vault not existing")
      );

      repository.setVaultStorage(vault, event.getValue().getStorageContents());
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
      inventory.addItem(repository.getVaultStorage(vault));
      return inventory;
    });
  }

}

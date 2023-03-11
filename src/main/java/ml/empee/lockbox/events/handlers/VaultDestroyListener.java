package ml.empee.lockbox.events.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.services.VaultService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class VaultDestroyListener implements Bean, RegisteredListener {

  private final VaultService vaultService;

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Vault vault = vaultService.getVaultAt(event.getBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    vaultService.destroyVault(vault);
  }

}

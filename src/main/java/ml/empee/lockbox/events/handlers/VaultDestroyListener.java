package ml.empee.lockbox.events.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.services.VaultService;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class VaultDestroyListener implements Bean, RegisteredListener {

  private final VaultService vaultService;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onVaultBreak(BlockBreakEvent event) {
    Vault vault = vaultService.findVaultAt(event.getBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    Player player = event.getPlayer();
    if(!vault.hasAccess(player, vaultService.getVaultInventory(vault).getContents())) {
      Logger.translatedLog(player, "vault-auth-failed");
      return;
    }

    vaultService.destroyVault(vault);
  }

}

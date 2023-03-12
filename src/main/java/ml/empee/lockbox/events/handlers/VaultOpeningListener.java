package ml.empee.lockbox.events.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.exceptions.VaultUnauthorizedException;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.services.VaultService;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@RequiredArgsConstructor
public class VaultOpeningListener implements Bean, RegisteredListener {

  private final VaultService vaultService;
  private final Logger logger;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
  public void onVaultOpen(PlayerInteractEvent event) {
    if(event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.hasBlock()) {
      return;
    }

    Vault vault = vaultService.findVaultAt(event.getClickedBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    event.setCancelled(true);
    openVault(vault, event.getPlayer());
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
  public void onVaultOpen(PlayerInteractEntityEvent event) {
    if(event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    Entity entity = event.getRightClicked();
    if(!(entity instanceof Hanging)) {
      return;
    }

    BlockFace attachedFace = ((Hanging) entity).getAttachedFace();
    Location vaultLoc = entity.getLocation().getBlock().getLocation();
    vaultLoc.add(attachedFace.getModX(), attachedFace.getModY(), attachedFace.getModZ());

    Vault vault = vaultService.findVaultAt(vaultLoc.getBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    event.setCancelled(true);
    openVault(vault, event.getPlayer());
  }

  private void openVault(Vault vault, Player player) {
    try {
      vaultService.openVault(vault, player);
    } catch (VaultUnauthorizedException e) {
      logger.log(player, "Not authorized!");
    }
  }

}

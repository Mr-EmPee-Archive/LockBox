package ml.empee.lockbox.events.handlers;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.services.VaultService;
import ml.empee.lockbox.utils.CollectionUtils;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class VaultProtectionListener implements RegisteredListener, Bean {

  private final VaultService vaultService;

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultExplode(EntityExplodeEvent event) {
    event.blockList().removeIf(block -> vaultService.findVaultAt(block).isPresent());
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultExplode(BlockExplodeEvent event) {
    event.blockList().removeIf(block -> vaultService.findVaultAt(block).isPresent());
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultMove(BlockPistonExtendEvent event) {
    boolean isMovingVault = event.getBlocks().stream().anyMatch(block -> vaultService.findVaultAt(block).isPresent());
    if(isMovingVault) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultMove(BlockPistonRetractEvent event) {
    boolean isMovingVault = event.getBlocks().stream().anyMatch(block -> vaultService.findVaultAt(block).isPresent());
    if(isMovingVault) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultRemove(EntityChangeBlockEvent event) {
    if(vaultService.findVaultAt(event.getBlock()).isPresent()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultBreak(BlockBreakEvent event) {
    Vault vault = vaultService.findVaultAt(event.getBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    List<ItemStack> originalStorage = Arrays.asList(vault.getInventoryTemplate().getContents());
    List<ItemStack> currentStorage = Arrays.asList(vaultService.getVaultInventory(vault).getContents());
    if(!CollectionUtils.hasAllItems(originalStorage, currentStorage)) {
      event.setCancelled(true);
      Logger.translatedLog(event.getPlayer(), "vault-not-original-state");
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultRemove(BlockFadeEvent event) {
    if(vaultService.findVaultAt(event.getBlock()).isPresent()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVaultRemove(BlockBurnEvent event) {
    if(vaultService.findVaultAt(event.getBlock()).isPresent()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDecorationBreak(HangingBreakEvent event) {
    Hanging entity = event.getEntity();
    BlockFace attachedFace = entity.getAttachedFace();
    Location vaultLoc = entity.getLocation().getBlock().getLocation();
    vaultLoc.add(attachedFace.getModX(), attachedFace.getModY(), attachedFace.getModZ());
    Vault vault = vaultService.findVaultAt(vaultLoc.getBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    if(vault.isDecoration(entity)) {
      event.setCancelled(true);
    }
  }

}

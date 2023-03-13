package ml.empee.lockbox.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ml.empee.lockbox.model.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class VaultPlacedEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final Player player;
  private final Vault vault;

  @NotNull
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }
}

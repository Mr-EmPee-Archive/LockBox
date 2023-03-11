package ml.empee.lockbox.model;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Represent a vault that can be opened only if certain condition are met
 */

public interface ConditionalVault {

  /**
   * @param location The relative location of the click
   * @param entity The entity that was clicked
   */
  boolean shouldOpen(@Nullable Location location, @Nullable Entity entity);

}

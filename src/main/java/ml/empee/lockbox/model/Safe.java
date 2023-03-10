package ml.empee.lockbox.model;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ml.empee.lockbox.model.dto.SafeData;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represent a lock **/

@Getter
@Builder
@ToString
public class Safe {

  @NotNull
  private final Block block;
  @NotNull
  private final Type type;
  @Getter
  private Integer id;

  /** Build a Lock entity from a Lock DTO **/
  public static Safe fromRaw(SafeData data) {
    Preconditions.checkNotNull(data.getId(), "Missing id from safe, " + data);
    Preconditions.checkNotNull(data.getWorld(), "World not valid for safe, " + data);
    Preconditions.checkNotNull(data.getX(), "Location not valid for safe, " + data);
    Preconditions.checkNotNull(data.getY(), "Location not valid for safe, " + data);
    Preconditions.checkNotNull(data.getZ(), "Location not valid for safe, " + data);

    Type type = Type.fromString(data.getType());
    Preconditions.checkNotNull(type, "Invalid safe type for safe, " + data);

    return Safe.builder()
        .id(data.getId())
        .block(data.getWorld().getBlockAt(data.getX(), data.getY(), data.getZ()))
        .type(type)
        .build();
  }

  /**
   * Compute the id that a lock applies to the given block will have
   **/
  public static int computeId(Block block) {
    return (block.getWorld().getName() + "W" + block.getX() + "X" + block.getY() + "Y" + block.getZ()).hashCode();
  }

  public SafeData toData() {
    return SafeData.builder()
        .id(computeId(block))
        .type(type.name())
        .world(block.getWorld().getName())
        .x(block.getX())
        .y(block.getY())
        .z(block.getZ())
        .build();
  }

  /** Existing type of a lock **/
  public enum Type {
    INSECURE,
    SECURE,
    BIOMETRIC,
    CREATIVE;

    @Nullable
    public static Type fromString(@Nullable String type) {
      if (type == null) {
        return null;
      }

      try {
        return Type.valueOf(type.toUpperCase());
      } catch (IllegalArgumentException e) {
        return null;
      }
    }

  }

}

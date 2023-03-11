package ml.empee.lockbox.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationUtils {


  /**
   * Calculate the face of the block nearest to the target
   * @param target
   * @param block
   */
  public static BlockFace getFaceInFront(Block target, Block block) {
    int dx = target.getX() - block.getX();
    int dy = target.getY() - block.getY();
    int dz = target.getZ() - block.getZ();

    int absDx = Math.abs(dx);
    int absDy = Math.abs(dy);
    int absDz = Math.abs(dz);

    if (absDx >= absDy && absDx >= absDz) {
      if (dx > 0) return BlockFace.EAST;
      else if (dx < 0) return BlockFace.WEST;
    } else if (absDy >= absDx && absDy >= absDz) {
      if (dy > 0) return BlockFace.UP;
      else if (dy < 0) return BlockFace.DOWN;
    } else {
      if (dz > 0) return BlockFace.SOUTH;
      else if (dz < 0) return BlockFace.NORTH;
    }

    return BlockFace.SELF;
  }

}

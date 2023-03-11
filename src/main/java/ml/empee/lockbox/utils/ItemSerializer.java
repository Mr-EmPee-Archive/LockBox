package ml.empee.lockbox.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemSerializer {

  /**
   * A method to serialize an array of items to Base64 string.
   *
   * @param inventory to serialize
   * @return Base64 string of the provided inventory
   * @throws IllegalStateException if an I/O exception occurs
   */
  public static String toBase64(ItemStack[] items) throws IOException {
    try (
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput dataOutput = new BukkitObjectOutputStream(outputStream)
    ) {

      dataOutput.writeInt(items.length);

      for(ItemStack item : items) {
        dataOutput.writeObject(item);
      }

      // Serialize that array
      dataOutput.flush();
      outputStream.flush();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    }
  }

  /**
   * A method to get an array of items from an encoded, Base64, string.
   *
   * @param data Base64 string of data containing an inventory.
   * @return Inventory created from the Base64 string.
   */
  public static ItemStack[] inventoryFromBase64(String data) throws IOException {
    if(data == null || data.isEmpty()) {
      return new ItemStack[0];
    }

    try (
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)
    ) {
      ItemStack[] items = new ItemStack[dataInput.readInt()];

      // Read the serialized inventory
      for (int i = 0; i < items.length; i++) {
        items[i] = (ItemStack) dataInput.readObject();
      }

      return items;
    } catch (ClassNotFoundException e) {
      throw new IOException("Unable to decode class type.", e);
    }
  }

}

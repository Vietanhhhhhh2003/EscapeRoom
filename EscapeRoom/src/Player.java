import java.util.ArrayList;
import java.util.Stack;

public class Player {
    private Stack<Room> moveHistory;
    private ArrayList<Item> inventory;
    private Room currentRoom;

    public Player(Room startRoom) {
        this.currentRoom = startRoom;
        this.moveHistory = new Stack<>();
        this.inventory = new ArrayList<>();
    }

    public void moveTo(Room room) throws LockedRoomException {
        // Check if room is locked
        if (room.isLocked()) {
            String requiredKey = room.getRequiredKey();
            if (!hasKey(requiredKey)) {
                throw new LockedRoomException("This room is locked! You need: " + requiredKey);
            }
            System.out.println("You used " + requiredKey + " to unlock the room.");
        }

        // Push current room to history before moving
        moveHistory.push(currentRoom);
        currentRoom = room;
        System.out.println("You moved to: " + room.getName());
    }

    public void goBack() {
        if (moveHistory.isEmpty()) {
            System.out.println("You can't go back any further!");
            return;
        }

        currentRoom = moveHistory.pop();
        System.out.println("You went back to: " + currentRoom.getName());
    }

    public void pickupItem(String itemName) {
        GameComponent found = null;

        for (GameComponent component : currentRoom.getContents()) {
            if (component instanceof Item && component.getName().equalsIgnoreCase(itemName)) {
                found = component;
                break;
            }
        }

        if (found != null) {
            ((Item) found).collect(this);
            currentRoom.removeContent(found);
        } else {
            System.out.println("Item not found: " + itemName);
        }
    }

    public boolean hasKey(String keyName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(keyName) && item.getItemType().equals("KEY")) {
                return true;
            }
        }
        return false;
    }

    public void addToInventory(Item item) {
        inventory.add(item);
    }

    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }

        System.out.println("=== Inventory ===");
        for (Item item : inventory) {
            System.out.println("  - " + item.getName() + " (" + item.getItemType() + ")");
        }
    }

    // Sort inventory using insertion sort
    public void sortInventoryByValue() {
        for (int i = 1; i < inventory.size(); i++) {
            Item key = inventory.get(i);
            int j = i - 1;

            while (j >= 0 && inventory.get(j).compareTo(key) > 0) {
                inventory.set(j + 1, inventory.get(j));
                j--;
            }
            inventory.set(j + 1, key);
        }
        System.out.println("Inventory sorted by value.");
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }
}
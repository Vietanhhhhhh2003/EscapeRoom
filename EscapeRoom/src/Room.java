import java.util.ArrayList;

// Room with recursive structure
public class Room extends GameComponent {
    private ArrayList<GameComponent> contents;
    private ArrayList<Room> connectedRooms;
    private boolean isExit;
    private String requiredKey; // Key needed to enter

    public Room(String name, boolean isExit) {
        super(name);
        this.isExit = isExit;
        this.contents = new ArrayList<>();
        this.connectedRooms = new ArrayList<>();
        this.requiredKey = null;
    }

    @Override
    public void inspect() {
        System.out.println("Hi, " + name);
        if (isExit) {
            System.out.println("This is the EXIT!");
        }

        if (contents.isEmpty() && connectedRooms.isEmpty()) {
            System.out.println("The room is empty.");
        } else {
            if (!contents.isEmpty()) {
                System.out.println("You see:");
                for (GameComponent component : contents) {
                    System.out.println("  - " + component.getName());
                }
            }

            if (!connectedRooms.isEmpty()) {
                System.out.println("Exits to:");
                for (Room room : connectedRooms) {
                    String lockStatus = room.isLocked() ? " [LOCKED]" : "";
                    System.out.println("  - " + room.getName() + lockStatus);
                }
            }
        }
    }

    // Recursive exploration with depth tracking
    public void exploreRecursive(int depth) {
        // Base case
        if (depth < 0) return;

        String indent = "  ".repeat(depth);
        System.out.println(indent + "- " + name + (isExit ? " [EXIT]" : ""));

        // Recursive case: explore connected rooms
        for (Room room : connectedRooms) {
            room.exploreRecursive(depth + 1);
        }
    }

    // Recursive search for item
    public boolean containsItemRecursive(String itemName) {
        // Base case: check current room
        for (GameComponent component : contents) {
            if (component instanceof Item && component.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }

        // Recursive case: search connected rooms
        for (Room room : connectedRooms) {
            if (room.containsItemRecursive(itemName)) {
                return true;
            }
        }

        return false;
    }

    // Recursive depth calculation
    public int maxDepthRecursive() {
        // Base case: no connected rooms
        if (connectedRooms.isEmpty()) {
            return 0;
        }

        // Recursive case: find max depth among connected rooms
        int maxDepth = 0;
        for (Room room : connectedRooms) {
            int depth = room.maxDepthRecursive();
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }

        return maxDepth + 1;
    }

    public void addContent(GameComponent component) {
        contents.add(component);
    }

    public void removeContent(GameComponent component) {
        contents.remove(component);
    }

    public void connectRoom(Room room) {
        if (!connectedRooms.contains(room)) {
            connectedRooms.add(room);
        }
    }

    public ArrayList<GameComponent> getContents() {
        return contents;
    }

    public ArrayList<Room> getConnectedRooms() {
        return connectedRooms;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setRequiredKey(String keyName) {
        this.requiredKey = keyName;
    }

    public String getRequiredKey() {
        return requiredKey;
    }

    public boolean isLocked() {
        return requiredKey != null;
    }
}
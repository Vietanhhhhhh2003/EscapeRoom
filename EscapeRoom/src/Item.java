// Item class with Comparable for sorting by value
public class Item extends GameComponent implements Collectible, Comparable<Item> {
    private int value;
    private String itemType;

    public Item(String name, int value, String itemType) {
        super(name);
        this.value = value;
        this.itemType = itemType;
    }

    @Override
    public void inspect() {
        System.out.println("[Item] " + name + " (Type: " + itemType + ", Value: " + value + ")");
    }

    @Override
    public void collect(Player p) {
        p.addToInventory(this);
        System.out.println("Picked up: " + name);
    }

    // Compare by value for sorting
    @Override
    public int compareTo(Item other) {
        return Integer.compare(this.value, other.value);
    }

    public String getItemType() {
        return itemType;
    }

    public int getValue() {
        return value;
    }
}
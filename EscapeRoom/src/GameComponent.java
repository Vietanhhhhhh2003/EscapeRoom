
// Abstract base class for all game objects
public abstract class GameComponent {
    protected String name;

    public GameComponent(String name) {
        this.name = name;
    }

    public abstract void inspect();

    public String getName() {
        return name;
    }
}
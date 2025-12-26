import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class GameEngine {
    private ArrayList<Room> map;
    private Queue<String> hintQueue;
    private Player player;
    private Scanner scanner;
    private int turnCounter;

    public GameEngine() {
        this.map = new ArrayList<>();
        this.hintQueue = new LinkedList<>();
        this.scanner = new Scanner(System.in);
        this.turnCounter = 0;
        setupGame();
    }

    private void setupGame() {
        // Create rooms
        Room entrance = new Room("Entrance Hall", false);
        Room library = new Room("Library", false);
        Room corridor = new Room("Secret Corridor", false);
        Room basement = new Room("Dark Basement", false);
        Room treasury = new Room("Treasury", false);
        Room exitRoom = new Room("Exit Door", true);

        // Create items
        Item rustyKey = new Item("Rusty Key", 10, "KEY");
        Item goldKey = new Item("Gold Key", 50, "KEY");
        Item flashlight = new Item("Flashlight", 20, "TOOL");
        Item clue1 = new Item("Mysterious Note", 5, "CLUE");
        Item torchItem = new Item("Torch", 25, "TOOL");

        // Create puzzles with rewards
        RiddlePuzzle riddle1 = new RiddlePuzzle(
                "Ancient Riddle",
                3,
                "I speak without a mouth and hear without ears. I have no body, but I come alive with wind. What am I?",
                "echo",
                rustyKey
        );

        // Puzzle to unlock path to basement (answer reveals connection)
        RiddlePuzzle corridorPuzzle = new RiddlePuzzle(
                "Guardian Riddle",
                4,
                "What has keys but no locks, space but no room, and you can enter but can't go inside?",
                "keyboard",
                torchItem
        );

        CodePuzzle codeLock = new CodePuzzle(
                "Safe Lock",
                5,
                "1234",
                "The code is the first four counting numbers",
                goldKey
        );

        // Add contents to rooms
        entrance.addContent(flashlight);
        entrance.addContent(riddle1);
        library.addContent(clue1);
        library.addContent(codeLock);
        corridor.addContent(corridorPuzzle);
        basement.addContent(new Item("Old Coin", 15, "TOOL"));

        // Connect rooms - corridor is required to reach basement
        entrance.connectRoom(library);
        entrance.connectRoom(corridor);
        library.connectRoom(treasury);
        corridor.connectRoom(basement); // Only corridor connects to basement
        basement.connectRoom(exitRoom);

        // Set locks
        basement.setRequiredKey("Rusty Key");
        treasury.setRequiredKey("Gold Key");

        // Add to map
        map.add(entrance);
        map.add(library);
        map.add(corridor);
        map.add(basement);
        map.add(treasury);
        map.add(exitRoom);

        // Create player
        player = new Player(entrance);

        // Add hints to queue
        hintQueue.offer("Try exploring all rooms first.");
        hintQueue.offer("Some puzzles give you keys as rewards.");
        hintQueue.offer("The Secret Corridor might lead somewhere important.");
        hintQueue.offer("Use 'back' to retrace your steps.");
        hintQueue.offer("Check your inventory with 'inventory' command.");
    }

    public void start() {
        System.out.println("===================================");
        System.out.println("  WELCOME TO THE ESCAPE ROOM!");
        System.out.println("===================================");
        System.out.println("Find your way to the exit!");
        System.out.println("Type 'help' for commands.\n");

        while (true) {
            try {
                turnCounter++;

                // Show hint every 3 turns
                if (turnCounter % 3 == 0 && !hintQueue.isEmpty()) {
                    System.out.println("\n[HINT] " + hintQueue.poll());
                }

                System.out.print("\n> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) continue;

                processCommand(input);

                if (winConditionCheck()) {
                    System.out.println("\n***********************************");
                    System.out.println("  CONGRATULATIONS! YOU ESCAPED!");
                    System.out.println("***********************************");
                    System.out.println("You completed the game in " + turnCounter + " turns!");
                    break;
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void processCommand(String cmd) throws InvalidCommandException {
        String[] parts = cmd.toLowerCase().split(" ", 2);
        String command = parts[0];

        try {
            switch (command) {
                case "help":
                    showHelp();
                    break;

                case "look":
                    player.getCurrentRoom().inspect();
                    break;

                case "move":
                    if (parts.length < 2) {
                        System.out.println("Move where? Usage: move <room name>");
                        break;
                    }
                    handleMove(parts[1]);
                    break;

                case "back":
                    player.goBack();
                    break;

                case "pickup":
                    if (parts.length < 2) {
                        System.out.println("Pickup what? Usage: pickup <item name>");
                        break;
                    }
                    player.pickupItem(parts[1]);
                    break;

                case "inventory":
                    player.showInventory();
                    break;

                case "sort":
                    player.sortInventoryByValue();
                    player.showInventory();
                    break;

                case "solve":
                    if (parts.length < 2) {
                        System.out.println("Solve what? Usage: solve <puzzle name>");
                        break;
                    }
                    handleSolve(parts[1]);
                    break;

                case "inspect":
                    if (parts.length < 2) {
                        System.out.println("Inspect what? Usage: inspect <item/puzzle name>");
                        break;
                    }
                    handleInspect(parts[1]);
                    break;

                case "map":
                    System.out.println("\n=== Game Map (Recursive) ===");
                    map.get(0).exploreRecursive(0);
                    break;

                case "status":
                    printStatus();
                    break;

                case "quit":
                    System.out.println("Thanks for playing!");
                    System.exit(0);
                    break;

                default:
                    throw new InvalidCommandException("Unknown command: " + command + ". Type 'help' for available commands.");
            }
        } catch (LockedRoomException e) {
            System.out.println(e.getMessage());
        } catch (InvalidPuzzleAnswerException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleMove(String roomName) throws LockedRoomException {
        Room targetRoom = null;

        for (Room room : player.getCurrentRoom().getConnectedRooms()) {
            if (room.getName().equalsIgnoreCase(roomName)) {
                targetRoom = room;
                break;
            }
        }

        if (targetRoom != null) {
            player.moveTo(targetRoom);
        } else {
            System.out.println("You can't go there from here!");
        }
    }

    private void handleSolve(String puzzleName) throws InvalidPuzzleAnswerException {
        Puzzle targetPuzzle = null;

        for (GameComponent component : player.getCurrentRoom().getContents()) {
            if (component instanceof Puzzle && component.getName().equalsIgnoreCase(puzzleName)) {
                targetPuzzle = (Puzzle) component;
                break;
            }
        }

        if (targetPuzzle == null) {
            System.out.println("Puzzle not found: " + puzzleName);
            return;
        }

        targetPuzzle.inspect();
        System.out.print("Enter your answer: ");
        String answer = scanner.nextLine().trim();

        if (targetPuzzle.attemptSolve(answer)) {
            // Give reward if puzzle solved
            if (targetPuzzle instanceof RiddlePuzzle) {
                Item reward = ((RiddlePuzzle) targetPuzzle).getReward();
                if (reward != null) {
                    player.addToInventory(reward);
                }
            } else if (targetPuzzle instanceof CodePuzzle) {
                Item reward = ((CodePuzzle) targetPuzzle).getReward();
                if (reward != null) {
                    player.addToInventory(reward);
                }
            }
        }
    }

    private void handleInspect(String name) {
        for (GameComponent component : player.getCurrentRoom().getContents()) {
            if (component.getName().equalsIgnoreCase(name)) {
                component.inspect();
                return;
            }
        }
        System.out.println("Not found: " + name);
    }

    private void printStatus() {
        System.out.println("\n=== Status ===");
        System.out.println("Current Room: " + player.getCurrentRoom().getName());
        System.out.println("Turn: " + turnCounter);
        System.out.println("Items in inventory: " + player.getInventory().size());
    }

    private boolean winConditionCheck() {
        return player.getCurrentRoom().isExit();
    }

    private void showHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("look              - Look around current room");
        System.out.println("move <room>       - Move to a connected room");
        System.out.println("back              - Go back to previous room");
        System.out.println("pickup <item>     - Pick up an item");
        System.out.println("inventory         - Show your inventory");
        System.out.println("sort              - Sort inventory by value");
        System.out.println("inspect <name>    - Inspect an item or puzzle");
        System.out.println("solve <puzzle>    - Attempt to solve a puzzle");
        System.out.println("map               - Show game map (recursive)");
        System.out.println("status            - Show game status");
        System.out.println("help              - Show this help");
        System.out.println("quit              - Exit game");
    }

    public static void main(String[] args) {
        GameEngine game = new GameEngine();
        game.start();
    }
}
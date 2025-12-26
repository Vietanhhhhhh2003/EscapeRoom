public class CodePuzzle extends Puzzle {
    private String code;
    private String hint;
    private Item reward;

    public CodePuzzle(String name, int difficulty, String code, String hint, Item reward) {
        super(name, difficulty);
        this.code = code;
        this.hint = hint;
        this.reward = reward;
    }

    @Override
    public void inspect() {
        System.out.println("[Code Puzzle] " + name);
        System.out.println("Hint: " + hint);
        if (solved) {
            System.out.println("Status: SOLVED");
        } else {
            System.out.println("Difficulty: " + difficulty);
        }
    }

    @Override
    public boolean attemptSolve(String answer) throws InvalidPuzzleAnswerException {
        if (solved) {
            System.out.println("This puzzle is already solved!");
            return true;
        }

        if (answer == null || answer.trim().isEmpty()) {
            throw new InvalidPuzzleAnswerException("Code cannot be empty!");
        }

        if (answer.equals(code)) {
            solved = true;
            System.out.println("Code accepted! The puzzle is solved!");
            if (reward != null) {
                System.out.println("You received: " + reward.getName());
            }
            return true;
        } else {
            System.out.println("Wrong code. Try again!");
            return false;
        }
    }

    public Item getReward() {
        return reward;
    }
}
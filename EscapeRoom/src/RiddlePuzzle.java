public class RiddlePuzzle extends Puzzle {
    private String riddle;
    private String correctAnswer;
    private Item reward;

    public RiddlePuzzle(String name, int difficulty, String riddle, String answer, Item reward) {
        super(name, difficulty);
        this.riddle = riddle;
        this.correctAnswer = answer.toLowerCase();
        this.reward = reward;
    }

    @Override
    public void inspect() {
        System.out.println("[Riddle Puzzle] " + name);
        System.out.println("Riddle: " + riddle);
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
            throw new InvalidPuzzleAnswerException("Answer cannot be empty!");
        }

        if (answer.toLowerCase().equals(correctAnswer)) {
            solved = true;
            System.out.println("Correct! The puzzle is solved!");
            if (reward != null) {
                System.out.println("You received: " + reward.getName());
            }
            return true;
        } else {
            System.out.println("Wrong answer. Try again!");
            return false;
        }
    }

    public Item getReward() {
        return reward;
    }
}
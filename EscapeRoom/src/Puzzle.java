// Abstract Puzzle class with Comparable for sorting by difficulty
public abstract class Puzzle extends GameComponent implements Comparable<Puzzle> {
    protected int difficulty;
    protected boolean solved;

    public Puzzle(String name, int difficulty) {
        super(name);
        this.difficulty = difficulty;
        this.solved = false;
    }

    public abstract boolean attemptSolve(String answer) throws InvalidPuzzleAnswerException;

    // Compare by difficulty
    @Override
    public int compareTo(Puzzle other) {
        return Integer.compare(this.difficulty, other.difficulty);
    }

    public boolean isSolved() {
        return solved;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
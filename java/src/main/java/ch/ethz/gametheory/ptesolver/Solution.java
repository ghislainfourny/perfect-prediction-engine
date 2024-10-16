package ch.ethz.gametheory.ptesolver;

public class Solution<T extends Comparable<T>> {
	int[] actions;
	T[][] outcome;
	
	public Solution(int[] actions, T[][] outcome)
	{
		this.actions = actions;
		this.outcome = outcome;
	}
	
	public int[] getSolutionPath() {
		return actions;
	}
	public T[][] getOutcome() {
		return outcome;
	}
}

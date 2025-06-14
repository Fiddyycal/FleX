package org.fukkit.scoreboard.entry;

import io.flex.commons.cache.cell.Cell;

public class ScoredTeamEntry extends TeamEntry {

	private int score;
	
	private Cell<Integer> auto;
	
	public ScoredTeamEntry(int score) {
		this(score, "");
	}
	
	public ScoredTeamEntry(int score, String line) {
		
		super(line);
		
		this.score = score;
		
	}
	
	public ScoredTeamEntry(int score, String prefix, String suffix) {
		
		super(prefix, suffix);
		
		this.score = score;
		
	}
	
	public ScoredTeamEntry(int score, String prefix, Cell<?> attribute) {
		
		super(prefix, attribute);
		
		this.score = score;
		
	}
	
	public ScoredTeamEntry(int score, Cell<?> prefix, Cell<?> suffix) {
		
		super(prefix, suffix);
		
		this.score = score;
		
	}
	
	public ScoredTeamEntry(Cell<Integer> score, String line) {
		
		super(line);
		
		this.auto = score;
		
	}
	
	public ScoredTeamEntry(Cell<Integer> score, String prefix, String suffix) {
		
		super(prefix, suffix);
		
		this.auto = score;
		
	}
	
	public ScoredTeamEntry(Cell<Integer> score, String prefix, Cell<?> attribute) {
		
		super(prefix, attribute);
		
		this.auto = score;
		
	}
	
	public ScoredTeamEntry(Cell<Integer> score, Cell<?> prefix, Cell<?> suffix) {
		
		super(prefix, suffix);
		
		this.auto = score;
		
	}
	
	public int getScore() {
		return this.auto != null ? this.auto.a() : this.score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setScore(Cell<Integer> score) {
		this.auto = score;
	}

}

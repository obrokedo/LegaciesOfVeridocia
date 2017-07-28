package mb.fc.game.dev;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import mb.fc.game.sprite.CombatSprite;

public class BattleOptimizer 
{
	private int plusAll = 1;
	private int wonAtPlus;
	private int lostAtPlus;
	private float threshold = .5f;
	private int checkPermutation = -1;
	private final static int BATTLES_PER = 10;
	private int bestPermutation = 0;
	private float bestPermutationResult = 1f;
	
	private int[][] permutations = {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1},
			{1, 1, 0, 0},
			{1, 0, 1, 0},
			{1, 0, 0, 1},
			{0, 1, 1, 0},
			{0, 1, 0, 1},
			{0, 0, 1, 1},
			{1, 1, 1, 0},
			{1, 0, 1, 1},
			{1, 1, 0, 1},
			{0, 1, 1, 1}}; 
	
	public void modifyStats(CombatSprite cs)
	{
		cs.setMaxHP(cs.getMaxHP() + plusAll);
		cs.setMaxAttack(cs.getMaxAttack() + plusAll);
		cs.setMaxDefense(cs.getMaxDefense() + plusAll);
		cs.setMaxSpeed(cs.getMaxSpeed() + plusAll);
		
		if (checkPermutation != -1)
		{
			cs.setMaxHP(cs.getMaxHP() + permutations[checkPermutation][0]);
			cs.setMaxAttack(cs.getMaxAttack() + permutations[checkPermutation][1]);
			cs.setMaxDefense(cs.getMaxDefense() + permutations[checkPermutation][2]);
			cs.setMaxSpeed(cs.getMaxSpeed() + permutations[checkPermutation][3]);
		}
	}
	
	public void lostBattle()
	{
		this.lostAtPlus++;
		this.endBattle();
	}
	
	public void wonBattle()
	{
		this.wonAtPlus++;
		this.endBattle();
	}
	
	private void endBattle()
	{
		if (wonAtPlus + lostAtPlus == BATTLES_PER)
		{
			try
			{
				PrintWriter pw;
				float winRate = (1.0f * wonAtPlus / 10.0f);
				pw = new PrintWriter(new FileWriter("/home/user/Results", true));
				if (checkPermutation == -1)
				{	
					pw.write("Results for adding " + plusAll + " to all stats: " + winRate);
					if (winRate > threshold)
					{
						plusAll++;
					}
					else if (plusAll > 0)
					{
						plusAll--;
						checkPermutation = 0;
					}
					else 
					{
						pw.write("Win rate for BASE STATS: " + winRate + " are likely to high");
						pw.flush();
						pw.close();
						System.exit(0);
					}
				}
				else
				{
					String s = "";
					for (int i : permutations[checkPermutation])
						s = s + " " + i;
					pw.write("Results for adding permutation" + s + ": " + winRate);
					
					if (permutations.length > checkPermutation + 1)
					{
						if (winRate > threshold)
						{
							if (winRate < bestPermutationResult)
							{
								bestPermutation = checkPermutation;
								bestPermutationResult = winRate;
							}
						}
						
						checkPermutation++;
					}
					else
					{
						s = "";
						for (int i : permutations[bestPermutation])
							s = s + " " + permutations[bestPermutation][i];
						pw.write("Best result with win rate: " + bestPermutationResult + " plus to all: " + plusAll + " and permutation:" + s);
						pw.flush();
						pw.close();
						System.exit(0);
					}
				}
				wonAtPlus = 0;
				lostAtPlus = 0;
				pw.write("\n");
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}

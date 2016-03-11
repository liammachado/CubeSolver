package cubesolver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CubeSolver {

	private static abstract class Move { //Framework for each of the possible moves that can be applied to a Rubik's Cube.

		final String NAME; //The name of the move.  The solution is made up of a series of these.

		Move(String NAME) {
			this.NAME = NAME;
		}

		abstract void move(int[] state); //These methods are left to be implemented by the subclasses.   
		abstract void inverseMove(int[] state); 

		void cornerOrientationSwap90(int[] state, int c1, int c2, int c3, int c4) { //utility methods
			int tmp = state[c1];
			state[c1] = state[c2];
			state[c2] = state[c3];
			state[c3] = state[c4];
			state[c4] = tmp;
		}

		void edgeOrientationSwap90(int[] state, int e1, int e2, int e3, int e4) { //utility methods
			int tmp = state[e1 + 8];
			state[e1 + 8] = state[e2 + 8];
			state[e2 + 8] = state[e3 + 8];
			state[e3 + 8] = state[e4 + 8];
			state[e4 + 8] = tmp;
		}

		void cornerOrientationSwap180(int[] state, int c1, int c2, int c3, int c4) { //utility methods
			int tmp = state[c1];
			state[c1] = state[c2];
			state[c2] = tmp;

			tmp = state[c3];
			state[c3] = state[c4];
			state[c4] = tmp;
		}

		void edgeOrientationSwap180(int[] state, int e1, int e2, int e3, int e4) { //utility methods
			int tmp = state[e1 + 8];
			state[e1 + 8] = state[e2 + 8];
			state[e2 + 8] = tmp;

			tmp = state[e3 + 8];
			state[e3 + 8] = state[e4 + 8];
			state[e4 + 8] = tmp;
		}

		void cornerOrientationCycle90(int[] state, int c1, int c2, int c3, int c4) { //utility methods
			int tmp = (state[c1] + 1) % 3;
			state[c1] = (state[c2] + 2) % 3;
			state[c2] = (state[c3] + 1) % 3;
			state[c3] = (state[c4] + 2) % 3;
			state[c4] = tmp;
		}

		void edgeOrientationCycle90(int[] state, int e1, int e2, int e3, int e4) { //utility methods
			int tmp = state[e1 + 8] ^ 1;
			state[e1 + 8] = state[e2 + 8] ^ 1;
			state[e2 + 8] = state[e3 + 8] ^ 1;
			state[e3 + 8] = state[e4 + 8] ^ 1;
			state[e4 + 8] = tmp;
		}

		void cornerPermutationSwap90(int[] state, int c1, int c2, int c3, int c4) { //utility methods
			int tmp = state[c1 + 20];
			state[c1 + 20] = state[c2 + 20];
			state[c2 + 20] = state[c3 + 20];
			state[c3 + 20] = state[c4 + 20];
			state[c4 + 20] = tmp;
		}

		void edgePermutationSwap90(int[] state, int e1, int e2, int e3, int e4) { //utility methods
			int tmp = state[e1 + 28];
			state[e1 + 28] = state[e2 + 28];
			state[e2 + 28] = state[e3 + 28];
			state[e3 + 28] = state[e4 + 28];
			state[e4 + 28] = tmp;
		}

		void cornerPermutationSwap180(int[] state, int c1, int c2, int c3, int c4) { //utility methods
			int tmp = state[c1 + 20];
			state[c1 + 20] = state[c2 + 20];
			state[c2 + 20] = tmp;

			tmp = state[c3 + 20];
			state[c3 + 20] = state[c4 + 20];
			state[c4 + 20] = tmp;
		}

		void edgePermutationSwap180(int[] state, int e1, int e2, int e3, int e4) { //utility methods
			int tmp = state[e1 + 28];
			state[e1 + 28] = state[e2 + 28];
			state[e2 + 28] = tmp;

			tmp = state[e3 + 28];
			state[e3 + 28] = state[e4 + 28];
			state[e4 + 28] = tmp;
		}
	}

	private static abstract class Phase {	//A framework for each of the phases in the Thistlewaite algorithm.

		final int[] PHASE_MOVES; //The moves that are allowed within this phase
		final int SIZE;	//The total number of possible states in this phase
		boolean[] visited;
		static int numSingleTurns;

		Phase(int size, int[] phaseMoves) {
			PHASE_MOVES = phaseMoves;
			SIZE = size;
		}

		abstract int asInteger(int[] state); //This method returns a number based on the orientations/permutations of the corners/edges.
		//If this number equals 0, then the current phase has been solved.

		List<String> sol = Arrays.asList("U2", "D", "R", "B", "   ", 
				"R'", "L2", "D'", "L2", "U'", "R'", "D2", "L", "   ", 
				"U'", "L2", "U", "F2", "U", "R2", "U", "B2", "U", "R2", "L2");

		List<String> toPhase(int[] initState, List<String> initSolution) {

			visited = new boolean[SIZE];
			Queue<CubeState> queue = new ArrayDeque<CubeState>();
			queue.add(new CubeState(initState, initSolution, numSingleTurns));
			visited[asInteger(initState)] = true;

			while (!queue.isEmpty()) {

				CubeState curState = queue.poll();

				numSingleTurns = curState.numSingleTurns;

				if (asInteger(curState.state) == 0) {
					System.arraycopy(curState.state, 0, initState, 0, 40);
					return curState.solution;
				}

				List<Integer> toMarkAsVisited = new ArrayList<>();
				for (int i = 0; i < PHASE_MOVES.length; ++i) {

					int[] newState = new int[40];
					System.arraycopy(curState.state, 0, newState, 0, 40);
					MOVES[PHASE_MOVES[i]].move(newState);
					numSingleTurns += NUM_SINGLE_TURNS[PHASE_MOVES[i]];
					int nextInt = asInteger(newState);

					if (!visited[nextInt]) {
						toMarkAsVisited.add(nextInt);
						List<String> newSol = new ArrayList<String>(curState.solution);
						newSol.add(MOVES[PHASE_MOVES[i]].NAME);
						queue.add(new CubeState(newState, newSol, numSingleTurns));
					}
					numSingleTurns -= NUM_SINGLE_TURNS[PHASE_MOVES[i]];
				}
				for (int i = 0; i < toMarkAsVisited.size(); ++i) {
					visited[toMarkAsVisited.get(i)] = true;
				}
			}
			numSingleTurns = -1;
			return null; 
		}

		class CubeState {

			int[] state;
			List<String> solution;
			int numSingleTurns;

			CubeState(int[] state, List<String> solution, int numSingleTurns) {
				this.state = state;
				this.solution = solution;
				this.numSingleTurns = numSingleTurns;
			}
		}
	}

	private static final Move[] MOVES = { //The 18 possible moves that can be applied to a Rubik's Cube
			new Move("U") {

				void move(int[] state) { 

					cornerOrientationSwap90(state, 3, 2, 1, 0);
					edgeOrientationSwap90(state, 3, 2, 1, 0);
					cornerPermutationSwap90(state, 3, 2, 1, 0);				
					edgePermutationSwap90(state, 3, 2, 1, 0);

				}

				void inverseMove(int[] state) {
					MOVES[1].move(state);
				}

			}, 
			new Move("U'") {

				void move(int[] state) { 

					cornerOrientationSwap90(state, 0, 1, 2, 3);
					edgeOrientationSwap90(state, 0, 1, 2, 3);
					cornerPermutationSwap90(state, 0, 1, 2, 3);				
					edgePermutationSwap90(state, 0, 1, 2, 3);

				}

				void inverseMove(int[] state) {
					MOVES[0].move(state);
				}

			}, 
			new Move("U2") {

				void move(int[] state) { 

					cornerOrientationSwap180(state, 0, 2, 1, 3);
					edgeOrientationSwap180(state, 0, 2, 1, 3);
					cornerPermutationSwap180(state, 0, 2, 1, 3);				
					edgePermutationSwap180(state, 0, 2, 1, 3);

				}

				void inverseMove(int[] state) {
					move(state);
				}

			}, new Move("D") {

				void move(int[] state) { 

					cornerOrientationSwap90(state, 4, 5, 6, 7);
					edgeOrientationSwap90(state, 8, 9, 10, 11);
					cornerPermutationSwap90(state, 4, 5, 6, 7);				
					edgePermutationSwap90(state, 8, 9, 10, 11);

				}

				void inverseMove(int[] state) {
					MOVES[4].move(state);
				}

			}, new Move("D'") {

				void move(int[] state) { 

					cornerOrientationSwap90(state, 7, 6, 5, 4);
					edgeOrientationSwap90(state, 11, 10, 9, 8);
					cornerPermutationSwap90(state, 7, 6, 5, 4);				
					edgePermutationSwap90(state, 11, 10, 9, 8);

				}

				void inverseMove(int[] state) {
					MOVES[3].move(state);
				}

			}, new Move("D2") {

				void move(int[] state) { 

					cornerOrientationSwap180(state, 4, 6, 5, 7);
					edgeOrientationSwap180(state, 8, 10, 9, 11);
					cornerPermutationSwap180(state, 4, 6, 5, 7);				
					edgePermutationSwap180(state, 8, 10, 9, 11);

				}

				void inverseMove(int[] state) {
					move(state);
				}

			}, new Move("R") {

				void move(int[] state) {

					cornerOrientationCycle90(state, 0, 4, 7, 3);
					edgeOrientationSwap90(state, 3, 4, 11, 7);			
					cornerPermutationSwap90(state, 0, 4, 7, 3);				
					edgePermutationSwap90(state, 3, 4, 11, 7);

				}

				void inverseMove(int[] state) {
					MOVES[7].move(state);
				}

			}, new Move("R'") {

				void move(int[] state) {

					cornerOrientationCycle90(state, 0, 3, 7, 4);
					edgeOrientationSwap90(state, 3, 7, 11, 4);			
					cornerPermutationSwap90(state, 0, 3, 7, 4);				
					edgePermutationSwap90(state, 3, 7, 11, 4);

				}

				void inverseMove(int[] state) {
					MOVES[6].move(state);
				}

			}, new Move("R2") {

				void move(int[] state) { 

					cornerOrientationSwap180(state, 0, 7, 3, 4);
					edgeOrientationSwap180(state, 3, 11, 4, 7);			
					cornerPermutationSwap180(state, 0, 7, 3, 4);				
					edgePermutationSwap180(state, 3, 11, 4, 7);

				}

				void inverseMove(int[] state) {
					move(state);
				}

			}, new Move("L") {

				void move(int[] state) {

					cornerOrientationCycle90(state, 2, 6, 5, 1);
					edgeOrientationSwap90(state, 1, 6, 9, 5);			
					cornerPermutationSwap90(state, 2, 6, 5, 1);				
					edgePermutationSwap90(state, 1, 6, 9, 5);

				}

				void inverseMove(int[] state) {
					MOVES[10].move(state);
				}

			}, new Move("L'") {

				void move(int[] state) {

					cornerOrientationCycle90(state, 2, 1, 5, 6);
					edgeOrientationSwap90(state, 1, 5, 9, 6);			
					cornerPermutationSwap90(state, 2, 1, 5, 6);				
					edgePermutationSwap90(state, 1, 5, 9, 6);

				}

				void inverseMove(int[] state) {
					MOVES[9].move(state);
				}

			}, new Move("L2") {

				void move(int[] state) { 

					cornerOrientationSwap180(state, 1, 6, 2, 5);
					edgeOrientationSwap180(state, 1, 9, 5, 6);
					cornerPermutationSwap180(state, 1, 6, 2, 5);				
					edgePermutationSwap180(state, 1, 9, 5, 6);

				}

				void inverseMove(int[] state) {
					move(state);
				}

			}, new Move("F") {

				void move(int[] state) { 

					cornerOrientationCycle90(state, 1, 5, 4, 0);
					edgeOrientationCycle90(state, 0, 5, 8, 4);			
					cornerPermutationSwap90(state, 1, 5, 4, 0);				
					edgePermutationSwap90(state, 0, 5, 8, 4);

				}

				void inverseMove(int[] state) {
					MOVES[13].move(state);
				}

			}, new Move("F'") {

				void move(int[] state) { 

					cornerOrientationCycle90(state, 1, 0, 4, 5);
					edgeOrientationCycle90(state, 0, 4, 8, 5);			
					cornerPermutationSwap90(state, 1, 0, 4, 5);				
					edgePermutationSwap90(state, 0, 4, 8, 5);

				}

				void inverseMove(int[] state) {
					MOVES[12].move(state);
				}

			}, new Move("F2") {

				void move(int[] state) { 

					cornerOrientationSwap180(state, 0, 5, 1, 4);
					edgeOrientationSwap180(state, 0, 8, 4, 5);
					cornerPermutationSwap180(state, 0, 5, 1, 4);				
					edgePermutationSwap180(state, 0, 8, 4, 5);

				}

				void inverseMove(int[] state) {
					move(state);
				}

			}, new Move("B") {

				void move(int[] state) { 

					cornerOrientationCycle90(state, 3, 7, 6, 2);
					edgeOrientationCycle90(state, 2, 7, 10, 6);			
					cornerPermutationSwap90(state, 3, 7, 6, 2);				
					edgePermutationSwap90(state, 2, 7, 10, 6);

				}

				void inverseMove(int[] state) {
					MOVES[16].move(state);
				}

			}, new Move("B'") {

				void move(int[] state) { 

					cornerOrientationCycle90(state, 3, 2, 6, 7);
					edgeOrientationCycle90(state, 2, 6, 10, 7);			
					cornerPermutationSwap90(state, 3, 2, 6, 7);				
					edgePermutationSwap90(state, 2, 6, 10, 7);

				}

				void inverseMove(int[] state) {
					MOVES[15].move(state);
				}

			}, new Move("B2") {

				void move(int[] state) { 

					cornerOrientationSwap180(state, 3, 6, 2, 7);			
					edgeOrientationSwap180(state, 2, 10, 6, 7);			
					cornerPermutationSwap180(state, 3, 6, 2, 7);				
					edgePermutationSwap180(state, 2, 10, 6, 7);

				}

				void inverseMove(int[] state) {
					move(state);
				}

			}
	};

	private static final Phase[] PHASES = { //The 4 phases that the Thistlewaite algorithm is comprised of.

			new Phase(2048, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }) {

				int asInteger(int[] state) {
					int stateInt = 0;
					for (int i = 8; i < 19; ++i) {
						stateInt = (stateInt << 1) | state[i];
					}
					return stateInt;
				}

			}, 
			new Phase(1082565, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 17 }) {

				int[] pow3 = { 1, 3, 9, 27, 81, 243, 729 };

				int asInteger(int[] state) {
					int stateInt = 0, edgesFound = 0;
					for (int i = 0; i < 7; ++i) 
						stateInt += 495 * pow3[6 - i] * state[i];

					for (int i = 0; i < 3; ++i)
						for (int j = 0; j < 4; ++j)
							if (SLICES[state[SLICE_EDGES[i][j] + 28]] == 2) ++edgesFound;
							else if (edgesFound > 0) stateInt += COMBINATIONS[4 * i + j][edgesFound - 1];

					return stateInt;
				}

			}, 
			new Phase(33554432, new int[] { 0, 1, 2, 3, 4, 5, 8, 11, 14, 17 }) {

				int asInteger(int[] state) {
					int number = 131072 * slicePlacement(state);
					number += 512 * tetradPlacement(state);
					number += 2 * cornerDuoPlacement(state);
					number += parity(state);
					return number;
				}

				//LR slice
				int slicePlacement(int[] state) {
					int number = 1 << SLICE_ORDER[state[28]];
					number |= 1 << SLICE_ORDER[state[30]];
					number |= 1 << SLICE_ORDER[state[36]];
					number |= 1 << SLICE_ORDER[state[38]];
					return number - 15;
				}

				//UFR tetrad
				int tetradPlacement(int[] state) {
					int number = 1 << TETRAD_ORDER[state[20]];
					number |= 1 << TETRAD_ORDER[state[22]];
					number |= 1 << TETRAD_ORDER[state[25]];
					number |= 1 << TETRAD_ORDER[state[27]];
					return number - 15;
				}

				int cornerDuoPlacement(int[] state) {
					int number = 1 << DUOS[state[20]];
					number |= 1 << DUOS[state[22]];
					number -= 3;
					number <<= 4;
					number |= 1 << DUOS[state[21]];
					number |= 1 << DUOS[state[23]];
					number -= 3;
					return number;
				}

				int parity(int[] state) {
					return numSingleTurns % 2;
				}
			}, 
			new Phase(22663552, new int[] { 2, 5, 8, 11, 14, 17 }) {
				int asInteger(int[] state) {
					int stateInt = 0;
					int[] cornerPermOrder = { 0, 0, 1, 1, 2, 2, 3, 3 };
					int[] edgePermOrder = { 0, 0, 1, 1, 0, 1, 2, 3, 3, 3, 2, 2 };
					for (int i = 0; i < 2; ++i)
						for (int j = 0, factorial = 6; j < 4; factorial /= (4 - ++j == 0 ? 1 : 4 - j)) {
							stateInt += Math.pow(24, 4 - i) * factorial * cornerPermOrder[state[TETRAD_CORNERS[i][j] + 20]];
							for (int k = 0; k < 4; ++k)
								if (cornerPermOrder[state[TETRAD_CORNERS[i][k] + 20]] > cornerPermOrder[state[TETRAD_CORNERS[i][j] + 20]]) 
									--cornerPermOrder[state[TETRAD_CORNERS[i][k] + 20]];
						}

					for (int i = 0; i < 3; ++i)
						for (int j = 0, factorial = 6; j < 4; factorial /= (4 - ++j == 0 ? 1 : 4 - j)) {
							stateInt += Math.pow(24, 2 - i) * factorial * edgePermOrder[state[SLICE_EDGES[i][j] + 28]];
							for (int k = 0; k < 4; ++k)
								if (edgePermOrder[state[SLICE_EDGES[i][k] + 28]] > edgePermOrder[state[SLICE_EDGES[i][j] + 28]]) 
									--edgePermOrder[state[SLICE_EDGES[i][k] + 28]];
						}

					return stateInt;
				}			
			}
	};

	private static final int[] NUM_SINGLE_TURNS = { 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0 };
	private static final int[] SLICE_ORDER = { 0, 4, 1, 5, -1, -1, -1, -1, 3, 7, 2, 6 };
	private static final int[] TETRAD_ORDER = { 0, 4, 1, 5, 6, 2, 7, 3 };
	private static final int[] DUOS = { 0, 0, 1, 1, 2, 2, 3, 3 }; 
	private static final int[] SLICES = { 0, 1, 0, 1, 2, 2, 2, 2, 0, 1, 0, 1 }; //Table to lookup which of the three slices each edge belongs in.
	private static final int[][] SLICE_EDGES = { //Reverse of SLICES table.
			{ 0, 2, 10, 8 },
			{ 1, 3, 11, 9 },
			{ 4, 5, 6, 7 }
	};
	private static final int[][] TETRAD_CORNERS = { //Reverse of TETRADS table.
			{ 0, 2, 5, 7 },
			{ 1, 3, 4, 6 }
	};
	private static final int[][] COMBINATIONS = { // rCc , where r is the row and c is the column.
			{ 1 },
			{ 1, 1 },
			{ 1, 2, 1 }, 
			{ 1, 3, 3, 1 }, 
			{ 1, 4, 6, 4 }, 
			{ 1, 5, 10, 10 }, 
			{ 1, 6, 15, 20 }, 
			{ 1, 7, 21, 35 }, 
			{ 1, 8, 28, 56 }, 
			{ 1, 9, 36, 84 },
			{ 1, 10, 45, 120 },
			{ 1, 11, 55, 165 }
	};
	private static final Map<String, Integer> MOVE_TO_INDEX;
	static {
		MOVE_TO_INDEX = new HashMap<String, Integer>();
		MOVE_TO_INDEX.put("U", 0);
		MOVE_TO_INDEX.put("U'", 1);
		MOVE_TO_INDEX.put("U2", 2);
		MOVE_TO_INDEX.put("D", 3);
		MOVE_TO_INDEX.put("D'", 4);
		MOVE_TO_INDEX.put("D2", 5);
		MOVE_TO_INDEX.put("R", 6);
		MOVE_TO_INDEX.put("R'", 7);
		MOVE_TO_INDEX.put("R2", 8);
		MOVE_TO_INDEX.put("L", 9);
		MOVE_TO_INDEX.put("L'", 10);
		MOVE_TO_INDEX.put("L2", 11);
		MOVE_TO_INDEX.put("F", 12);
		MOVE_TO_INDEX.put("F'", 13);
		MOVE_TO_INDEX.put("F2", 14);
		MOVE_TO_INDEX.put("B", 15);
		MOVE_TO_INDEX.put("B'", 16);
		MOVE_TO_INDEX.put("B2", 17);
	}

	public static int[] transform(String scramble) {
		String[] moves = scramble.split(" ");
		int[] config = new int[40];
		for (int i = 21; i < 28; ++i) {
			config[i] = config[i - 1] + 1;
		}
		for (int i = 29; i < 40; ++i) {
			config[i] = config[i - 1] + 1;
		}
		if (scramble.equals("")) return config;

		for (String s : moves) {
			MOVES[MOVE_TO_INDEX.get(s)].move(config);
		}

		return config;
	}

	private static List<String> solve(String scramble, int numSingleTurns) {
		Phase.numSingleTurns = numSingleTurns;
		int[] state = transform(scramble);
		List<String> list = PHASES[0].toPhase(state, new ArrayList<String>());
		System.out.println(Phase.numSingleTurns);
		System.out.println("Phase 1 done...");
		list.add("   ");
		list = PHASES[1].toPhase(state, list);
		System.out.println(Phase.numSingleTurns);
		System.out.println("Phase 2 done...");
		list.add("   ");
		list = PHASES[2].toPhase(state, list);
		System.out.println(Phase.numSingleTurns);
		System.out.println("Phase 3 done...");
		list.add("   ");
		list = PHASES[3].toPhase(state, list);
		if (list == null) {
			throw new RuntimeException();
		}
		return list;
	}

	public static List<String> solve(String scramble) { //Main solving method.
		try {
			return solve(scramble, 0);
		} catch (Exception e) {
			System.out.println("Parity error! Resolving...");
			return solve(scramble, 1);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Enter cube state: ");
		String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
		long startTime = System.nanoTime();
		List<String> sol = solve(input);
		long endTime = System.nanoTime();
		System.out.println("Solution: " + Arrays.toString(sol.toArray()));
		System.out.println("Length: " + (sol.size() - 3));
		System.out.println("Total time: " + (endTime - startTime) / 1000000 + " ms");
	}
}

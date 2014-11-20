import java.io.*;
import java.math.*;
import java.util.*;

public class QuadSieve {
	BigInteger N = new BigInteger("16637");
	int Fbelopp = 10;
	int L = Fbelopp + 30;
	int[][] matrix = new int[L][Fbelopp];
	BigInteger[] rs = new BigInteger[L];
	ArrayList<boolean[]> solutions = new ArrayList<boolean[]>();
	ArrayList<Integer> primes = new ArrayList<Integer>();

	public static void main(String[] args) {
		QuadSieve q = new QuadSieve();
		q.run();
	}

	public void run() {
		readPrimes();
		formMatrix();
		getSolutions();
		trySolutions();
	}

	public void trySolutions() {
		for (boolean[] solution : solutions) {
			System.out.print(".");
			BigInteger factor = candidateFactor(solution);
			BigInteger gcd = factor.gcd(N);
			if (gcd.equals(BigInteger.ONE) || gcd.equals(N) || gcd.equals(0))
				continue;
			System.out.println(gcd);
			System.out.printf("%s = %s * %s\n", N.toString(),
					gcd.toString(), N.divide(gcd).toString());
			return;
		}
		// No solutions?
		System.out.println("Candidate list exhausted, factorisation fails.");
	}

	public BigInteger candidateFactor(boolean[] aSolution) {
		BigInteger factor = BigInteger.ONE;
		BigInteger leftSide = BigInteger.ONE;
		int[] specificSolution = new int[Fbelopp];
		for (int i = 0; i < L; i++) {
			if (!aSolution[i])
				continue;
			for (int j = 0; j < Fbelopp; j++) {
				specificSolution[j] += matrix[i][j];
				// factor = factor.multiply(
				// BigInteger.valueOf(primes.get(j)).pow(
				// matrix[i][j]/2));
			}

			leftSide = leftSide.multiply(rs[i]);
		}
		for (int i = 0; i < Fbelopp; i++) {
			if (specificSolution[i] != 0) {
				factor = factor.multiply(BigInteger.valueOf(primes.get(i)).pow(
						specificSolution[i] / 2));
			}
		}
		factor = factor.mod(N);
		leftSide = leftSide.mod(N);
		return factor.subtract(leftSide);
	}

	public void getSolutions() {
		PrintStream ps = null;
		try {
			ps = new PrintStream(new File("in"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		ps.print(L);
		ps.print(" ");
		ps.println(Fbelopp);

		for (int i = 0; i < L; i++) {
			for (int j = 0; j < Fbelopp; j++) {
				ps.print(matrix[i][j] % 2);
				ps.print(" ");
			}
			ps.println();
		}

		ps.close();

		try {
			new File("out").createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		@SuppressWarnings("unused")
		Process proc;
		try {
			proc = Runtime.getRuntime().exec("./gaussbin in out");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner scan = null;

		try {
			scan = new Scanner(new FileReader(new File("out")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int nSolutions = scan.nextInt();

		outer: for (int i = 0; i < nSolutions; i++) {
			boolean[] row = new boolean[L];
			for (int j = 0; j < L; j++) {
				row[j] = scan.nextInt() == 0 ? false : true;
			}
			solutions.add(row);
		}
		scan.close();
	}

	public void formMatrix() {
		int limit = (int) Math.sqrt(L);
		int row = 0;

		outer: for (int i = 1; true; i++) {
			inner: for (int j = 1; j < limit; j++) {
				BigInteger r = Rgen(i, j);
				BigInteger r2 = r.multiply(r).mod(N);
				int[] theRow = factor(r2);
				if (theRow == null)
					continue;

				for (int[] existingRow : matrix) {
					boolean duplicate = true;
					for (int k = 0; k < Fbelopp; k++) {
						if (theRow[k] % 2 != existingRow[k] % 2) {
							duplicate = false;
							break;
						}
					}
					if (duplicate) {
						continue inner;
					}
				}

				for (int c = 0; c < Fbelopp; c++) {
					matrix[row][c] = theRow[c];
				}
				rs[row] = r;
				row++;
				if (row == L) {
					break outer;
				}
			}
		}
	}

	public int[] factor(BigInteger x) {
		int[] factors = new int[primes.size()];

		while (!x.equals(BigInteger.ONE)) {
			boolean nullFactor = true;
			for (int i = 0; i < primes.size(); i++) {
				BigInteger bigPrime = BigInteger.valueOf(primes.get(i));
				if (x.mod(bigPrime).equals(BigInteger.ZERO)) {
					factors[i]++;
					x = x.divide(bigPrime);
					nullFactor = false;
					break;
				}
			}
			if (nullFactor) {
				return null;
			}
		}

		return factors;
	}

	public void readPrimes() {
		Scanner scan = null;
		try {
			scan = new Scanner(new FileReader(new File("prim_2_24")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < Fbelopp; i++) {
			primes.add(scan.nextInt());
		}
		scan.close();
	}

	public BigInteger Rgen(int k, int j) {
		return squareRoot(N.multiply(BigInteger.valueOf(k))).add(
				BigInteger.valueOf(j));
	}

	public BigInteger squareRoot(BigInteger x) {
		BigInteger right = x, left = BigInteger.ZERO, mid;
		while (right.subtract(left).compareTo(BigInteger.ONE) > 0) {
			mid = (right.add(left)).shiftRight(1);
			if (mid.multiply(mid).compareTo(x) > 0)
				right = mid;
			else
				left = mid;
		}
		return left;
	}
}
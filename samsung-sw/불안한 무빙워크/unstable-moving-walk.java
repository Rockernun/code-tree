import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main {

    static int n;
    static int k;
    static int[] durability;
    static boolean[] onBoard;
    static int zeroCount;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        int totalLength = 2 * n;
        durability = new int[totalLength];
        onBoard = new boolean[totalLength];

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < durability.length; i++) {
            durability[i] = Integer.parseInt(st.nextToken());
        }

        int answer = 0;

        while (zeroCount < k) {
            answer++;

            int lastWalk = durability[totalLength - 1];
            boolean lastPerson = onBoard[totalLength - 1];

            for (int i = totalLength - 1; i > 0; i--) {
                durability[i] = durability[i - 1];
                onBoard[i] = onBoard[i - 1];
            }

            durability[0] = lastWalk;
            onBoard[0] = lastPerson;

            if (onBoard[n - 1]) {
                onBoard[n - 1] = false;
            }

            for (int i = n - 2; i >= 0; i--) {
                if (onBoard[i] && !onBoard[i + 1] && durability[i + 1] > 0) {
                    onBoard[i] = false;
                    onBoard[i + 1] = true;
                    durability[i + 1]--;
                    
                    if (durability[i + 1] == 0) {
                        zeroCount++;
                    }

                    if (i + 1 == n - 1) {
                        onBoard[i + 1] = false;
                    }
                }
            }

            if (!onBoard[0] && durability[0] != 0) {
                durability[0]--;
                onBoard[0] = true;

                if (durability[0] == 0) {
                    zeroCount++;
                }
            }
        }

        System.out.println(answer);
    }
}
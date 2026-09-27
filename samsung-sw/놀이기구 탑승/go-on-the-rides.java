import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class Main {

    static int n;
    static int[][] friends;
    static int[][] grid;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine().trim());

        int total = n * n;
        friends = new int[total + 1][4];
        grid = new int[n][n];

        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < total; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int s = Integer.parseInt(st.nextToken());
            queue.offer(s);
            for (int j = 0; j < 4; j++) {
                friends[s][j] = Integer.parseInt(st.nextToken());
            }
        }

        while (!queue.isEmpty()) {
            int student = queue.poll();
            selectSeat(student);
        }

        int score = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int student = grid[i][j];
                int friend = 0;

                for (int dir = 0; dir < 4; dir++) {
                    int nX = i + dx[dir];
                    int nY = j + dy[dir];
                    if (nX < 0 || nX >= n || nY < 0 || nY >= n) continue;

                    int neighbor = grid[nX][nY];
                    if (neighbor == 0) continue;

                    boolean isFriend = Arrays.stream(friends[student]).anyMatch(x -> x == neighbor);
                    if (isFriend) friend++;
                }

                if (friend == 1) score += 1;
                else if (friend == 2) score += 10;
                else if (friend == 3) score += 100;
                else if (friend == 4) score += 1000;
            }
        }

        System.out.println(score);
    }

    static void selectSeat(int student) {
        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] != 0) continue;

                int friend = 0, empty = 0;
                for (int dir = 0; dir < 4; dir++) {
                    int nX = i + dx[dir];
                    int nY = j + dy[dir];
                    if (nX < 0 || nX >= n || nY < 0 || nY >= n) continue;

                    int neighbor = grid[nX][nY];
                    if (neighbor == 0) {
                        empty++;
                    } else {
                        boolean isFriend = Arrays.stream(friends[student]).anyMatch(x -> x == neighbor);
                        if (isFriend) friend++;
                    }
                }

                result.add(new int[]{friend, empty, i, j});
            }
        }

        result.sort((a, b) -> {
            if (a[0] != b[0]) return b[0] - a[0];   
            if (a[1] != b[1]) return b[1] - a[1];  
            if (a[2] != b[2]) return a[2] - b[2]; 
            return a[3] - b[3];
        });

        int[] best = result.get(0);
        grid[best[2]][best[3]] = student;
    }
}
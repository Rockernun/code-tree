import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br;
    static StringTokenizer st;
    static int R;
    static int C;
    static int K;
    static int[][] forest;  // 0: 빈칸, 1~K: 골렘 번호
    static boolean[][] isExit;  // 골렘 출구 칸 표시
    static int[][] golem;
    static long answer = 0;

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }

    public static void main(String[] args) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));

        R = nextInt();
        C = nextInt();
        K = nextInt();

        forest = new int[R + 3][C]; 
        isExit = new boolean[R + 3][C];
        golem = new int[K][2];

        for (int i = 0; i < K; i++) {
            golem[i][0] = nextInt();
            golem[i][1] = nextInt();
        }

        for (int i = 0; i < K; i++) {
            int r = 1;  // 골렘 중심: 몸 전체가 숲 위에 있는 위치
            int c = golem[i][0] - 1;  // 0-indexed 열
            int d = golem[i][1];  // 출구 방향

            // 이동: 남 → 남서 → 남동 순서로, 더 못 움직일 때까지
            while (true) {
                if (canPlace(r + 1, c)) {
                    r++;
                } else if (canPlace(r, c - 1) && canPlace(r + 1, c - 1)) {
                    c--;
                    r++;
                    d = (d + 3) % 4;  // 반시계 회전
                } else if (canPlace(r, c + 1) && canPlace(r + 1, c + 1)) {
                    c++;
                    r++;
                    d = (d + 1) % 4;  // 시계 회전
                } else {
                    break;
                }
            }

            // 몸 일부가 숲 밖이면 숲을 비우고 이 정령은 점수 없음
            if (r - 1 < 3) {
                forest = new int[R + 3][C];
                isExit = new boolean[R + 3][C];
                continue;
            }

            // 골렘을 숲에 고정: 다섯 칸을 골렘 번호로 칠하고 출구 표시
            int id = i + 1;
            forest[r][c] = id;
            for (int k = 0; k < 4; k++) {
                forest[r + dx[k]][c + dy[k]] = id;
            }
            isExit[r + dx[d]][c + dy[d]] = true;

            // 정령 이동: 갈 수 있는 가장 아래 행
            int maxRow = bfs(r, c);
            answer += maxRow - 2;
        }

        System.out.println(answer);
    }

    // 중심이 (r, c)인 골렘이 놓일 수 있는지
    static boolean canPlace(int r, int c) {
        if (c - 1 < 0 || c + 1 >= C || r + 1 > R + 2) return false;
        if (forest[r][c] != 0) return false;
        for (int k = 0; k < 4; k++) {
            if (forest[r + dx[k]][c + dy[k]] != 0) return false;
        }
        return true;
    }

    // 정령이 (sr, sc)에서 출발해 도달할 수 있는 가장 큰 행
    static int bfs(int sr, int sc) {
        boolean[][] visited = new boolean[R + 3][C];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {sr, sc});
        visited[sr][sc] = true;
        int maxRow = sr;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int x = cur[0], y = cur[1];
            maxRow = Math.max(maxRow, x);

            for (int k = 0; k < 4; k++) {
                int nx = x + dx[k];
                int ny = y + dy[k];
                
                if (nx < 3 || nx > R + 2 || ny < 0 || ny >= C) continue;
                if (visited[nx][ny] || forest[nx][ny] == 0) continue;
                
                // 같은 골렘 안이면 자유롭게, 다른 골렘이면 지금 칸이 출구일 때만
                if (forest[nx][ny] == forest[x][y] || isExit[x][y]) {
                    visited[nx][ny] = true;
                    queue.offer(new int[] {nx, ny});
                }
            }
        }
        return maxRow;
    }
}
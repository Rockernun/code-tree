import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br;
    static StringTokenizer st;
    static int[][] map = new int[5][5];
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static int K;
    static int M;
    static int[] wall;  // 유적 벽면 숫자
    static int wallIdx = 0;  // 다음에 쓸 벽면 숫자 위치 (턴이 바뀌어도 이어짐)

    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }

    public static void main(String[] args) throws Exception {
        // System.setIn(new FileInputStream("res/input.txt")); 
        br = new BufferedReader(new InputStreamReader(System.in));

        K = nextInt();
        M = nextInt();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                map[i][j] = nextInt();
            }
        }
        wall = new int[M];
        for (int i = 0; i < M; i++) {
            wall[i] = nextInt();
        }

        StringBuilder sb = new StringBuilder();
        for (int turn = 1; turn <= K; turn++) {
            // 1. 탐사: 각도 → 열 → 행 순서로 돌면서 '더 클 때만' 갱신하면 동점 규칙이 자동 적용
            int best = 0;
            int[][] bestBoard = null;
            for (int n = 1; n <= 3; n++) {
                for (int y = 1; y <= 3; y++) {
                    for (int x = 1; x <= 3; x++) {
                        int[][] candidate = rotate(x, y, n);
                        int value = search(candidate, false);
                        if (value > best) {
                            best = value;
                            bestBoard = candidate;
                        }
                    }
                }
            }

            // 어떤 회전으로도 유물을 못 얻으면 즉시 종료
            if (best == 0) break;
            map = bestBoard;

            // 2. 유물 획득 + 연쇄 획득
            int sum = 0;
            while (true) {
                int got = search(map, true);
                if (got == 0) break;
                sum += got;
                fill(map);
            }
            sb.append(sum).append(' ');
        }
        System.out.println(sb.toString().trim());
    }

    // 크기 3 이상 덩어리들의 칸 수 합을 반환. remove가 true면 그 칸들을 0으로 지운다
    static int search(int[][] input, boolean remove) {
        boolean[][] visited = new boolean[5][5];
        int total = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (visited[i][j]) continue;
                int number = input[i][j];
                List<int[]> group = new ArrayList<>();
                Deque<int[]> queue = new ArrayDeque<>();
                queue.offer(new int[] {i, j});
                visited[i][j] = true;

                while (!queue.isEmpty()) {
                    int[] current = queue.poll();
                    group.add(current);
                    for (int d = 0; d < 4; d++) {
                        int nX = current[0] + dx[d];
                        int nY = current[1] + dy[d];
                        if (nX < 0 || nX >= 5 || nY < 0 || nY >= 5) continue;
                        if (visited[nX][nY] || input[nX][nY] != number) continue;
                        visited[nX][nY] = true;
                        queue.offer(new int[] {nX, nY});
                    }
                }

                if (group.size() >= 3) {
                    total += group.size();
                    if (remove) {
                        for (int[] c : group) input[c[0]][c[1]] = 0;
                    }
                }
            }
        }
        return total;
    }

    // 빈칸(0)을 벽면 숫자로 채움: 열 작은 순, 같은 열이면 행 큰 순(아래부터)
    static void fill(int[][] board) {
        for (int c = 0; c < 5; c++) {
            for (int r = 4; r >= 0; r--) {
                if (board[r][c] == 0) {
                    board[r][c] = wall[wallIdx++];
                }
            }
        }
    }

    // (x, y) 중심 3*3을 시계 방향 90도로 n번 회전한 새 배열
    static int[][] rotate(int x, int y, int n) {
        int[][] copiedArr = new int[5][];
        for (int i = 0; i < 5; i++) copiedArr[i] = map[i].clone();

        for (int i = 0; i < n; i++) {
            int temp1 = copiedArr[x][y - 1];
            int temp2 = copiedArr[x - 1][y - 1];

            copiedArr[x - 1][y - 1] = copiedArr[x + 1][y - 1];
            copiedArr[x][y - 1] = copiedArr[x + 1][y];
            copiedArr[x + 1][y - 1] = copiedArr[x + 1][y + 1];

            copiedArr[x + 1][y] = copiedArr[x][y + 1];
            copiedArr[x + 1][y + 1] = copiedArr[x - 1][y + 1];

            copiedArr[x][y + 1] = copiedArr[x - 1][y];
            copiedArr[x - 1][y + 1] = temp2;

            copiedArr[x - 1][y] = temp1;
        }
        return copiedArr;
    }
}
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

    static BufferedReader br;
    static StringTokenizer st;

    static int N;
    static int r;  // 고래의 현재 행
    static int c;  // 고래의 현재 열
    static int d;  // 고래가 바라보는 방향 (0 상, 1 우, 2 하, 3 좌 : 시계 순서)

    static int[][] ocean;
    static List<int[]> path;
    static boolean[][] visited;
    static int finish;

    // 이동용 방향: 시계 순서 (상 우 하 좌) → 오른쪽 (d+1)%4, 왼쪽 (d+3)%4
    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};

    // 입력 방향(1 상, 2 하, 3 좌, 4 우) → 시계 순서 인덱스
    static int[] toDir = {-1, 0, 2, 3, 1};

    // BFS용 방향: 문제의 최단 경로 우선순위 (좌 하 우 상)
    static int[][] bfsDir = {
            {0, -1}, {1, 0}, {0, 1}, {-1, 0}
    };

    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }

     public static void main(String[] args) throws Exception {
         // System.setIn(new FileInputStream("res/input.txt"));
         br = new BufferedReader(new InputStreamReader(System.in));
         N = nextInt();
         r = nextInt() - 1;  // path에는 +1하고 삽입
         c = nextInt() - 1;
         d = toDir[nextInt()];

         ocean = new int[N][N];
         path = new ArrayList<>();
         path.add(new int[] {r + 1, c + 1});  // 처음 시작 위치 정보 삽입 (시작 행, 시작 열)
         visited = new boolean[N][N];
         visited[r][c] = true;
         finish = 1;

         int sea = 0;

         // 바다 정보 초기화 (0은 빈칸, 1은 암초)
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 ocean[i][j] = nextInt();
                 if (ocean[i][j] == 0) sea++;
             }
         }

         // 모든 바다를 방문할 때까지 (갈 곳이 없으면 move 안에서 finish를 최대로)
         while (finish < sea) {
             move(r, c, d);
         }

         StringBuilder sb = new StringBuilder();
         for (int[] p : path) {
             sb.append(p[0]).append(' ').append(p[1]).append('\n');
         }
         System.out.print(sb);
     }

     // 메서드1: 직진 → 왼쪽 → 오른쪽 순서로 한 칸 이동, 안 되면 가장 가까운 미방문 바다로
     static void move(int row, int col, int dir) {  // 현재 위치한 행, 열, 바라보는 방향
         int[] order = {dir, (dir + 3) % 4, (dir + 1) % 4};  // 직진, 왼쪽, 오른쪽

         for (int nd : order) {
             int nR = row + dr[nd];
             int nC = col + dc[nd];

             if (nR < 0 || nR >= N || nC < 0 || nC >= N) continue;
             if (ocean[nR][nC] == 1 || visited[nR][nC]) continue;

             r = nR;
             c = nC;
             d = nd;  // 움직인 방향이 곧 새로 바라보는 방향
             visited[r][c] = true;
             finish++;
             path.add(new int[] {r + 1, c + 1});
             return;  // 인접한 칸으로 이동했으면 끝
         }

        // 인접한 칸으로 못 갔으면: 가장 가까운 미방문 바다까지 최단 경로로 이동
        int[] near = findOcean(row, col);
        if (near == null) {  // 갈 수 있는 미방문 바다가 없음
            finish = Integer.MAX_VALUE;
            return;
        }

        List<int[]> route = shortestPath(row, col, near[0], near[1]);  // 출발 제외, 도착 포함
        path.add(new int[] {near[0] + 1, near[1] + 1});

        // 마지막 한 걸음의 방향으로 바라보는 방향 갱신
        int[] last = route.get(route.size() - 1);
        int[] before = route.size() >= 2 ? route.get(route.size() - 2) : new int[] {row, col};
        for (int k = 0; k < 4; k++) {
            if (before[0] + dr[k] == last[0] && before[1] + dc[k] == last[1]) {
                d = k;
                break;
            }
        }

        r = near[0];
        c = near[1];
        visited[r][c] = true;
        finish++;  // 새로 방문한 건 도착 칸 하나뿐
    }

    // 메서드3: (sr, sc)에서 (tr, tc)까지 최단 경로 (bfsDir 순서가 우선순위)
    static List<int[]> shortestPath(int sr, int sc, int tr, int tc) {
        int[][] prevX = new int[N][N];
        int[][] prevY = new int[N][N];
        boolean[][] visit = new boolean[N][N];
        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {sr, sc});
        visit[sr][sc] = true;

        while (!queue.isEmpty()) {
            int[] poll = queue.poll();
            if (poll[0] == tr && poll[1] == tc) break;
            for (int l = 0; l < 4; l++) {  // 좌하우상
                int nR = poll[0] + bfsDir[l][0];
                int nC = poll[1] + bfsDir[l][1];
                if (nR < 0 || nR >= N || nC < 0 || nC >= N) continue;
                if (ocean[nR][nC] == 1 || visit[nR][nC]) continue;  // 암초만 피함
                visit[nR][nC] = true;
                prevX[nR][nC] = poll[0];
                prevY[nR][nC] = poll[1];
                queue.offer(new int[] {nR, nC});
            }
        }

        List<int[]> route = new ArrayList<>();
        int x = tr, y = tc;
        while (!(x == sr && y == sc)) {
            route.add(new int[] {x, y});
            int pX = prevX[x][y];
            int pY = prevY[x][y];
            x = pX;
            y = pY;
        }

        Collections.reverse(route);
        return route;
    }

     // 메서드2: 현재 위치 기준으로 가장 가까운 미방문 바다 찾기 (거리 → 행 → 열)
     static int[] findOcean(int row, int col) {  // 현재 위치의 행, 열
         int[][] dist = new int[N][N];
         for (int[] line : dist) Arrays.fill(line, -1);
         Deque<int[]> queue = new ArrayDeque<>();
         queue.offer(new int[] {row, col});
         dist[row][col] = 0;

         while (!queue.isEmpty()) {
             int[] current = queue.poll();
             for (int k = 0; k < 4; k++) {
                 int nR = current[0] + bfsDir[k][0];
                 int nC = current[1] + bfsDir[k][1];
                 if (nR < 0 || nR >= N || nC < 0 || nC >= N) continue;
                 if (ocean[nR][nC] == 1 || dist[nR][nC] != -1) continue;
                 dist[nR][nC] = dist[current[0]][current[1]] + 1;
                 queue.offer(new int[] {nR, nC});
             }
         }

         int[] best = null;
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 if (dist[i][j] <= 0 || visited[i][j]) continue;  // 못 감(-1), 현재 칸(0), 이미 방문 제외
                 if (best == null || dist[i][j] < best[2]) {     // 행 → 열 순서로 도니까 < 이면 동점은 앞 칸 유지
                     best = new int[] {i, j, dist[i][j]};
                 }
             }
         }
         return best;
     }
}
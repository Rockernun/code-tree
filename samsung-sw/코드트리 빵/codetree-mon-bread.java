import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    
    static BufferedReader br;
    static StringTokenizer st;
    static int n;
    static int m;
    static int[][] grid;
    static int[][] market;  // 각 번호에 대한 편의점의 위치 정보
    static int[][] man;
    static boolean[][] used;  // 지나갈 수 없는 칸 (사용한 베이스캠프, 도착한 편의점)
    static boolean[] finish;  // 각 사람이 편의점에 도착했는지에 대한 정보
    static int[] dx = {-1, 0, 0, 1};  // 상 좌 우 하 (문제의 우선순위)
    static int[] dy = {0, -1, 1, 0};
    static int count;  // 도착한 사람의 수
    
    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }
    
     public static void main(String[] args) throws Exception {
         // System.setIn(new FileInputStream("res/input.txt"));
         br = new BufferedReader(new InputStreamReader(System.in));
         
         n = nextInt();
         m = nextInt();
         
         grid = new int[n][n];
         man = new int[m + 1][2];
         market = new int[m + 1][2];
         used = new boolean[n][n];
         finish = new boolean[m + 1];  
         count = 0;
         
         for (int i = 1; i <= m; i++) {
             man[i][0] = -1;
             man[i][1] = -1;
         }
         
         // 빈 칸과 베이스캠프 위치 정보 초기화
         for (int i = 0; i < n; i++) {
             for (int j = 0; j < n; j++) {
                 grid[i][j] = nextInt();  // 0은 빈 칸, 1은 베이스캠프
             }
         }
         
         // 편의점 위치 초기화
         for (int i = 1; i <= m; i++) {
             int r = nextInt();
             int c = nextInt();
             market[i][0] = r - 1;
             market[i][1] = c - 1;
         }
         
         int time = 0;
         
         while (true) {
             time++;
             
             move(); 
             
             if (count == m) {
                 break;
             }
             
             if (time <= m) {
                 findBaseCamp(time); 
             }
         }
         
         System.out.println(time);
     }
     
     // 메서드1: number번 사람을 베이스캠프로 이동
     static void findBaseCamp(int number) {
         // 편의점에서 BFS: 각 칸까지 실제 최단 거리
         int[][] dist = bfs(market[number][0], market[number][1]);
         List<int[]> result = new ArrayList<>();  // 베이스캠프 후보 [행, 열, 거리]
         
         for (int i = 0; i < n; i++) {
             for (int j = 0; j < n; j++) {
                 // 베이스캠프이고, 아직 안 쓰였고, 편의점까지 갈 수 있는 칸
                 if (grid[i][j] == 1 && !used[i][j] && dist[i][j] != -1) {
                     result.add(new int[] {i, j, dist[i][j]});
                 }
             }
         }
         
         result.sort((a, b) -> {
             if (a[2] != b[2]) return Integer.compare(a[2], b[2]);
             if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
             return Integer.compare(a[1], b[1]);
         });
         
         int[] candidate = result.get(0);
         man[number][0] = candidate[0];
         man[number][1] = candidate[1];
         used[candidate[0]][candidate[1]] = true;  // 이 베이스캠프는 이제 못 지나감
     }
     
     // 메서드2: 이미 격자에 있는 사람들을 본인이 원하는 편의점으로 한 칸 이동
     static void move() {
         List<Integer> arrived = new ArrayList<>();  // 이번 시간에 도착한 사람들
         
         for (int i = 1; i <= m; i++) {
             if (man[i][0] == -1 && man[i][1] == -1) continue;  // 격자 바깥에 있는 사람은 패스
             if (finish[i]) continue;  // 이미 편의점에 도착한 사람은 패스
             int x = man[i][0];  // 이동할 사람의 현재 위치 행
             int y = man[i][1];  // 이동할 사람의 현재 위치 열
             
             // 편의점에서 BFS: 각 칸에서 편의점까지 실제 최단 거리
             int[][] dist = bfs(market[i][0], market[i][1]);
             
             // 갈 수 있는 이웃 중 편의점까지 거리가 가장 짧은 칸
             int bestX = -1, bestY = -1;
             int best = Integer.MAX_VALUE;
             for (int d = 0; d < 4; d++) {
                 int nX = x + dx[d];
                 int nY = y + dy[d];
                 
                 if (nX < 0 || nX >= n || nY < 0 || nY >= n) continue;  // 격자 밖
                 if (used[nX][nY] || dist[nX][nY] == -1) continue;  // 못 지나가는 칸
                 
                 if (dist[nX][nY] < best) {  // 더 작을 때만 갱신
                     best = dist[nX][nY];
                     bestX = nX;
                     bestY = nY;
                 }
             }
             
             man[i][0] = bestX;
             man[i][1] = bestY;
             
             if (man[i][0] == market[i][0] && man[i][1] == market[i][1]) {
                 arrived.add(i);
             }
         }
         
         // 모두 이동한 뒤에 도착한 편의점 막기
         for (int i : arrived) {
             used[market[i][0]][market[i][1]] = true;
             finish[i] = true;
             count++;
         }
     }
     
     // 메서드3: (sr, sc)에서 각 칸까지의 최단 거리 (못 가는 칸은 -1)
     static int[][] bfs(int sr, int sc) {
         int[][] dist = new int[n][n];
         for (int[] row : dist) Arrays.fill(row, -1);
         Deque<int[]> queue = new ArrayDeque<>();
         queue.offer(new int[] {sr, sc});
         dist[sr][sc] = 0;
         
         while (!queue.isEmpty()) {
             int[] current = queue.poll();
             for (int d = 0; d < 4; d++) {
                 int nX = current[0] + dx[d];
                 int nY = current[1] + dy[d];
                 if (nX < 0 || nX >= n || nY < 0 || nY >= n) continue;
                 if (used[nX][nY] || dist[nX][nY] != -1) continue;
                 dist[nX][nY] = dist[current[0]][current[1]] + 1;
                 queue.offer(new int[] {nX, nY});
             }
         }
         return dist;
     }
}
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
    static int r;  // 고래의 시작 행
    static int c;  // 고래의 시작 열
    static int d;  // 고래의 시작 방향
    
    static int[][] ocean;
    static List<int[]> path;
    static boolean[][] visited;
    static int finish;
    
    static int[][] d1 = {  // 상좌우
            {-1, 0}, {0, -1}, {0, 1}
    };
    
    static int[][] d2 = {  // 하우좌
            {1, 0}, {0, 1}, {0, -1}
    };
    
    static int[][] d3 = {  // 좌하상
            {0, -1}, {1, 0}, {-1, 0}
    };
    
    static int[][] d4 = {  // 우상하
            {0, 1}, {-1, 0}, {1, 0}
    };
    
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
         d = nextInt();
         
         ocean = new int[N][N];
         path = new ArrayList<>();
         path.add(new int[] {r + 1, c + 1});  // 처음 시작 위치 정보 삽입 (시작 행, 시작 열)
         visited = new boolean[N][N];
         visited[r][c] = true;
         finish = 1;
         
         int sea = 0;
         
         // 바다 정보 초기화
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 ocean[i][j] = nextInt();  // 0은 빈칸, 1은 암초
             }
         }
         
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 if (ocean[i][j] == 0) sea++;
             }
         }
         
         // 여기서 프로세스 진행 (메서드 조합)
         while (finish < sea) {  // 모든 바다를 방문하지 않았다면
             move(r, c, d);
         }
         
         for (int i = 0; i < path.size(); i++) {
             System.out.println(path.get(i)[0] + " " + path.get(i)[1]);
         }
     }
     
     // 메서드1: 상하좌우로 인접한 칸 중 우선순위대로 한 칸 이동
     static void move(int row, int col, int dir) {  // 현재 위치한 행, 열, 바라보는 방향
         int count = path.size();
         if (dir == 1) {  // 바라보는 방향이 위라면 (d1 적용) 
             for (int i = 0; i < 3; i++) {
                 int nR = row + d1[i][0];
                 int nC = col + d1[i][1];
                 
                 if (nR >= 0 && nR < N && nC >= 0 && nC < N) {
                     if (ocean[nR][nC] == 0 && !visited[nR][nC]) {
                         r = nR;
                         c = nC;
                         visited[r][c] = true;
                         finish++;
                         if (i == 1) {
                             d = 3;
                         } else if (i == 2) {
                             d = 4;
                         }
                         path.add(new int[] {r + 1, c + 1});
                         break;  // 이동 가능한 바다가 있으면 우선순위로 그곳에 이동
                     }
                 }
             }
         } else if (dir == 2) {  // 아래 (d2 적용)
             for (int i = 0; i < 3; i++) {
                 int nR = row + d2[i][0];
                 int nC = col + d2[i][1];
                 
                 if (nR >= 0 && nR < N && nC >= 0 && nC < N) {
                     if (ocean[nR][nC] == 0 && !visited[nR][nC]) {
                         r = nR;
                         c = nC;
                         visited[r][c] = true;
                         finish++;
                         if (i == 1) {
                             d = 4;
                         } else if (i == 2) {
                             d = 3;
                         }
                         path.add(new int[] {r + 1, c + 1});
                         break;  // 이동 가능한 바다가 있으면 우선순위로 그곳에 이동
                     }
                 }
             }
         } else if (dir == 3) {  // 왼쪽 (d3 적용)
             for (int i = 0; i < 3; i++) {
                 int nR = row + d3[i][0];
                 int nC = col + d3[i][1];
                 
                 if (nR >= 0 && nR < N && nC >= 0 && nC < N) {
                     if (ocean[nR][nC] == 0 && !visited[nR][nC]) {
                         r = nR;
                         c = nC;
                         visited[r][c] = true;
                         finish++;
                         if (i == 1) {
                             d = 2;
                         } else if (i == 2) {
                             d = 1;
                         }
                         path.add(new int[] {r + 1, c + 1});
                         break;  // 이동 가능한 바다가 있으면 우선순위로 그곳에 이동
                     }
                 }
             }
         } else if (dir == 4) {  // 오른쪽 (d4 적용)
             for (int i = 0; i < 3; i++) {
                 int nR = row + d4[i][0];
                 int nC = col + d4[i][1];
                 
                 if (nR >= 0 && nR < N && nC >= 0 && nC < N) {
                     if (ocean[nR][nC] == 0 && !visited[nR][nC]) {
                         r = nR;
                         c = nC;
                         visited[r][c] = true;
                         finish++;
                         if (i == 1) {
                             d = 1;
                         } else if (i == 2) {
                             d = 2;
                         }
                         path.add(new int[] {r + 1, c + 1});
                         break;  // 이동 가능한 바다가 있으면 우선순위로 그곳에 이동
                     }
                 }
             }
         }
         
         if (count != path.size()) return;  // 인접한 칸으로 이동했으면 끝
        
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
        if (last[0] < before[0]) d = 1;
        else if (last[0] > before[0]) d = 2;
        else if (last[1] < before[1]) d = 3;
        else d = 4;
        
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
     
     // 메서드2: 현재 위치 기준으로 가장 가까운 바다 찾기
     static int[] findOcean(int row, int col) {  // 현재 위치의 행, 열
         int[][] dist = new int[N][N];
         for (int[] r : dist) Arrays.fill(r,  -1);
         Deque<int[]> queue = new ArrayDeque<>();
         queue.offer(new int[] {row, col});
         dist[row][col] = 0;
         
         while (!queue.isEmpty()) {
             int[] current = queue.poll();
             for (int d = 0; d < 4; d++) {
                 int nR = current[0] + bfsDir[d][0];
                 int nC = current[1] + bfsDir[d][1];
                 if (nR < 0 || nR >= N || nC < 0 || nC >= N) continue;
                 if (ocean[nR][nC] == 1 || dist[nR][nC] != -1) continue;
                 dist[nR][nC] = dist[current[0]][current[1]] + 1;
                 queue.offer(new int[] {nR, nC});
             }
         }
         
         List<int[]> candidates = new ArrayList<>();
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 if (dist[i][j] > 0 && !visited[i][j]) {
                     candidates.add(new int[] {i, j, dist[i][j]});
                 }
             }
         }
         
         candidates.sort((a, b) -> {
             if (a[2] != b[2]) return Integer.compare(a[2], b[2]);
             if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
             return Integer.compare(a[1], b[1]);
         });
         
         if (candidates.isEmpty()) return null;
         return candidates.get(0);
     }
}
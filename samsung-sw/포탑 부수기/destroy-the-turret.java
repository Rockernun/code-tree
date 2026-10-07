import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    
    static BufferedReader br;
    static StringTokenizer st;
    static int N;
    static int M;
    static int K;
    static int[][] grid;
    static int[][] turn; 
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
    static int[][] cannon = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1},
            {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };
    static boolean[][] involved;
    
    static int nextInt() throws IOException {
        if (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }
    
     public static void main(String[] args) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        
        N = nextInt();  // 격자의 행의 길이
        M = nextInt();  // 격자의 열의 길이
        K = nextInt();  // 반복할 과정
        
        grid = new int[N][M];  // 포탑 공격력 정보 2차원 배열
        turn = new int[N][M];  // x번째 공격한 포탑들에 대한 정보
        
        // 포탑 공격력 정보 2차원 배열 초기화
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                grid[i][j] = nextInt();
            }
        }
        
        for (int i = 0; i < K; i++) {
            // 부서지지 않은 포탑이 1개라면 그 포탑 출력 후 break;
            int[] count = count();
            if (count[2] <= 1) {
                break;
            }
            
            List<int[]> attackerAndTarget = findTarget(i);
            int[] attacker = attackerAndTarget.get(0);
            int[] target = attackerAndTarget.get(1);
            int power = grid[attacker[0]][attacker[1]];
            involved = new boolean[N][M];
            involved[attacker[0]][attacker[1]] = true;
            
            List<int[]> path = laser(attacker[0], attacker[1], target[0], target[1]);
            
            if (path == null) {
                // 포탄 공격
                grid[target[0]][target[1]] = (grid[target[0]][target[1]] - power < 0) ? 0 : grid[target[0]][target[1]] - power;
                involved[target[0]][target[1]] = true;
                
                for (int c = 0; c < 8; c++) {
                    int nX = (target[0] + cannon[c][0] + N) % N;
                     int nY = (target[1] + cannon[c][1] + M) % M;
                     
                     if (nX == attacker[0] && nY == attacker[1]) continue;
                     grid[nX][nY] =  (grid[nX][nY] - (power / 2) < 0) ? 0 : grid[nX][nY] - (power / 2);
                     involved[nX][nY] = true;
                }
            } else {
                // 해당 좌표에 있는 포탑들의 공격력 차감
                for (int p = 0; p < path.size() - 1; p++) {
                    int[] position = path.get(p);  // 해당 좌표
                    grid[position[0]][position[1]] = (grid[position[0]][position[1]] - (power / 2) < 0) ? 0 : grid[position[0]][position[1]] - (power / 2);
                    involved[position[0]][position[1]] = true;
                }
                
                grid[target[0]][target[1]] = (grid[target[0]][target[1]] - power < 0) ? 0 : grid[target[0]][target[1]] - power;
                involved[target[0]][target[1]] = true;
            }
            
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < M; c++) {
                    if (grid[r][c] != 0 && !involved[r][c]) {
                        grid[r][c]++;
                    }
                }
            }
        }
        
        // 최종 가장 높은 공격력 출력
        int answer = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (grid[i][j] > answer) {
                    answer = grid[i][j];
                }
            }
        }
        
        System.out.println(answer);
        return;
    }
     
     // 메서드4: 부서지지 않은 포탑 개수 세기
     static int[] count() {
         int c = 0;
         int x = -1;
         int y = -1;
         
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < M; j++) {
                 if (grid[i][j] == 0) continue;
                 c++;
                 x = i;
                 y = j;
             }
         }
         
         return new int[] {x, y, c};
     }
     
     // 메서드3: 레이저 공격
     static List<int[]> laser(int sr, int sc, int tr, int tc) {
         // 직전 칸의 행과 열
         int[][] prevX = new int[N][M];
         int[][] prevY = new int[N][M];
         boolean[][] visited = new boolean[N][M];
         
         Deque<int[]> queue = new ArrayDeque<>();
         queue.offer(new int[] {sr, sc});
         visited[sr][sc] = true;
         
         while (!queue.isEmpty()) {
             int[] current = queue.poll();
             
             if (current[0] == tr && current[1] == tc) break;
             
             for (int i = 0; i < 4; i++) {
                 int nX = (current[0] + dx[i] + N) % N;
                 int nY = (current[1] + dy[i] + M) % M;
                 
                 if (visited[nX][nY] || grid[nX][nY] == 0) continue;
                 queue.offer(new int[]{nX, nY});
                 visited[nX][nY] = true;
                 prevX[nX][nY] = current[0];
                 prevY[nX][nY] = current[1];
             }
         }
         
         if (!visited[tr][tc]) return null;
         
         List<int[]> path = new ArrayList<>();
         int x = tr, y = tc;
         while (!(x == sr && y == sc)) {
             path.add(new int[] {x, y});
             int pX = prevX[x][y];
             int pY = prevY[x][y];
             x = pX;
             y = pY;
         }
         
         Collections.reverse(path);
         return path;
     }
     
     
     // 메서드1: 공격자의 (행, 열) 정보를 반환하는 메서드, 메서드 사용 시 공격자의 공격력 증가
     static int[] findAttacker(int process) {
         List<int[]> result = new ArrayList<>();
         int attack = Integer.MAX_VALUE;
         
         // 1. 가장 낮은 공격력을 찾음
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < M; j++) {
                 if (grid[i][j] == 0) continue;
                 if (grid[i][j] < attack) {
                     attack = grid[i][j];
                 }
             }
         }
         
         // 2. 그 공격력을 지닌 포탑의 (행, 열, 공격했던 시점) 정보 담기
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < M; j++) {
                 if (grid[i][j] == attack) {
                     result.add(new int[] {i, j, turn[i][j]});
                 }
             }
         }
         
         // 3. 가장 낮은 공격력을 가진 포탑 선정하기 (가장 최근에 공격한 포탑 -> 행 + 열이 가장 큰 포탑 -> 열이 가장 큰 포탑
         result.sort((a, b) -> {
             if (a[2] != b[2]) return Integer.compare(b[2], a[2]);
             if (a[0] + a[1] != b[0] + b[1]) return Integer.compare(b[0] + b[1], a[0] + a[1]);
             return Integer.compare(b[1], a[1]);
         });
         
         int[] attacker = result.get(0);
         grid[attacker[0]][attacker[1]] += (N + M);
         turn[attacker[0]][attacker[1]] = process + 1;
         return attacker;
     }
     
     // 메서드2: 공격 대상 (행, 열) 정보를 반환하는 메서드
     static List<int[]> findTarget(int process) {
         List<int[]> answer = new ArrayList<>();
         List<int[]> result = new ArrayList<>();
         
         int[] attacker = findAttacker(process);
         answer.add(attacker);
         
         int attack = 0;
         
         // 1. 가장 높은 공격력을 찾음
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < M; j++) {
                 if (i == attacker[0] && j == attacker[1]) continue;
                 if (grid[i][j] > attack) {
                     attack = grid[i][j];
                 }
             }
         }
         
         // 2. 그 공격력을 지닌 포탑의 (행, 열, 공격했던 시점) 정보 담기
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < M; j++) {
                 if (i == attacker[0] && j == attacker[1]) continue;
                 if (grid[i][j] == attack) {
                     result.add(new int[] {i, j, turn[i][j]});
                 }
             }
         }
         
         // 3. 가장 높은 공격력을 가진 포탑 선정하기 (가장 과거에 공격한 포탑 -> 행 + 열이 가장 작은 포탑 -> 열이 가장 작은 포탑)
         result.sort((a, b) -> {
             if (a[2] != b[2]) return Integer.compare(a[2], b[2]);
             if (a[0] + a[1] != b[0] + b[1]) return Integer.compare(a[0] + a[1], b[0] + b[1]);
             return Integer.compare(a[1], b[1]);
         });
         
         answer.add(result.get(0));
         return answer;
     }
}

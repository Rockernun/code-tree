import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    
    static BufferedReader br;
    static StringTokenizer st;
    static int N;
    static int M;
    static int K;
    static int finish = 0;
    static int[][] maze;
    static int[][] users;
    static int[] exit;
    static long total = 0;
    
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    
    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }

    
    public static void main(String[] args) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        
        N = nextInt();
        M = nextInt();
        K = nextInt();
        
        maze = new int[N + 1][N + 1];
        users = new int[M][2];  // [[1, 3], [3, 1], [3, 5]]        
        exit = new int[2];
        
        // 주어진 입력으로 미로 2차원 배열 초기화
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                maze[i][j] = nextInt();
            }
        }
        
        // 주어진 입력으로 참가자 출발점 2차원 배열 초기
        for (int i = 0; i < M; i++) {
            users[i][0] = nextInt();
            users[i][1] = nextInt();
        }
        
        // 초기 탈출구 좌표 초기화
        exit[0] = nextInt();
        exit[1] = nextInt();
        
        // 전체 과정 K초만큼 반복
        for (int i = 1; i <= K; i++) {
            // process1: 모든 참가자 이동
            move();
            
            if (finish == M) {
                break;
            }
            
            // process2: 정사각형 회전
            rotate();
        }
        
        System.out.println(total);
        System.out.println(exit[0] + " " + exit[1]);
    }
    
    // 메서드1: 모든 참가자를 한 칸 이동시키기
    static void move() {
        int[][] dist = findDist();
        for (int i = 0; i < users.length; i++) {
            int r = users[i][0], c = users[i][1];  // 현재 참가자의 위치(행, 열)
            
            // 탈출한 참가자는 패스
            if (r == 0 && c == 0) continue;
            
            for (int d = 0; d < 4; d++) {
                int nX = r + dx[d];
                int nY = c + dy[d];
                
                if (nX >= 1 && nX <= N && nY >= 1 && nY <= N) {
                    // 이동한 칸이 빈 칸이고, 최단거리가 원래 머물러 있던 칸보다 작을 때
                    if (maze[nX][nY] == 0 && dist[nX][nY] < dist[r][c]) {
                        // 그 칸으로 이동
                        users[i][0] = nX;
                        users[i][1] = nY;
                        total++;
                        break;
                    }
                }
            }
            
            // 이동한 위치가 현재 미로 기준 탈출구일 때 참가자 위치를 (0, 0)으로 설정 후, finish을 1만큼 증가 
            if (users[i][0] == exit[0] && users[i][1] == exit[1]) {
                users[i][0] = 0;
                users[i][1] = 0;
                finish++;
            }
        }
    }
    
    // 메서드2: 현재 출구 기준으로부터 각 위치까지 떨어진 거리 저장하기
    static int[][] findDist() {
        // 출구 좌표만 필요
        int[][] dist = new int[N + 1][N + 1];
        
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                int d = Math.abs(i - exit[0]) + Math.abs(j - exit[1]);
                dist[i][j] = d;
            }
         }
        
        return dist;
    }
    
    // 메서드3: 한 명 이상의 참가자와 출구를 포함한 가장 작은 정사각형을 찾기
    static int[] findSmallRectangle() {
        for (int len = 2; len <= N; len++) {
            for (int r = 1; r + len - 1 <= N; r++) {
                for (int c = 1; c + len - 1 <= N; c++) {
                    // 출구가 이 정사각형 밖이면 다음 후보
                    if (!inside(exit[0], exit[1], r, c, len)) continue;

                    // 탈출하지 않은 참가자가 한 명이라도 안에 있으면 정답
                    for (int i = 0; i < users.length; i++) {
                        if (users[i][0] == 0 && users[i][1] == 0) continue;
                        if (inside(users[i][0], users[i][1], r, c, len)) {
                            // 정사각형의 좌상단 행, 열, 길이 반환
                            return new int[] {r, c, len};
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    // 메서드4: (x, y)가 좌상단 (r, c), 한 변 len인 정사각형 안에 있는지
    static boolean inside(int x, int y, int r, int c, int len) {
        return r <= x && x < r + len && c <= y && y < c + len;
    }

    // 메서드5: 선택된 정사각형을 시계 방향으로 90도 회전시키고 내구도 감소시키기
    static void rotate() {
        int[] rectangle = findSmallRectangle();
        int r = rectangle[0];
        int c = rectangle[1];
        int len = rectangle[2];
        
        // 덮어쓸 정사각형 2차원 배열
        int[][] temp = new int[len][len];
        
        for (int x = r; x < r + len; x++) {
            for (int y = c; y < c + len; y++) {
                int a = x - r;
                int b = y - c;
                
                int na = b;
                int nb = len - 1 - a;
                
                temp[na][nb] = maze[x][y] > 0 ? maze[x][y] - 1 : 0;
            }
        }
        
        for (int na = 0; na < len; na++) {
            for (int nb = 0; nb < len; nb++) {
                maze[r + na][c + nb] = temp[na][nb];
            }
        }
        
        for (int i = 0; i < users.length; i++) {
            int x = users[i][0], y = users[i][1];
            if (x == 0 && y == 0) continue;
            if (!inside(x, y, r, c, len)) continue;
            
            int a = x - r, b = y - c;
            int na = b, nb = len - 1 - a;
            users[i][0] = r + na;
            users[i][1] = c + nb;
        }
        
        int a = exit[0] - r, b = exit[1] - c;
        int na = b, nb = len - 1 - a;
        exit[0] = r + na;
        exit[1] = c + nb;
    }
    
}

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class Main {
    
    static BufferedReader br;
    static StringTokenizer st;
    static int L;
    static int N;
    static int Q;
    static int[][] grid;  // 체스판 (빈칸, 함정, 벽 위치 정보)
    static int[][] knights;  // 기사 정보 ([좌상단 행, 좌상단 열, 높이, 너비, 초기 체력])
    static int[][] commands;  // 왕의 명령 모음
    static int[] initHp;  // 처음 체력 (받은 피해 = 처음 체력 - 현재 체력)
    static boolean[] dead;
    
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    
    
    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }
    
     public static void main(String[] args) throws Exception { 
         // System.setIn(new FileInputStream("res/input.txt"));
         br = new BufferedReader(new InputStreamReader(System.in));
         
         L = nextInt();
         N = nextInt();
         Q = nextInt();
         
         grid = new int[L][L];
         knights = new int[N + 1][5];
         commands = new int[Q][2];
         initHp = new int[N + 1];
         dead = new boolean[N + 1];
         
         // 체스판 정보 초기화 (0: 빈칸, 1: 함정, 2: 벽)
         for (int i = 0; i < L; i++) {
             for (int j = 0; j < L; j++) {
                 grid[i][j] = nextInt();
             }
         }
         
         // 기사들 정보 초기화
         for (int i = 1; i <= N; i++) {
             knights[i][0] = nextInt() - 1 ;
             knights[i][1] = nextInt() - 1;
             knights[i][2] = nextInt();
             knights[i][3] = nextInt();
             knights[i][4] = nextInt();
             initHp[i] = knights[i][4];
         }
         
         // 왕의 명령어 초기화
         for (int i = 0; i < Q; i++) {
             commands[i][0] = nextInt();
             commands[i][1] = nextInt();
         }
         
         // 명령을 순차적으로 실행
         for (int i = 0; i < Q; i++) {
             int[] command = commands[i];
             int number = command[0];  // 명령받은 기사 번호
             int dir = command[1];  // 이동해야 할 방향 (상하좌우)
             move(number, dir);
         }
         
         int answer = 0;
         
         for (int i = 1; i <= N; i++) {
             if (!dead[i]) {
                 answer += initHp[i] - knights[i][4];
             }
         }
         
         System.out.println(answer);
     }
     
     // 메서드1: 체스판에 기사 분포 정보 초기화
     static int[][] makeOwner() {
         int[][] owner = new int[L][L];
         
         for (int i = 1; i <= N; i++) {
             if (dead[i]) continue;
             int[] knight = knights[i];
             
             for (int x = knight[0]; x < knight[0] + knight[2]; x++) {
                 for (int y = knight[1]; y < knight[1] + knight[3]; y++) {
                     owner[x][y] = i;
                 }
             }
         }
         
         return owner;
     }
     
     // 메서드2: 명령받은 기사가 이동해야 할 방향으로 밀리는 기사들을 찾고, 밀릴 수 있는지 여부 알아내기
     static boolean[] findKnights(int start, int dir) {
         int[][] owner = makeOwner();
         boolean[] pushed = new boolean[N + 1];
         Deque<Integer> queue = new ArrayDeque<>();
         queue.offer(start);
         pushed[start] = true;
         
         while (!queue.isEmpty()) {
             int current = queue.poll();
             int nR = knights[current][0] + dx[dir];
             int nC = knights[current][1] + dy[dir];
             
             for (int x = nR; x < nR + knights[current][2]; x++) {
                 for (int y = nC; y < nC + knights[current][3]; y++) {
                     if (x < 0 || x >= L || y < 0 || y >= L) return null;
                     if (grid[x][y] == 2) return null;
                      
                     int other = owner[x][y];
                     if (other != 0 && other != current && !pushed[other]) {
                         pushed[other] = true;
                         queue.offer(other);    
                     }
                 }
             }
         }
         
         return pushed;
     }
     
     // 메서드3: 기사들을 이동시키고 함정의 개수만큼 체력 감소
     static void move(int number, int dir) {
         if (dead[number]) return;  // 사라진 기사에게 명령하면 아무 일도 없음
         
         boolean[] pushed = findKnights(number, dir);
         if (pushed == null) return;  // 하나라도 벽에 막히면 아무도 안 움직임
         
         for (int i = 1; i <= N; i++) {
             if (!pushed[i]) continue;
             knights[i][0] += dx[dir];
             knights[i][1] += dy[dir];
             
             if (i == number) continue;  // 명령받은 기사는 피해 없음
             
             // 밀려난 기사: 새 위치의 함정 수만큼 피해
             int damage = 0;
             int[] k = knights[i];
             for (int x = k[0]; x < k[0] + k[2]; x++) {
                 for (int y = k[1]; y < k[1] + k[3]; y++) {
                     if (grid[x][y] == 1) damage++;
                 }
             }
             k[4] -= damage;
             if (k[4] <= 0) dead[i] = true;  // 체력이 다하면 사라짐
         }
     }

}
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Main {
    
    static BufferedReader br;
    static StringTokenizer st;
    static int N;
    static int T;
    static String[][] food;
    static int[][] trust;
    
    // 0: 상, 1: 하, 2: 좌, 3: 우
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    
    static final String[] ORDER = {"TCM", "TC", "TM", "CM", "M", "C", "T"};
    
    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }
    
    static String next() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return st.nextToken();
    }
    
    public static void main(String[] args) throws Exception {
        // System.setIn(new FileInputStream("res/input.txt"));
        br = new BufferedReader(new InputStreamReader(System.in));
        N = nextInt();
        T = nextInt();
        
        // ================================ 올바르게 초기화 확인
        food = new String[N][N];
        trust = new int[N][N];
        
        for (int i = 0; i < N; i++) {
            String line = next();                 
            for (int j = 0; j < N; j++) {
                food[i][j] = String.valueOf(line.charAt(j));
            }
        }
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                trust[i][j] = nextInt();
            }
        }
        
        // ================================
        
        for (int i = 1; i <= T; i++) {
            morning();  // 1. 모든 학생들의 신앙심을 1씩 증가시키고,
            List<int[]> leaders = findLeaders();  // 2. 각 그룹의 리더 선출
            attack(leaders);
            
            int answer1 = 0, answer2 = 0, answer3 = 0, answer4 = 0, answer5 = 0, answer6 = 0, answer7 = 0;
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    if (food[r][c].equals(ORDER[0])) answer1 += trust[r][c];
                    else if (food[r][c].equals(ORDER[1])) answer2 += trust[r][c];
                    else if (food[r][c].equals(ORDER[2])) answer3 += trust[r][c];
                    else if (food[r][c].equals(ORDER[3])) answer4 += trust[r][c];
                    else if (food[r][c].equals(ORDER[4])) answer5 += trust[r][c];
                    else if (food[r][c].equals(ORDER[5])) answer6 += trust[r][c];
                    else if (food[r][c].equals(ORDER[6])) answer7 += trust[r][c];        
                }
            }
            
            System.out.println(answer1 + " " + answer2 + " " + answer3 + " " + answer4 + " " + answer5 + " " + answer6 + " " + answer7);
        }
    }
    
    // 메서드1: 모든 학생들의 신앙심을 1만큼 증가
    static void morning() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                trust[i][j]++;
            }
        }
    }
    
    // 메서드2: 각 그룹에서 대표자를 선발
    static List<int[]> findLeaders() {
        List<int[]> leaders = new ArrayList<>();
        Deque<int[]> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[N][N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (visited[i][j]) continue;
                
                List<int[]> group = new ArrayList<>();
                queue.offer(new int[] {i, j, trust[i][j]});
                visited[i][j] = true;
                
                while (!queue.isEmpty()) {
                    int[] cur = queue.poll();
                    group.add(cur);
                    
                    for (int d = 0; d < 4; d++) {
                        int nX = cur[0] + dx[d];
                        int nY = cur[1] + dy[d];
                        
                        if (!inRange(nX, nY) || visited[nX][nY] || !food[nX][nY].equals(food[i][j])) continue;
                        visited[nX][nY] = true;
                        queue.offer(new int[] {nX, nY, trust[nX][nY]});
                    }
                }
                
                group.sort((a, b) -> {
                    if (a[2] != b[2]) return Integer.compare(b[2], a[2]);
                    if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
                    return Integer.compare(a[1], b[1]);
                });
                
                int[] leader = group.get(0);
                for (int m = 1; m < group.size(); m++) {
                    trust[leader[0]][leader[1]]++;
                    trust[group.get(m)[0]][group.get(m)[1]]--;
                }
                
                leaders.add(leader);
            }
        }
        
        return leaders;
    }
    
    // 메서드3: 선출된 리더들이 정해진 순서대로 전파
    static void attack(List<int[]> leaders) {
        // 리스트에 각 [리더의 신앙심, 리더의 행, 리더의 열, 리더가 선호하는 음식 길이]를 삽입
        List<int[]> copiedLeaders = new ArrayList<>(leaders);
        List<int[]> orders = new ArrayList<>();
        boolean[][] attacked = new boolean[N][N];
        
        for (int i = 0; i < copiedLeaders.size(); i++) {
            int[] leader = copiedLeaders.get(i);
            orders.add(new int[] {trust[leader[0]][leader[1]], leader[0], leader[1], food[leader[0]][leader[1]].length()});
        }
        
        orders.sort((a, b) -> {
            if (a[3] != b[3]) return Integer.compare(a[3], b[3]);
            if (a[0] != b[0]) return Integer.compare(b[0], a[0]);
            if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
            return Integer.compare(a[2], b[2]);
        });
        
        // 각 리더가 순서대로 전파 시작
        for (int i = 0; i < orders.size(); i++) {
            int[] currentLeader = orders.get(i); // 현재 리더
            int row = currentLeader[1];
            int col = currentLeader[2];
            int power = currentLeader[0] - 1;  // 현재 리더의 간절함
            int dir = currentLeader[0] % 4;  // 현재 리더가 진행해야 할 방향 (0: 상, 1: 하, 2: 좌, 3: 우)
            
            if (attacked[row][col]) continue;
            
            String leaderFood = food[row][col];
            trust[row][col] = 1;
            
            while (power > 0) {  // 현재 위치가 유효한 위치일 경우
                int nX = row + dx[dir];
                int nY = col + dy[dir];
                
                if (!inRange(nX, nY)) break;  // 범위 벗어나면 종료
                
                row = nX;
                col = nY;
                
                if (leaderFood.equals(food[nX][nY])) continue;  // 같은 그룹이면 패스
                
                int target = trust[nX][nY];
                
                if (power > target) {  // 강한 전파
                    food[nX][nY] = leaderFood;
                    power -= target + 1;
                    trust[nX][nY]++;
                } else {  // 약한 전파
                    Set<String> set = new HashSet<>();
                    String[] split1 = leaderFood.split("");  // 전파자가 선호하던 기본 음식
                    String[] split2 = food[nX][nY].split("");  // 전파대상이 선호하던 기본 음식
                    
                    // 집합에 다 집어 넣음
                    for (String s1 : split1) {
                        set.add(s1);
                    }
                    
                    for (String s2 : split2) {
                        set.add(s2);
                    }
                    
                    List<String> list = new ArrayList<>(set);
                    list.sort((a, b) -> Integer.compare("TCM".indexOf(a), "TCM".indexOf(b)));
                    food[nX][nY] = String.join("", list); 
                    
                    trust[nX][nY] += power;
                    power = 0;
                }
                
                attacked[nX][nY] = true;
            }
        }
    }
    
    static boolean inRange(int row, int col) {
        return (row >= 0 && row < N && col >= 0 && col < N);
    }
    
}
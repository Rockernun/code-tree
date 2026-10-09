import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    
    static BufferedReader br;
    static StringTokenizer st;
    static int N;
    static int Q;
    
    static int[][] grid;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    
    static int nextInt() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return Integer.parseInt(st.nextToken());
    }
    
    public static void main(String[] args) throws Exception {
        // System.setIn(new FileInputStream("res/input.txt"));
        br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        
        N = nextInt();
        Q = nextInt();
        
        grid = new int[N][N];
        
        for (int i = 1; i <= Q; i++) {
            int c1 = nextInt();  // 좌상단 열
            int r1 = nextInt();  // 좌상단 행
            int c2 = nextInt() - 1;  // 우하단 열
            int r2 = nextInt() - 1;  // 우하단 행
            
            // 메서드1 호출
            putIn(i, c1, r1, c2, r2);
            
            // 메서드2 호출
            removeDividedMicrobe();  
            
            // 메서드3 호출 (새로운 배양 용기로 옮기기)
            move();
            
            sb.append(score()).append('\n');
        }
        
        System.out.println(sb);
    }
    
    // 메서드1: 미생물을 투입하고 배양 용기 갱신
    static void putIn(int number, int row1, int col1, int row2, int col2) {  // 미생물 번호, 좌상단 좌표, 우하단 좌표로 바꿔서 생각
        for (int r = row1; r <= row2; r++) {
            for (int c = col1; c <= col2; c++) {
                grid[r][c] = number;
            }
        }
        
        
    }
    
    // 메서드2: 어떤 종류의 미생물 무리가 둘 이상으로 나뉘어졌는지 확인
    // 하나의 미생물 기준으로 BFS를 수행해서 개수를 센 다음, 총 미생물의 개수와 맞지 않으면 둘로 나뉜 것
    static void removeDividedMicrobe() {  // 미생물의 무리는 최대 50개까지 있을 수 있음
        int[][] countAndBfs = new int[Q + 1][2];  // 각 번호에 해당하는 미생물의 갯수, BFS 개수
        
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                int number = grid[r][c];
                if (number == 0) continue;
                countAndBfs[number][0]++;
                if (countAndBfs[number][1] == 0) {  // BFS를 수행하지 않은 경우에만 그 좌표를 기점으로 BFS 수행
                    countAndBfs[number][1] = bfs(number, r, c);
                }
            }
        }
        
        for (int g = 1; g <= Q; g++) {
            if (countAndBfs[g][0] == countAndBfs[g][1]) continue;  // 한 덩어리라면 다음 진행
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (grid[i][j] == g) {
                        grid[i][j] = 0;  // 두 덩어리 이상인 미생물들 전부 없애기
                    }
                }
            }
        }
    }
    
    static int bfs(int number, int row, int col) {
        boolean[][] visited = new boolean[N][N];
        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {row, col});
        visited[row][col] = true;
        int count = 1;
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            
            for (int i = 0; i < 4; i++) {
                int nX = current[0] + dx[i];
                int nY = current[1] + dy[i];
                
                if (nX < 0 || nX >= N || nY < 0 || nY >= N) continue;
                if (visited[nX][nY] || grid[nX][nY] != number) continue;
                
                queue.offer(new int[] {nX, nY});
                visited[nX][nY] = true;
                count++;
            }
        }
        
        return count;
    }
    
    // 메서드3: 새로운 배양 용기에 정해준 규칙대로 미생물 무리들을 이동
    static void move() {
        // 각 무리의 칸 목록을 모은다 (칸 수 = 무리 크기)
        List<List<int[]>> cells = new ArrayList<>();
        for (int i = 0; i <= Q; i++) cells.add(new ArrayList<>());
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] != 0) {
                    cells.get(grid[i][j]).add(new int[] {i, j});
                }
            }
        }
 
        List<int[]> list = new ArrayList<>();
        for (int i = 1; i <= Q; i++) {
            if (cells.get(i).isEmpty()) continue;  // 없는 무리는 제외
            list.add(new int[] {i, cells.get(i).size()});  // [미생물 번호, 무리 크기]
        }
 
        list.sort((a, b) -> {
            if (a[1] != b[1]) return Integer.compare(b[1], a[1]);  
            return Integer.compare(a[0], b[0]);               
        });
 
        int[][] newGrid = new int[N][N];  // 새 용기 (옛 grid는 읽기만)
 
        for (int i = 0; i < list.size(); i++) {
            int number = list.get(i)[0];
            List<int[]> shape = cells.get(number);
 
            // 기준점: 모양을 감싸는 직사각형의 좌측 하단 
            int minX = N, minY = N;
            for (int[] p : shape) {
                minX = Math.min(minX, p[0]);
                minY = Math.min(minY, p[1]);
            }
 
            boolean placed = false;
            for (int j = 0; j < N && !placed; j++) {      
                for (int k = 0; k < N && !placed; k++) { 
                    if (isPossible(newGrid, shape, minX, minY, j, k)) {
                        for (int[] p : shape) {
                            newGrid[p[0] - minX + j][p[1] - minY + k] = number;
                        }
                        placed = true;
                    }
                }
            }
        }
 
        grid = newGrid;
    }
 
    // 모양의 모든 칸을 (row, col) 기준으로 옮겼을 때 용기 안이고 비어 있는지
    static boolean isPossible(int[][] newGrid, List<int[]> shape, int minX, int minY, int row, int col) {
        for (int[] p : shape) {
            int nX = p[0] - minX + row; 
            int nY = p[1] - minY + col; 
            if (nX >= N || nY >= N) return false; 
            if (newGrid[nX][nY] != 0) return false;
        }
        return true;
    }
 
    // 메서드4: 맞닿은 무리 쌍마다 (A 넓이) * (B 넓이)를 더함
    static long score() {
        int[] area = new int[Q + 1];
        boolean[][] adjacent = new boolean[Q + 1][Q + 1];
 
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int a = grid[i][j];
                if (a == 0) continue;
                area[a]++;
                for (int d = 0; d < 4; d++) {
                    int nX = i + dx[d];
                    int nY = j + dy[d];
                    if (nX < 0 || nX >= N || nY < 0 || nY >= N) continue;
                    int b = grid[nX][nY];
                    if (b == 0 || b == a) continue;
                    adjacent[a][b] = true;
                }
            }
        }
 
        long sum = 0;
        for (int a = 1; a <= Q; a++) {
            for (int b = a + 1; b <= Q; b++) {
                if (adjacent[a][b]) sum += (long) area[a] * area[b];
            }
        }
        return sum;
    }
}
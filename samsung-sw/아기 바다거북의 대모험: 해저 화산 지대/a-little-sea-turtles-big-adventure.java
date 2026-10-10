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

    static int N;  // 격자 크기
    static int M;  // 아기 거북 수
    static int K;  // 해저 화산 수

    static int[][] grid;  // 0: 빈 칸, 1: 산호초, 2: 살아있는 거북, 3: 화석

    // 아기 거북 관련 변수
    static int[][] position;  // 각 거북이 위치한 [행, 열]
    static boolean[] finished;  // 각 거북이 안식처에 도착했는지 여부
    static boolean[] dead;  // 각 거북이 화석이 되었는지 여부
    static int[] turn;  // 몇 번째 턴에 안식처에 도착했는지 (화석이 되었거나, 도착할 수 없다면 -1)
    static int count;  // 더 이상 움직이지 않는 거북의 수 (도착 + 화석)

    // 화산 관련 변수
    static List<int[]> volcanoes;  // 해저 화산의 좌표 목록 {행, 열}
    static int[][] pressure;  // 해저 화산의 압력
    static int[][] limit;  // 각 화산의 임계치
    static int[][] heat;  // 열기 지도
    static boolean[][] fire;  // 이번 턴에 분출했는지 여부

    static int[] dr = {0, 1, 0, -1};  // 우, 하, 좌, 상 (최단 경로 우선순위)
    static int[] dc = {1, 0, -1, 0};


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
        M = nextInt();
        K = nextInt();

        grid = new int[N][N];
        position = new int[M + 1][2];
        finished = new boolean[M + 1];
        dead = new boolean[M + 1];
        turn = new int[M + 1];
        Arrays.fill(turn, -1);  // 기본값은 -1 (도착하면 덮어씀)
        count = 0;

        volcanoes = new ArrayList<>();
        pressure = new int[N][N];
        limit = new int[N][N];
        heat = new int[N][N];
        fire = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = nextInt();  // 0: 빈 칸, 1: 산호초
            }
        }

        // 각 거북의 위치 초기화
        for (int i = 1; i <= M; i++) {
            position[i][0] = nextInt();
            position[i][1] = nextInt();
            grid[position[i][0]][position[i][1]] = 2;
        }

        // 화산 위치 정보와 임계치 초기화
        for (int i = 0; i < K; i++) {
            int row = nextInt();
            int col = nextInt();
            int l = nextInt();

            volcanoes.add(new int[] {row, col});
            limit[row][col] = l;
        }

        for (int i = 1; i <= 100; i++) {
            if (count == M) break;  // 모든 거북이 도착했거나 화석이 되었으면 중단

            // 메서드1: 1번 거북부터 차례대로 이동하기
            move(i);

            // 메서드2: 모든 화산의 압력 10만큼 증가
            increasePressure();

            // 메서드3: 화산 분출 & 연쇄 반응 수행
            explode();

            // 메서드4: 열기 20 이상인 칸의 거북은 화석
            fossilize();

            // 메서드5: 열기 초기화, 분출한 화산 압력 0
            reset();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= M; i++) {
            sb.append(turn[i]).append('\n');
        }
        System.out.print(sb);
    }

    // 메서드1: 1번 거북부터 차례대로 이동
    static void move(int number) {
        for (int i = 1; i <= M; i++) {
            if (dead[i] || finished[i]) continue;  // 화석이 되었거나 이미 도착한 거북은 패스
            int row = position[i][0];  // 거북의 현재 위치
            int col = position[i][1];

            // 현재 위치 기준 안식처까지 BFS 복원한 경로 (출발 제외, 도착 포함). 없으면 null
            List<int[]> path = findShortestPath(row, col, N - 1, N - 1);
            if (path == null) continue;  // 최단 경로가 없으면 제자리

            int[] next = path.get(0);
            int nR = next[0];
            int nC = next[1];
            grid[row][col] = 0;  // 원래 자리 비우기

            if (nR == N - 1 && nC == N - 1) {  // 이번 턴에 안식처에 도착 → 지도에서 제외
                finished[i] = true;
                turn[i] = number;
                count++;
                continue;
            }

            position[i][0] = nR;  // 위치 갱신 (다음 거북의 탐색에 바로 반영)
            position[i][1] = nC;
            grid[nR][nC] = 2;
        }
    }

    // 최단거리 BFS 경로 복원 (우하좌상 순서로 탐색 → 첫 이동 방향의 우선순위가 지켜짐)
    static List<int[]> findShortestPath(int sr, int sc, int tr, int tc) {
        int[][] prevR = new int[N][N];
        int[][] prevC = new int[N][N];
        boolean[][] seen = new boolean[N][N];
        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {sr, sc});
        seen[sr][sc] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[0] == tr && current[1] == tc) break;  // 도착 지점일 경우 중단
            for (int d = 0; d < 4; d++) {
                int nR = current[0] + dr[d];
                int nC = current[1] + dc[d];
                if (!inRange(nR, nC) || seen[nR][nC] || !canPass(nR, nC)) continue;
                seen[nR][nC] = true;
                prevR[nR][nC] = current[0];
                prevC[nR][nC] = current[1];
                queue.offer(new int[] {nR, nC});
            }
        }

        if (!seen[tr][tc]) return null;  // 도달 불가

        List<int[]> path = new ArrayList<>();
        int r = tr;
        int c = tc;
        while (!(r == sr && c == sc)) {  // 출발점에 닿을 때까지 거슬러 올라감
            path.add(new int[] {r, c});
            int pr = prevR[r][c];
            int pc = prevC[r][c];
            r = pr;
            c = pc;
        }

        Collections.reverse(path);
        return path;
    }

    // 메서드2: 모든 화산의 압력 10만큼 증가
    static void increasePressure() {
        for (int[] v : volcanoes) {
            pressure[v[0]][v[1]] += 10;
        }
    }

    // 메서드3: 화산 분출 & 연쇄 반응 (새로 분출하는 화산이 없을 때까지 반복)
    static void explode() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int[] v : volcanoes) {
                int row = v[0];
                int col = v[1];

                if (fire[row][col]) continue;  // 이미 분출한 화산은 패스
                // 처음엔 heat가 0이라 "압력 ≥ 임계치", 이후엔 "압력 + 외부 열기 ≥ 임계치"
                if (pressure[row][col] + heat[row][col] < limit[row][col]) continue;

                fire[row][col] = true;
                spread(row, col, limit[row][col]);
                changed = true;  // 새로 터졌으니 한 바퀴 더 확인
            }
        }
    }

    // 화산 칸에 value, 상하좌우로 반씩 줄며 퍼뜨리기 (여러 화산의 열기는 합산)
    static void spread(int row, int col, int value) {
        heat[row][col] += value;

        for (int d = 0; d < 4; d++) {
            int heats = value;  // 방향마다 화산의 열기에서 다시 시작
            int nR = row;
            int nC = col;

            while (true) {
                nR += dr[d];
                nC += dc[d];
                heats /= 2;  // 한 칸 갈 때마다 절반 (내림)

                if (!inRange(nR, nC) || grid[nR][nC] == 1 || heats == 0) break;  // 판 밖, 산호초, 열기 0이면 중단
                heat[nR][nC] += heats;
            }
        }
    }

    // 메서드4: 모든 분출이 끝난 뒤, 열기 20 이상인 칸의 살아있는 거북은 화석
    static void fossilize() {
        for (int i = 1; i <= M; i++) {
            if (dead[i] || finished[i]) continue;
            int row = position[i][0];
            int col = position[i][1];
            if (heat[row][col] >= 20) {
                dead[i] = true;
                grid[row][col] = 3;  // 이후 턴부터 장애물
                count++;
            }
        }
    }

    // 메서드5: 열기 초기화, 분출한 화산 압력 0, 분출 표시 초기화
    static void reset() {
        for (int[] v : volcanoes) {
            if (fire[v[0]][v[1]]) pressure[v[0]][v[1]] = 0;
        }
        heat = new int[N][N];
        fire = new boolean[N][N];
    }

    static boolean inRange(int row, int col) {
        return (row >= 0 && row < N && col >= 0 && col < N);
    }

    // 지나갈 수 있는 칸: 빈 칸(0)만 (산호초 1, 거북 2, 화석 3은 장애물. 화산은 grid에 없어서 0)
    static boolean canPass(int row, int col) {
        return grid[row][col] == 0;
    }
}
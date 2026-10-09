import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

    static BufferedReader br;
    static StringTokenizer st;

    static int N;
    static int M;
    static int P;
    static int C;
    static int D;

    static int[][] grid;  // 산타 위치 지도: grid[r][c] = 산타 번호 (0은 빈칸)
    static int[] rudolph;  // {행, 열}
    static int[][] santas;  // santas[번호] = {행, 열, 점수}
    static int[] stun;  // 기절 남은 턴 (0이면 움직일 수 있음)
    static int[][] dir = {  // 0 ~ 7
            {-1, 0}, {0, 1}, {1, 0}, {0, -1},  // 상우하좌 (산타는 0~3만 사용)
            {-1, 1}, {1, 1}, {1, -1}, {-1, -1}  // 대각선 방향 포함
    };

    static boolean[] alive;
    static int dead;  // 탈락한 산타의 수

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
        P = nextInt();
        C = nextInt();
        D = nextInt();

        grid = new int[N + 1][N + 1];
        rudolph = new int[2];
        santas = new int[P + 1][3];
        stun = new int[P + 1];
        alive = new boolean[P + 1];
        dead = 0;

        rudolph[0] = nextInt();  // 초기 위치한 행
        rudolph[1] = nextInt();  // 초기 위치한 열

        for (int k = 1; k <= P; k++) {
            int id = nextInt();  // 산타 번호 (순서대로 주어지지 않을 수 있음)
            santas[id][0] = nextInt();  // 초기 위치한 행
            santas[id][1] = nextInt();  // 초기 위치한 열
            santas[id][2] = 0;  // 점수
            alive[id] = true;
            grid[santas[id][0]][santas[id][1]] = id;
        }

        for (int i = 1; i <= M; i++) {
            if (dead == P) break;

            // 메서드1 수행
            rudolphAttack();

            // 메서드2 수행
            santasAttack();

            // alive 순회하면서 true인 애들은 +1
            for (int a = 1; a <= P; a++) {
                if (alive[a]) santas[a][2]++;
            }

            // stun을 순회하면서 0이 아닌 산타들(기절 상태인 산타들)을 -1
            for (int s = 1; s <= P; s++) {
                if (stun[s] != 0) stun[s]--;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int s = 1; s <= P; s++) {
            sb.append(santas[s][2]);
            if (s < P) sb.append(' ');
        }
        System.out.println(sb);
    }

    // 메서드1: 루돌프가 가장 가까운 산타에게 돌진
    static void rudolphAttack() {
        int row = rudolph[0];  // 현재 루돌프가 위치한 행
        int col = rudolph[1];  // 현재 루돌프가 위치한 열

        List<int[]> santasPosition = new ArrayList<>();
        for (int s = 1; s <= P; s++) {
            if (!alive[s]) continue;  // 탈락한 산타는 고려 안 함 (기절한 산타는 포함)
            santasPosition.add(new int[] {santas[s][0], santas[s][1], dist(row, col, santas[s][0], santas[s][1]), s});  // {행, 열, 거리, 번호}
        }
        if (santasPosition.isEmpty()) return;

        // 거리가 가장 가까운 -> r좌표가 더 큰 -> c좌표가 더 큰 산타 순서대로 정렬
        santasPosition.sort((a, b) -> {
            if (a[2] != b[2]) return Integer.compare(a[2], b[2]);
            if (a[0] != b[0]) return Integer.compare(b[0], a[0]);
            return Integer.compare(b[1], a[1]);
        });

        int[] targetSanta = santasPosition.get(0);

        // 가장 가까워지는 한 칸 = 행, 열을 각각 목표 쪽으로 1씩 (-1, 0, 1)
        int dR = Integer.compare(targetSanta[0], row);
        int dC = Integer.compare(targetSanta[1], col);
        rudolph[0] = row + dR;
        rudolph[1] = col + dC;

        int hit = grid[rudolph[0]][rudolph[1]];  // 이동한 칸에 산타가 있으면 충돌
        if (hit != 0) {
            santas[hit][2] += C;  // 그 산타는 C점 획득
            stun[hit] = 2;  // 그 산타는 기절 상태 (다음 턴까지 못 움직임)
            push(hit, dR, dC, C);  // 루돌프가 돌진해온 방향으로 C칸 밀려남
        }
    }

    // 메서드2: 산타가 번호 순서대로 루돌프에게 접근
    static void santasAttack() {
        for (int s = 1; s <= P; s++) {
            if (!alive[s] || stun[s] != 0) continue;  // 탈락했거나 기절한 산타는 못 움직임

            int row = santas[s][0];
            int col = santas[s][1];
            int best = dist(row, col, rudolph[0], rudolph[1]);  // 지금보다 가까워져야만 이동
            int bestDir = -1;

            for (int d = 0; d < 4; d++) {  // 상우하좌 순서 = 우선순위
                int nR = row + dir[d][0];
                int nC = col + dir[d][1];
                if (!inRange(nR, nC) || grid[nR][nC] != 0) continue;  // 판 밖, 다른 산타가 있는 칸 제외

                int newDist = dist(nR, nC, rudolph[0], rudolph[1]);
                if (newDist < best) {  // < 이므로 동점이면 먼저 본 방향 유지
                    best = newDist;
                    bestDir = d;
                }
            }

            if (bestDir == -1) continue;  // 가까워지는 방향이 없으면 그대로

            // 이동
            grid[row][col] = 0;
            santas[s][0] = row + dir[bestDir][0];
            santas[s][1] = col + dir[bestDir][1];
            grid[santas[s][0]][santas[s][1]] = s;

            // 루돌프 칸으로 들어갔으면 충돌
            if (santas[s][0] == rudolph[0] && santas[s][1] == rudolph[1]) {
                santas[s][2] += D;  // D점 획득
                stun[s] = 2;
                int back = (bestDir + 2) % 4;  // 자신이 이동해온 반대 방향
                push(s, dir[back][0], dir[back][1], D);  // D칸 밀려남
            }
        }
    }

    // 메서드3: 산타 id를 (dR, dC) 방향으로 power칸 밀기 (착지한 칸에 산타가 있으면 1칸씩 연쇄)
    static void push(int id, int dR, int dC, int power) {
        grid[santas[id][0]][santas[id][1]] = 0;  // 원래 자리 비우기
        int moving = id;
        int r = santas[id][0] + dR * power;
        int c = santas[id][1] + dC * power;

        while (true) {
            if (!inRange(r, c)) {  // 판 밖으로 밀려나면 탈락
                alive[moving] = false;
                dead++;
                break;
            }
            int other = grid[r][c];  // 덮어쓰기 전에 원래 있던 산타 기억
            grid[r][c] = moving;
            santas[moving][0] = r;
            santas[moving][1] = c;
            if (other == 0) break;  // 빈칸이면 연쇄 끝

            moving = other;  // 원래 있던 산타가 같은 방향으로 1칸 밀림
            r += dR;
            c += dC;
        }
    }

    // 공통 메서드: 두 칸 사이의 거리 (제곱합)
    static int dist(int r1, int c1, int r2, int c2) {
        return (r1 - r2) * (r1 - r2) + (c1 - c2) * (c1 - c2);
    }

    // 격자 범위 내에 있는지
    static boolean inRange(int row, int col) {
        return (row >= 1 && row <= N && col >= 1 && col <= N);
    }
}
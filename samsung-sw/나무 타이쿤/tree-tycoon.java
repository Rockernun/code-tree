import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class Main {

    static int[][] dir = {
        {0, 1}, {-1, 1}, {-1, 0}, {-1, -1},
        {0, -1}, {1, -1}, {1, 0}, {1, 1}
    };

    static int n;
    static int m;
    static int[][] trees;
    static int[][] drug;
    static int[][] rules;
    static int answer = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st0 = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st0.nextToken());
        m = Integer.parseInt(st0.nextToken());

        drug = new int[n + 2][n + 2];  // 영양제는 행렬이 1칸씩 더 큰 격자로 세팅
        drug[n - 1][1] = 1;
        drug[n - 1][2] = 1;
        drug[n][1] = 1;
        drug[n][2] = 1;

        // 리브로수 초기화
        trees = new int[n + 2][n + 2];
        for (int i = 1; i < n + 1; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int j = 1; j < n + 1; j++) {
                trees[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 이동 방향(d), 이동 칸 수(p)
        rules = new int[m][2];
        for (int i = 0; i < m; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 2; j++) {
                rules[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 0; i < rules.length; i++) {
            int[] d = dir[rules[i][0] - 1];  // 이동 방향
            int p = rules[i][1];  // 이동 칸 수

            // 1. 특수 영양제 이동 (이동 결과를 새 격자에 담아 겹쳐쓰기 방지)
            int[][] newDrug = new int[n + 2][n + 2];
            for (int j = 1; j < n + 1; j++) {
                for (int k = 1; k < n + 1; k++) {
                    if (drug[j][k] == 1) {  // 특수 영양제가 있는 위치라면
                        int nX = j;
                        int nY = k;
                        for (int l = 0; l < p; l++) {
                            nX += d[0];  // 새로운 행
                            nY += d[1];  // 새로운 열

                            // 격자 바깥으로 빠져나갔을 경우 반대편으로
                            if (nX == 0) {
                                nX = n;
                            } else if (nX == n + 1) {
                                nX = 1;
                            }
                            if (nY == 0) {
                                nY = n;
                            } else if (nY == n + 1) {
                                nY = 1;
                            }
                        }
                        newDrug[nX][nY] = 1;  // 이동한 위치에 특수 영양제 놓기
                    }
                }
            }
            drug = newDrug;

            // 2. 특수 영양제 투입 (+1) 을 먼저 전부 진행
            int[][] fed = new int[n + 2][n + 2];  // 이번에 영양제를 맞은 칸 표시
            for (int j = 1; j < n + 1; j++) {
                for (int k = 1; k < n + 1; k++) {
                    if (drug[j][k] == 1) {
                        trees[j][k]++;
                        fed[j][k] = 1;
                        drug[j][k] = 0;
                    }
                }
            }

            // 3. 영양제 맞은 칸: 대각선으로 인접한 높이 1 이상인 리브로수 수만큼 추가 성장
            //    (2단계가 끝난 격자를 기준으로 계산)
            int[][] add = new int[n + 2][n + 2];
            for (int j = 1; j < n + 1; j++) {
                for (int k = 1; k < n + 1; k++) {
                    if (fed[j][k] == 1) {
                        int count = 0;
                        int[][] diagonal = new int[][]{dir[1], dir[3], dir[5], dir[7]};
                        for (int l = 0; l < 4; l++) {
                            int nX = j + diagonal[l][0];
                            int nY = k + diagonal[l][1];

                            if (nX < 1 || nX > n || nY < 1 || nY > n) {
                                continue;
                            }
                            if (trees[nX][nY] >= 1) {
                                count++;
                            }
                        }
                        add[j][k] = count;
                    }
                }
            }
            for (int j = 1; j < n + 1; j++) {
                for (int k = 1; k < n + 1; k++) {
                    trees[j][k] += add[j][k];
                }
            }

            // 4. 영양제 맞은 칸을 제외하고 높이 2 이상인 곳을 -2 하고 특수 영양제를 올려둠
            for (int j = 1; j < n + 1; j++) {
                for (int k = 1; k < n + 1; k++) {
                    if (fed[j][k] == 1) continue;
                    if (trees[j][k] >= 2) {
                        trees[j][k] -= 2;
                        drug[j][k] = 1;
                    }
                }
            }
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                answer += trees[i][j];
            }
        }

        System.out.println(answer);
    }
}
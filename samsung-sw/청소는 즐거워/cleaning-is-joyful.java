import java.util.Scanner;

public class Main {
    public static final int MAX_N = 499;
    public static final int DIR_NUM = 4;
    
    // 전역 변수 선언:
    public static int n;
    
    public static int currX, currY;
    public static int moveDir, moveNum;
    
    public static int ans;
    
    public static int[][] grid = new int[MAX_N][MAX_N];
    
    public static int[][][] dustRatio = new int[][][]{
        {
            {0,  0, 2, 0, 0},
            {0, 10, 7, 1, 0},
            {5,  0, 0, 0, 0},
            {0, 10, 7, 1, 0},
            {0,  0, 2, 0, 0},
        },
        {
            {0,  0, 0,  0, 0},
            {0,  1, 0,  1, 0},
            {2,  7, 0,  7, 2},
            {0, 10, 0, 10, 0},
            {0,  0, 5,  0, 0},
        },
        {
            {0, 0, 2,  0, 0},
            {0, 1, 7, 10, 0},
            {0, 0, 0,  0, 5},
            {0, 1, 7, 10, 0},
            {0, 0, 2,  0, 0},
        },
        {
            {0,  0, 5,  0, 0},
            {0, 10, 0, 10, 0},
            {2,  7, 0,  7, 2},
            {0,  1, 0,  1, 0},
            {0,  0, 0,  0, 0},
        }
    };
    
    public static boolean inRange(int x, int y) {
        return 0 <= x && x < n && 0 <= y && y < n;
    }
    
    // (x, y) 위치에 dust 만큼의 먼지를 추가합니다.
    public static void addDust(int x, int y, int dust) {
        // 격자 범위를 벗어난다면 답에 더해줍니다.
        if(!inRange(x, y))
            ans += dust;
        // 격자 범위 안이라면, 해당 칸에 더해줍니다.
        else
            grid[x][y] += dust;
    }
    
    // 한 칸 움직이며 청소를 진행합니다.
    public static void move() {
        // 문제에서 원하는 진행 순서대로 
        // 왼쪽 아래 오른쪽 위 방향이 되도록 정의합니다.
        int[] dx = new int[]{0, 1, 0, -1};
        int[] dy = new int[]{-1, 0, 1, 0};
        
        // curr 위치를 계산합니다.
        currX += dx[moveDir]; currY += dy[moveDir];
        
        // 현재 위치를 기준으로 각 위치에 먼지를 더해줍니다.
        int addedDust = 0;
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 5; j++) {
                int dust = grid[currX][currY] * dustRatio[moveDir][i][j] / 100;
                addDust(currX + i - 2, currY + j - 2, dust);
                
                addedDust += dust;
            }
        
        // a% 자리에 먼지를 추가합니다.
        addDust(currX + dx[moveDir], currY + dy[moveDir], 
                grid[currX][currY] - addedDust);
    }
    
    public static boolean end() {
        return currX == 0 && currY == 0;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 입력:
        n = sc.nextInt();
        
        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                grid[i][j] = sc.nextInt();
        
        // 시작 위치와 방향, 
        // 해당 방향으로 이동할 횟수를 설정합니다. 
        currX = n / 2; currY = n / 2;
        moveDir = 0; moveNum = 1;

        while(!end()) {
            // moveNum 만큼 이동합니다.
            for(int i = 0; i < moveNum; i++) {
                move();
                
                // 이동하는 도중 (0, 0)으로 오게 되면,
                // 움직이는 것을 종료합니다.
                if(end())
                    break;
            }
            
            // 방향을 바꿉니다.
            moveDir = (moveDir + 1) % 4;
            // 만약 현재 방향이 왼쪽 혹은 오른쪽이 된 경우에는
            // 특정 방향으로 움직여야 할 횟수를 1 증가시킵니다.
            if(moveDir == 0 || moveDir == 2)
                moveNum++;
        }
        
        // 출력:
        System.out.print(ans);

    }
}

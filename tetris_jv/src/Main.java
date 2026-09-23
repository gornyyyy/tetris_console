class TetrisGame {
    int rows;
    int col;
    Random random = new Random();
    int[][] gameboard;
    // 3 - это низ, 2 - приземлившаяся фигурка, 1 - летящая фигурка

    public TetrisGame(int rows, int col) {
        this.rows = Math.max(rows, 10);
        this.col = Math.max(col, 10);
        this.gameboard = new int[rows][col];
    }

    private void FillBoard() {
        for (int i = 0; i < col; i++) {
            gameboard[rows - 2][i] = 3;
        }
        SpawnBlock();
    }

    private void DrawBoard() {

        System.out.print("\033[H\033[2J");
        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < col; j++) {
                if (gameboard[i][j] == 3) {
                    System.out.print("3" + " ");
                } else if (gameboard[i][j] == 2) {
                    System.out.print("*" + " ");
                } else {
                    System.out.print(gameboard[i][j] == 1 ? "#" + " " : " " + " ");
                }
            }
            System.out.println();
        }
    }

    private void SpawnBlock() {
        int seed = random.nextInt(1,6);

        switch (seed) {
            case 1:
                gameboard[0][col- 4] = 1;
                gameboard[1][col- 4] = 1;
                gameboard[1][col - 3] = 1;
                gameboard[0][col -3] = 1;
                break;
            case 2:
                gameboard[0][col - 4] = 1;
                gameboard[0][col - 3] = 1;
                gameboard[0][col - 5] = 1;
                gameboard[0][col - 6] = 1;
                break;
            case 3:
                gameboard[0][col - 4] = 1;
                gameboard[0][col - 5] = 1;
                gameboard[0][col - 6] = 1;
                gameboard[1][col - 6] = 1;
                break;
            case 4:
                gameboard[0][col - 4] = 1;
                gameboard[0][col - 5] = 1;
                gameboard[0][col - 6] = 1;
                gameboard[1][col - 4] = 1;
                break;
            case 5:
                gameboard[0][col - 4] = 1;
                gameboard[0][col - 5] = 1;
                gameboard[1][col - 5] = 1;
                gameboard[1][col - 6] = 1;
                break;
            case 6:
                gameboard[1][col - 4] = 1;
                gameboard[1][col - 5] = 1;
                gameboard[0][col - 5] = 1;
                gameboard[0][col - 6] = 1;
                break;
        }
    }

    private void MoveDown() {
        int[][] next_state = new int[rows][col];
        boolean should_stop = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (gameboard[i][j] != 1) {
                    next_state[i][j] = gameboard[i][j];
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (gameboard[i][j] != 1) continue;

                if (should_stop == true) {
                    next_state[i + 1][j] = 2;
                }
                else {
                    next_state[i + 1][j] = 1;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                gameboard[i][j] = next_state[i][j];
            }
        }
    }

    public void StartGame() {
        try {
            FillBoard();
            while (true) {
            DrawBoard();
            MoveDown();

            Thread.sleep(600);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
void main()
{
    TetrisGame game = new TetrisGame(20, 13);

    game.StartGame();
}

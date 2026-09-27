import java.util.Random;

public class Main {

    int rows = 20;
    int col = 10;
    Random random = new Random();
    int[][] gameboard = new int[rows][col];
    // 3 - это низ, 2 - приземлившаяся фигурка, 1 - летящая фигурка

    void FillBoard(int[][] board) {
        for (int i = 0; i < col; i++) {
            board[rows - 2][i] = 3;
        }
        SpawnBlock(board);
    }

    void DrawBoard(int[][] board) {

        System.out.print("\033[H\033[2J");

        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 3) {
                    System.out.print("=" + " ");
                } else if (board[i][j] == 2) {
                    System.out.print("*" + " ");
                } else {
                    System.out.print(board[i][j] == 1 ? "#" + " " : " " + " ");
                }
            }
            System.out.println();
        }
    }

    void SpawnBlock(int[][] board) {
        int seed = random.nextInt(1, 6);

        switch (seed) {
            case 1:
                board[0][col - 4] = 1;
                board[1][col - 4] = 1;
                board[1][col - 3] = 1;
                board[0][col - 3] = 1;
                break;
            case 2:
                board[0][col - 4] = 1;
                board[0][col - 3] = 1;
                board[0][col - 5] = 1;
                board[0][col - 6] = 1;
                break;
            case 3:
                board[0][col - 4] = 1;
                board[0][col - 5] = 1;
                board[0][col - 6] = 1;
                board[1][col - 6] = 1;
                break;
            case 4:
                board[0][col - 4] = 1;
                board[0][col - 5] = 1;
                board[0][col - 6] = 1;
                board[1][col - 4] = 1;
                break;
            case 5:
                board[0][col - 4] = 1;
                board[0][col - 5] = 1;
                board[1][col - 5] = 1;
                board[1][col - 6] = 1;
                break;
            case 6:
                board[1][col - 4] = 1;
                board[1][col - 5] = 1;
                board[0][col - 5] = 1;
                board[0][col - 6] = 1;
                break;
        }
    }

    void MoveDown(int[][] board) {
        int[][] next_state = new int[rows][col];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 1) {
                    next_state[i][j] = board[i][j];
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 1) continue;

                if (ShouldStop(board)) {
                    next_state[i][j] = 2;
                } else {
                    next_state[i + 1][j] = 1;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = next_state[i][j];
            }
        }
    }

    boolean ShouldStop(int[][] board) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 1) continue;

                if (i + 2 >= rows || board[i + 1][j] == 2 || board[i + 1][j] == 3) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean IsBoardFree(int[][] board) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 1) return false;
            }
        }
        return true;
    }

    void MoveBlock(int[][] board, String direction) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 1) continue;

                if (direction == "left") {
                    if (j - 1 < 0 || board[i][j - 1] == 2) return;
                }

                if (direction == "right") {
                    if (j + 1 < col || board[i][j + 1] == 2) return;
                }
            }
        }

        int[][] next = new int[rows][col];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; i < col; j++) {
                if (board[i][j] != 1) {
                    next[i][j] = board[i][j];
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; i < col; j++) {
                if (board[i][j] != 1) continue;

                if (direction == "left") {
                    next[i][j - 1] = 1;
                } else if (direction == "right") {
                    next[i][j + 1] = 1;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; i < col; j++) {
                board[i][j] = next[i][j];
            }
        }
    }

    void StartGame() {
        try {
            FillBoard(gameboard);
            while (true) {
                DrawBoard(gameboard);
                MoveDown(gameboard);

                if (IsBoardFree(gameboard)) {
                    SpawnBlock(gameboard);
                }

                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main().StartGame();
    }

}

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class Main {

    int rows = 20;
    int col = 10;
    Random random = new Random();
    int[][] gameboard = new int[rows][col];
    // 3 - это низ, 2 - приземлившаяся фигурка, 1 - летящая фигурка

    long drop_interval = 500;
    long frame_interval = 15;

    Terminal terminal;
    NonBlockingReader reader;

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

                if (direction.equals("left")) {
                    if (j - 1 < 0 || board[i][j - 1] == 2) return;
                }

                if (direction.equals("right")) {
                    if (j + 1 >= col || board[i][j + 1] == 2) return;
                }
            }
        }

        int[][] next = new int[rows][col];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 1) {
                    next[i][j] = board[i][j];
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 1) continue;

                if (direction.equals("left")) {
                    next[i][j - 1] = 1;
                } else if (direction.equals("right")) {
                    next[i][j + 1] = 1;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = next[i][j];
            }
        }
    }

    void HandleInput() {
        try {
            int inputChar = reader.read(1);

            if (inputChar == 'a' || inputChar == 'ф') {
                MoveBlock(gameboard, "left");
            } else if (inputChar == 'd' || inputChar == 'в') {
                MoveBlock(gameboard, "right");
            } else if (inputChar == 's' || inputChar == 'ы') {
                MoveDown(gameboard);
            } else if (inputChar == 'r' || inputChar == 'к') {
                Rotate(gameboard);
            } else if (inputChar == 'q' || inputChar == 'й') {
                System.exit(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void Rotate(int[][] board) {
        List<int[]> cells = new ArrayList<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < col; x++) {
                if (board[y][x] == 1) {
                    cells.add(new int[]{y, x});
                }
            }
        }

        if (cells.size() < 2) return;

        int minYBefore = Integer.MAX_VALUE;
        for (int[] c : cells) {
            if (c[0] < minYBefore) minYBefore = c[0];
        }

        int centerY = cells.get(1)[0];
        int centerX = cells.get(1)[1];

        List<int[]> rotated = new ArrayList<>();
        for (int[] c : cells) {
            int dy = c[0] - centerY;
            int dx = c[1] - centerX;

            int newY = centerY + dx;
            int newX = centerX - dy;
            rotated.add(new int[]{newY, newX});
        }

        int minYAfter = Integer.MAX_VALUE;
        for (int[] c : rotated) {
            if (c[0] < minYAfter) minYAfter = c[0];
        }

        int[] kicks = {0, 1, -1, 2, -2};

        for (int kick : kicks) {
            boolean valid = true;

            for (int[] c : rotated) {
                int y = c[0];
                int nx = c[1] + kick;

                if (y < 0 || y >= rows || nx < 0 || nx >= col
                        || board[y][nx] == 2 || board[y][nx] == 3) {
                    valid = false;
                    break;
                }
            }

            if (!valid) continue;

            for (int[] c : cells) {
                if (board[c[0]][c[1]] == 1) {
                    board[c[0]][c[1]] = 0;
                }
            }

            for (int[] c : rotated) {
                board[c[0]][c[1] + kick] = 1;
            }

            if (minYAfter < minYBefore) {
                MoveDown(board);
            }

            return;
        }
    }

    boolean IsLose(int[][] board) {
        for (int j = 0; j < col; j++) {
            if (board[0][j] == 2 || board[1][j] == 2) {
                return true;
            }
        }
        return false;
    }

    void DelRow(int[][] board) {

        for (int i = rows -2; i >= 0; i--) {
            boolean fullRow = true;
            for (int j = 0; j < col; j++) {
                if (board[i][j] != 2) {
                    fullRow = false;
                    break;
                }
            }

            if (!fullRow) continue;

            for (int r = i; r > 0; r--) {
                for (int j = 0; j < col; j++) {
                    board[r][j] = board[r-1][j] == 3 ? 0 : board[r-1][j];
                }
            }

            for (int j = 0; j < col; j++) {
                board[0][j] = 0;
            }

            i++;
        }
    }

    void StartGame() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            terminal.enterRawMode();
            reader = terminal.reader();
            long lastDropTime = System.currentTimeMillis();

            FillBoard(gameboard);
            while (true) {
                long now = System.currentTimeMillis();

                DrawBoard(gameboard);
                HandleInput();

                if (now - lastDropTime >= drop_interval) {
                    MoveDown(gameboard);
                    DelRow(gameboard);

                    if (IsBoardFree(gameboard)) {
                        SpawnBlock(gameboard);
                    }

                    if (IsLose(gameboard)) {
                        System.out.println("Lose");
                        System.exit(0);
                    }

                    lastDropTime = now;
                }

                Thread.sleep(frame_interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main().StartGame();

    }
}

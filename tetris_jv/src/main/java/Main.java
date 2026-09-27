import java.util.Random;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.InfoCmp;

public class Main {

    int rows = 20;
    int col = 10;
    int score = 0;
    int rowCount = 0;
    int level = 1;
    int currentType;
    int nextType;
    Random random = new Random();
    int[][] gameboard = new int[rows][col];
    // 3 - это низ, 2 - приземлившаяся фигурка, 1 - летящая фигурка

    long drop_interval = 500;
    long frame_interval = 15;

    Terminal terminal;
    NonBlockingReader reader;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[46m";
    private static final String WHITE = "\u001B[47m";
    private static final String GRAY = "\u001B[100m";
    private static final String BG_HIGHLIGHT = "\u001B[48;5;236m";
    private static final String DARK_GRAY = "\u001B[90m";

    private static final String[] BLOCK_COLORS = {
            "\u001B[46m", // голубой (cyan)
            "\u001B[43m", // жёлтый
            "\u001B[45m", // маджента
            "\u001B[42m", // зелёный
            "\u001B[41m", // красный
            "\u001B[44m", // синий
            "\u001B[47m"  // белый
    };

    private static final int[][][] BLOCKS = {
            {
                    {1, 1, 1, 1}
            },
            {
                    {1, 1},
                    {1, 1}
            },
            {
                    {0, 1, 0},
                    {1, 1, 1}
            },
            {
                    {0, 1, 1},
                    {1, 1, 0}
            },
            {
                    {1, 1, 0},
                    {0, 1, 1}
            },
            {
                    {1, 0, 0},
                    {1, 1, 1}
            },
            {
                    {0, 0, 1},
                    {1, 1, 1}
            }
    };


    void FillBoard(int[][] board) {
        for (int i = 0; i < col; i++) {
            board[rows - 2][i] = 3;
        }
        SpawnBlock(board);
    }

    void DrawScore(int score, int level) {
        System.out.print("\033[1;1H");
        System.out.printf("%sScore: %-6d Level: %-4d%s", GREEN, score, level, RESET);
    }

    void DrawBoard(int[][] board) {
        System.out.print("\033[3;1H");

        boolean[] activeCols = new boolean[col];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 1) {
                    activeCols[j] = true;
                }
            }
        }

        String activeColor = BLOCK_COLORS[currentType - 1];

        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 3) {
                    System.out.print(WHITE + "   " + RESET);
                } else if (board[i][j] == 2) {
                    System.out.print(GRAY + "   " + RESET);
                } else if (board[i][j] == 1) {
                    System.out.print(activeColor + "   " + RESET);
                } else {
                    if (activeCols[j]) {
                        System.out.print(BG_HIGHLIGHT + " · " + RESET);
                    } else {
                        System.out.print(DARK_GRAY + " · " + RESET);
                    }
                }
            }
            System.out.println();
        }

        DrawNextBlock();
    }

    void SpawnBlock(int[][] board) {
        if (nextType == 0) {
            nextType = random.nextInt(1, 8);
        }

        currentType = nextType;
        nextType = random.nextInt(1,8);

        int[][] shape = BLOCKS[currentType - 1];
        int startY = 0;
        int startX = col / 2 - shape[0].length / 2;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    board[startY + i][startX + j] = 1;
                }
            }
        }
    }

    void DrawNextBlock() {
        int[][] shape = BLOCKS[nextType - 1];

        int startRow = 4;
        int startCol = col * 3 + 3;

        System.out.print("\033[" + startRow + ";" + startCol + "H");
        System.out.print("Next:           ");

        for (int i = 0; i < 4; i++) {
            System.out.print("\033[" + (startRow + 2 + i) + ";" + startCol + "H");
            System.out.print("            ");
        }

        for (int i = 0; i < shape.length; i++) {
            System.out.print("\033[" + (startRow + 2 + i) + ";" + startCol + "H");

            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    System.out.print(CYAN + "   " + RESET);
                } else {
                    System.out.print("   ");
                }
            }
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
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    int[][] transpose(int[][] m) {
        int r = m.length;
        int c = m[0].length;
        int[][] t = new int[c][r];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                t[j][i] = m[i][j];
            }
        }
        return t;
    }

    int[][] rotateCW(int[][] m) {
        int[][] t = transpose(m);
        int r = t.length;
        int c = t[0].length;
        int[][] result = new int[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                result[i][j] = t[i][c - 1 - j];
            }
        }
        return result;
    }

    void Rotate(int[][] board) {
        int minY = Integer.MAX_VALUE, minX = Integer.MAX_VALUE;
        int maxY = -1, maxX = -1;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < col; x++) {
                if (board[y][x] == 1) {
                    if (y < minY) minY = y;
                    if (x < minX) minX = x;
                    if (y > maxY) maxY = y;
                    if (x > maxX) maxX = x;
                }
            }
        }

        if (maxY == -1) return;

        int h = maxY - minY + 1;
        int w = maxX - minX + 1;

        int[][] shape = new int[h][w];
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (board[y][x] == 1) {
                    shape[y - minY][x - minX] = 1;
                }
            }
        }

        int[][] rotated = rotateCW(shape);

        int newH = rotated.length;
        int newW = rotated[0].length;

        int[][] kicks = {{0, 0}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] kick : kicks) {
            int baseY = minY + kick[0];
            int baseX = minX + kick[1];

            boolean valid = true;

            for (int i = 0; i < newH; i++) {
                for (int j = 0; j < newW; j++) {
                    if (rotated[i][j] == 0) continue;

                    int y = baseY + i;
                    int x = baseX + j;

                    if (y < 0 || y >= rows || x < 0 || x >= col) { valid = false; break; }
                    if (board[y][x] == 2 || board[y][x] == 3) { valid = false; break; }
                }
                if (!valid) break;
            }

            if (!valid) continue;

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < col; x++) {
                    if (board[y][x] == 1) board[y][x] = 0;
                }
            }

            for (int i = 0; i < newH; i++) {
                for (int j = 0; j < newW; j++) {
                    if (rotated[i][j] == 1) {
                        board[baseY + i][baseX + j] = 1;
                    }
                }
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

    int DelRow(int[][] board) {

        int delRows = 0;
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
            delRows++;
            i++;
        }
        return delRows;
    }

    void ResetGame() {
        score = 0;
        level = 1;
        rowCount = 0;
        drop_interval = 500;
        gameboard = new int[rows][col];
        nextType = 0;
        currentType = 0;
    }

    void GameCycle() throws InterruptedException {
        int lastScore = -1;
        long lastDropTime = System.currentTimeMillis();

        System.out.print("\033[H\033[2J");
        FillBoard(gameboard);

        while (true) {
            long now = System.currentTimeMillis();

            if (score != lastScore) {
                DrawScore(score, level);
                lastScore = score;
            }

            DrawBoard(gameboard);
            HandleInput();

            if (now - lastDropTime >= drop_interval) {
                MoveDown(gameboard);

                int delRowCount = DelRow(gameboard);
                rowCount += delRowCount;
                switch (delRowCount) {
                    case 1 -> score += 100 * level;
                    case 2 -> score += 300 * level;
                    case 3 -> score += 500 * level;
                    case 4 -> score += 800 * level;
                }

                level = 1 + rowCount / 10;
                drop_interval = Math.max(100, 500 - (level - 1) * 50);

                if (IsBoardFree(gameboard)) {
                    SpawnBlock(gameboard);
                }

                if (IsLose(gameboard)) {
                    return;   // ← выход из цикла при проигрыше
                }

                lastDropTime = now;
            }

            Thread.sleep(frame_interval);
        }
    }

    void StartGame() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            terminal.enterRawMode();
            terminal.puts(InfoCmp.Capability.cursor_invisible);
            reader = terminal.reader();

            while (true) {
                ResetGame();
                GameCycle();

                // Показать меню
                System.out.print("\033[H\033[2J");
                System.out.println();
                System.out.println("  ╔══════════════════════════╗");
                System.out.println("  ║       GAME OVER          ║");
                System.out.println("  ╠══════════════════════════╣");
                System.out.println("  ║  Score: " + String.format("%-16s", score) + "║");
                System.out.println("  ║  Level: " + String.format("%-16s", level) + "║");
                System.out.println("  ║  Lines: " + String.format("%-16s", rowCount) + "║");
                System.out.println("  ╠══════════════════════════╣");
                System.out.println("  ║  [R] Restart             ║");
                System.out.println("  ║  [Q] Quit                ║");
                System.out.println("  ╚══════════════════════════╝");
                System.out.println();
                System.out.print("  Your choice: ");

                // Ждём ввода
                int c;
                while ((c = reader.read(1)) < 0) {
                    Thread.sleep(20);
                }

                char choice = Character.toLowerCase((char) c);

                if (choice == 'r' || choice == 'к') {
                    continue;
                } else if (choice == 'q' || choice == 'й') {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            terminal.puts(InfoCmp.Capability.cursor_visible);
            try { terminal.close(); } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        new Main().StartGame();
        System.out.println("Lose");
    }
}

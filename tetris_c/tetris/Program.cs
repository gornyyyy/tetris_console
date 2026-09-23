using System.Diagnostics;

int rows = 13;
int col = 9;
Random rand = new Random();

Stopwatch timer = new Stopwatch();
timer.Start();

Console.CursorVisible = false;

int[,] gameboard = new int[rows, col];

for (int i = 0; i < col; i++)
{
    gameboard[rows - 2, i] = 3;
}

void DrawBoard(int[,] board)
{
    for (int i = 0; i < rows - 1; i++)
    {
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] == 3)
                Console.Write("3" + " ");
            else if (board[i, j] == 2)
                Console.Write("*" + " ");
            else
            {
                Console.Write(board[i, j] == 1 ? "#" + " " : " " + " ");
            }
        }
        Console.WriteLine();
    }
}

void MoveDown(int[,] board)
{
    int[,] next = new int[rows, col];

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < col; j++)
            if (board[i, j] != 1)
                next[i, j] = board[i, j];

    bool shouldSettle = false;

    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] != 1) continue;

            if (i + 1 >= rows || board[i + 1, j] == 2 || board[i + 1, j] == 3)
            {
                shouldSettle = true;
                break;
            }
        }
        if (shouldSettle) break;
    }

    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] != 1) continue;

            if (shouldSettle)
                next[i, j] = 2;
            else
                next[i + 1, j] = 1;
        }
    }

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < col; j++)
            board[i, j] = next[i, j];
}

void SpawnBlock(int[,] board)
{
    int seed = rand.Next(1, 6);

    switch (seed)
    {
        case 1:
            board[0, col- 4] = 1;
            board[1, col- 4] = 1;
            board[1, col - 3] = 1;
            board[0, col -3] = 1;
            break;
        case 2:
            board[0, col - 4] = 1;
            board[0, col - 3] = 1;
            board[0, col - 5] = 1;
            board[0, col - 6] = 1;
            break;
        case 3:
            board[0, col - 4] = 1;
            board[0, col - 5] = 1;
            board[0, col - 6] = 1;
            board[1, col - 6] = 1;
            break;
        case 4:
            board[0, col - 4] = 1;
            board[0, col - 5] = 1;
            board[0, col - 6] = 1;
            board[1, col - 4] = 1;
            break;
        case 5:
            board[0, col - 4] = 1;
            board[0, col - 5] = 1;
            board[1, col - 5] = 1;
            board[1, col - 6] = 1;
            break;
        case 6:
            board[1, col - 4] = 1;
            board[1, col - 5] = 1;
            board[0, col - 5] = 1;
            board[0, col - 6] = 1;
            break;
    }
}

bool IsBoardFree(int[,] board)
{
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] == 1)
                return false;
        }
    return true;
}

void Move(int[,] board, string direction)
{
    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] != 1) continue;

            if (direction == "left")
            {
                if (j - 1 < 0 || board[i, j - 1] == 2 || board[i, j - 1] == 3)
                    return;
            }
            else if (direction == "right")
            {
                if (j + 1 >= col || board[i, j + 1] == 2 || board[i, j + 1] == 3)
                    return;
            }
        }
    }

    int[,] next = new int[rows, col];

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < col; j++)
            if (board[i, j] != 1)
                next[i, j] = board[i, j];

    for (int i = 0; i < rows; i++)
    {
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] != 1) continue;

            if (direction == "left")
                next[i, j - 1] = 1;
            else if (direction == "right")
                next[i, j + 1] = 1;
        }
    }

    for (int i = 0; i < rows; i++)
        for (int j = 0; j < col; j++)
            board[i, j] = next[i, j];
}

void Rotate(int[,] board)
{
    List<(int r, int c)> cells = new();
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < col; j++)
            if (board[i, j] == 1)
                cells.Add((i, j));

    if (cells.Count == 0) return;

    int centerR = cells[1].r;
    int centerC = cells[1].c;

    List<(int r, int c)> rotated = new();
    foreach (var (r, c) in cells)
    {
        int dr = r - centerR;
        int dc = c - centerC;

        int newR = centerR + dc;
        int newC = centerC - dr;
        rotated.Add((newR, newC));
    }


    int[] kicks = { 0, 1, -1, 2, -2 };

    foreach (int kick in kicks)
    {
        bool valid = true;

        foreach (var (r, c) in rotated)
        {
            int nc = c + kick;
            if (r < 0 || r >= rows || nc < 0 || nc >= col || board[r, nc] == 2 || board[r, nc] == 3)
            {
                valid = false;
                break;
            }
        }

        if (!valid) continue;

        foreach (var (r, c) in cells)
            board[r, c] = 0;

        foreach (var (r, c) in rotated)
            board[r, c + kick] = 1;

        return;
    }
}

bool IsLose(int[,] board)
{
    for (int j = 0; j < col; j++)
        if (board[0, j] == 2 || board[1, j] == 2)
            return true;

    return false;
}

void DelRow(int[,] board)
{
    for (int i = rows - 2; i >= 0; i--)
    {
        bool fullRow = true;
        for (int j = 0; j < col; j++)
        {
            if (board[i, j] != 2)
            {
                fullRow = false;
                break;
            }
        }

        if (!fullRow) continue;

        for (int r = i; r > 0; r--)
            for (int j = 0; j < col; j++)
                board[r, j] = board[r - 1, j] == 3 ? 0 : board[r - 1, j];

        for (int j = 0; j < col; j++)
            board[0, j] = 0;

        i++;
    }
}

void Tick(int[,] board)
{
    Console.SetCursorPosition(0, 0);
    DrawBoard(board);
}

while (true)
{
    if (Console.KeyAvailable)
    {
        ConsoleKeyInfo key = Console.ReadKey(intercept: true);
        switch (key.Key)
        {
            case ConsoleKey.RightArrow:
                Move(gameboard, "right");
                break;
            case ConsoleKey.LeftArrow:
                Move(gameboard, "left");
                break;
            case ConsoleKey.R:
                Rotate(gameboard);
                break;
        }

        Tick(gameboard);
    }

    if (timer.ElapsedMilliseconds >= 600)
    {
        MoveDown(gameboard);
        DelRow(gameboard);

        if (IsBoardFree(gameboard))
            SpawnBlock(gameboard);

        Tick(gameboard);

        timer.Restart();
    }

    if (IsLose(gameboard))
        break;

    await Task.Delay(10);
}

Console.WriteLine("Lose");


import React, { useState, useEffect } from 'react';
import './App.css';
import { Box, HStack, VStack, Text, Button, RadioGroup, Stack, Radio, theme, useToast } from '@chakra-ui/react';

/**
 * @returns a minesweeper app 
 * @author Haitam Chouiekh
 */
function App() {
  const cell = function (value, row, col, isRevealed, isFlagged) {
    return { value: value, row: row, col: col, isRevealed: isRevealed, isFlagged: isFlagged };
  }
  const ROWS = 16;
  const COLS = 16;
  const intialMines = 16;
  const [mines, setMineCount] = useState(intialMines);
  const toast = useToast();
  const [running, setRunning] = useState(false);
  const newBoard = [];
  const [isCtrlPress, setIsCtrlPressed] = useState(false);
  const [flags, setFlags] = useState(mines);
  const [minesFound, setMinesFound] = useState(0);
  const [unrevealedCells, setUnrevealedCells] = useState(ROWS * COLS);
  for (let i = 0; i < ROWS; i++) {
    const row = [];
    for (let j = 0; j < COLS; j++) {
      row.push(new cell("", i, j, false, false));
    }
    newBoard.push(row);
  }
  const [Board, setBoard] = useState(newBoard);
  function start() {
    setMines();
    setRunning(true);
  }

  useEffect(() => {
    document.addEventListener('keydown', (e) => {
      if (e.key === "Control") {
        setIsCtrlPressed(true);
      }
    });
    document.addEventListener('keyup', (e) => {
      setIsCtrlPressed(false);
    })
  }, [])

  useEffect(() => {
    if (minesFound === mines) {
      console.log("hi");
      toast({
        title: "You win",
        status: "success"
      })
    }
  }, [minesFound]);

  function setMines() {
    let minesCount = mines;
    const temp = [...Board];
    while (minesCount > 0) {
      let randomRow = Math.floor(Math.random() * (ROWS - 1));
      let randomCol = Math.floor(Math.random() * (COLS - 1));
      let tempCell = temp[randomRow][randomCol];
      while (tempCell.value === "💣") {
        randomRow = Math.floor(Math.random() * (ROWS - 1));
        randomCol = Math.floor(Math.random() * (COLS - 1));
        tempCell = temp[randomRow][randomCol];
      }
      temp[randomRow][randomCol].value = "💣";
      if (randomRow - 1 >= 0 && randomCol - 1 >= 0 && temp[randomRow - 1][randomCol - 1].value !== "💣") temp[randomRow - 1][randomCol - 1].value++;
      if (randomRow + 1 < 16 && randomCol + 1 < 16 && temp[randomRow + 1][randomCol + 1].value !== "💣") temp[randomRow + 1][randomCol + 1].value++;
      if (randomRow - 1 >= 0 && randomCol + 1 < 16 && temp[randomRow - 1][randomCol + 1].value !== "💣") temp[randomRow - 1][randomCol + 1].value++;
      if (randomRow + 1 < 16 && randomCol - 1 >= 0 && temp[randomRow + 1][randomCol - 1].value !== "💣") temp[randomRow + 1][randomCol - 1].value++;
      if (randomRow - 1 >= 0 && temp[randomRow - 1][randomCol].value !== "💣") temp[randomRow - 1][randomCol].value++;
      if (randomCol - 1 >= 0 && temp[randomRow][randomCol - 1].value !== "💣") temp[randomRow][randomCol - 1].value++;
      if (randomRow + 1 < 16 && temp[randomRow + 1][randomCol].value !== "💣") temp[randomRow + 1][randomCol].value++;
      if (randomCol + 1 < 16 && temp[randomRow][randomCol + 1].value !== "💣") temp[randomRow][randomCol + 1].value++;
      minesCount--;
    }
    setBoard(temp);
  }

  function revealButton(elem) {
    if (elem.isRevealed === false && elem.isFlagged === false) {
      const temp = [...Board];
      temp[elem.row][elem.col].isRevealed = true;
      setBoard(temp);
      if (elem.value === "💣") {
        for (let x = 0; x < ROWS; x++) {
          for (let y = 0; y < COLS; y++) {
            temp[x][y].isRevealed = true;
          }
        }
        toast({
          title: "You lose",
          status: "error",
          position: "top"
        })
      }
      else if (elem.value === "") {
        if (elem.row - 1 >= 0 && elem.col - 1 >= 0) revealButton(temp[elem.row - 1][elem.col - 1]);
        if (elem.row + 1 < 16 && elem.col + 1 < 16) revealButton(temp[elem.row + 1][elem.col + 1]);
        if (elem.row - 1 >= 0 && elem.col + 1 < 16) revealButton(temp[elem.row - 1][elem.col + 1]);
        if (elem.row + 1 < 16 && elem.col - 1 >= 0) revealButton(temp[elem.row + 1][elem.col - 1]);
        if (elem.row - 1 >= 0) revealButton(temp[elem.row - 1][elem.col]);
        if (elem.col - 1 >= 0) revealButton(temp[elem.row][elem.col - 1]);
        if (elem.row + 1 < 16) revealButton(temp[elem.row + 1][elem.col]);
        if (elem.col + 1 < 16) revealButton(temp[elem.row][elem.col + 1]);
      }
    }
  }

  function flagButton(elem) {
    const temp = [...Board];
    if (temp[elem.row][elem.col].isFlagged === false && flags > 0) {
      temp[elem.row][elem.col].isFlagged = true;
      setFlags(flags - 1);
      if (temp[elem.row][elem.col].value === "💣") {
        const n = minesFound + 1;
        setMinesFound(n);
      }
    } else if (temp[elem.row][elem.col].isFlagged === true) {
      temp[elem.row][elem.col].isFlagged = false;
      setFlags(flags + 1);
      if (temp[elem.row][elem.col].value === "💣") {
        setMinesFound(minesFound - 1);
      }
    }
    setBoard(temp);
  }
  
  function rightColor(elem) {
    if (elem.value != "💣") {
      if (elem.value === 1) return theme.colors.blue[500];
      if (elem.value === 2) return theme.colors.cyan[500];
      if (elem.value === 3) return theme.colors.green[500];
      if (elem.value === 4) return theme.colors.yellow[500];
      if (elem.value === 5) return theme.colors.orange[500];
      if (elem.value === 6) return theme.colors.red[500];
      if (elem.value === 4) return theme.colors.purple[500];
      if (elem.value === 4) return theme.colors.pink[500];
    }
  }

  function displayFlag(elem) {
    if (elem.isFlagged === true) {
      return "🚩";
    }
  }

  function Game(props) {
    const isRunning = props.isRunning;
    if (isRunning) {
      return (
        <div>
          {
            Board.map((col, i) => {
              return (
                <VStack key={i} spacing="0">
                  <HStack spacing="0">
                    {
                      col.map((elem, j) => {
                        return (
                          <Box key={j} w="3rem" h="3rem" display="flex" border="solid" borderWidth="0.05px" alignItems="center" justifyContent="center" bg={theme.colors.blackAlpha[100]}>
                            {
                              !elem.isRevealed &&
                              <Button w="3rem" h="3rem" borderRadius="0" border="solid" borderWidth="0.1px" colorScheme="blackAlpha" onClick={(e) => {
                                if (isCtrlPress) {
                                  flagButton(elem);
                                } else {
                                  revealButton(elem)
                                }
                              }}>
                                {displayFlag(elem)}
                              </Button>
                            }
                            {
                              elem.isRevealed &&
                              <Text display="flex" alignItems="center" justifyContent="center" color={rightColor(elem)}>
                                {elem.value}
                              </Text>
                            }
                          </Box>
                        )
                      })
                    }
                  </HStack>
                </VStack>
              )
            })
          }
        </div>
      )
    } else return (
      <VStack spacing="3rem">
        <RadioGroup defaultValue={mines} onChange = {setMineCount}>
          <Stack spacing={4} direction='row'>
            <Radio value='16'>
              Easy
            </Radio>
            <Radio value='32'>Medium</Radio>
            <Radio value='64'>Hard</Radio>
          </Stack>
        </RadioGroup>
        <Box display="flex" alignItems="center" justifyContent="center">
          <Button color="white" w="10rem" bg="grey" onClick={(e) => start()}>
            Start Game ▶️
          </Button>
        </Box>
      </VStack>
    )
  }
  return (
    <div className="App">
      <header className="App-header">
        <Box padding ="1rem"> 
          Minesweap
        </Box>
      </header>
      <main>
        <Game isRunning={running}></Game>
      </main>
    </div>
  );
}

export default App;

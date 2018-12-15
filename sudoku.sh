#!/bin/bash

printf "\nCompile java programm. Executing javac Main.java\n"
javac Main.java
printf "Ready.\n"
if [[ $1 == "h" ]]; then
  printf "\nGood day.\n"
  printf "My name is Daniel and this is a simple Sudoku solver program.\n"
  printf "\n"
  printf "Format for the entries in the given file should be row_num,col_num,val for the\n"
  printf "values that are not default, the rest is assumed empty.\n\n"
  printf "In case of absence of file's name, the option will be given to entry values through your console.\n\n"
  printf "5 test files are provided for testing this small programm.\n"
  printf "sudokuTest.txt - typical Sudoku puzzle to solve.\n"
  printf "twoInBoxRow.txt - Sudoku puzzle with repeating values in the same row and box, which is against rules.\n"
  printf "empty.txt - empty file with no entries in it.\n"
#  printf "solved.txt - file with already complete Sudoku.\n"
  printf "letter.txt - typical Sudoku puzzle, but with some letters insted of numbers\n"
  printf "unsolvable.txt - Sudoku puzzle with no possible solution\n\n"
else
  printf "\nLaunching java programm. Executing java Main $1\n\n"
  java Main $1
fi

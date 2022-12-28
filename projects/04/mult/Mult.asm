// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

// Put your code here.

//  
// sum = 0
// count = 1
// while(count <= r1){
//     sum = sum + r0      
// }
// r2 = sum
//

(START)
@sum
M=0

@count
M=1

(LOOP)
@R1
D=M

@count
D = M - D 

@END
D;JGT

@R0
D = M

@sum
M = M + D

@count
M = M + 1 

@LOOP
0;JMP

(END)
@sum
D = M

@R2
M = D 

@START
0;JMP
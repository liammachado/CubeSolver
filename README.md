# CubeSolver
Computes the solution to a Rubik's Cube.
Enter the scramble used to mix up the cube, give it a few seconds, and it will return a solution that will solve the cube. The input scramble is a series of characters that represent the moves executed on the cube:

R- right face clockwise 90 degrees
L- left face clockwise 90 degrees 
U- top face clockwise 90 degrees
D- bottom face clockwise 90 degrees
F- front face clockwise 90 degrees
B- back face clockwise 90 degrees

if a ''' is in front of the letter, this means to turn the face 90 degrees counter-clockwise instead of clockwise. For example, U' means to turn the top face 90 degrees counter-clockwise. If a '2' is in front of the letter, this means to turn the face 180 degrees. For example, U2 means to turn the top face 180 degrees.

Here is an example of an input scramble:

R2 B' U2 B D2 R2 D2 L2 B' R2 F D' L2 R2 F' U' L U2 R F' U

This means first turn the right face 180 degrees, then turn the back face 90 degrees counter-clockwise, then turn the top face 180 degrees, etc.

The program uses the Thistlewaite algorithm, which splits the state of the cube into 4 phases.

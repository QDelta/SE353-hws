Install Glasgow Haskell Compiler, then run `make` to build the main executable.

Run `./main $INPUT_FILE` to get the results.

Alternatively, run `runghc Main.hs $INPUT_FILE` if you do not want to compile
the project, but it may takes more time compared to optimized native code.

The code is tested on GHC version 9.2.2
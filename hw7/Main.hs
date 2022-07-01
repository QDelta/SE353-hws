module Main where

import System.Environment (getArgs)
import Data.List (sort)
import Parser (parse)
import qualified BruteForce as BF
import qualified DPLL
import Common

main :: IO ()
main = do
  srcFile : _ <- getArgs
  probText <- readFile srcFile
  let prob = parse probText
  putStr "Brute force: "
  putStrLn (showSol (BF.solve prob))
  putStr "DPLL:        "
  putStrLn (showSol (DPLL.solve prob))

showSol :: Maybe Solution -> String
showSol Nothing = "unsat"
showSol (Just binds) = show (map f (sort binds)) where
  f (x, True ) = x
  f (x, False) = -x

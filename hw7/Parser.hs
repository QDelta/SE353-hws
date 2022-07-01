module Parser (Problem, parse) where

import Data.Maybe (mapMaybe)
import qualified Data.Set as S
import Common

parse :: String -> Problem
parse s =
  let cs = mapMaybe parseLine (lines s)
  in (genVars cs, genClauses cs)
  where
    genVars = S.toList . S.fromList . map abs . concat
    genClauses = map (map genAtom)
    genAtom n | n >= 0    = Is n
              | otherwise = Not (- n)

parseLine :: String -> Maybe [Int]
parseLine s = let ws = words s in
  if valid ws
  then Just (pa ws)
  else Nothing
  where
    valid l = not (null l) && (head l /= "p") && (head l /= "c")
    pa []       = []
    pa [a]      = []
    pa (a : al) = read a : pa al
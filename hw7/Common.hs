module Common where

import Data.Maybe (mapMaybe)

data Atom = Is Int | Not Int deriving (Show, Eq, Ord)
type Clause = [Atom]
type Problem = ([Int], [Clause])
type Solution = [(Int, Bool)]

varOfAtom :: Atom -> Int
varOfAtom (Is  x) = x
varOfAtom (Not x) = x

immSatTest :: [Clause] -> Maybe Bool
immSatTest [] = Just True
immSatTest cs = if any null cs
                then Just False
                else Nothing

data CAtom = Var Atom | Const Bool deriving (Eq)
type CClause = [CAtom]

assign :: (Int, Bool) -> [Clause] -> [Clause]
assign p = mapMaybe (assignClause p)

assignClause :: (Int, Bool) -> Clause -> Maybe Clause
assignClause p = simpl . map (assignA p) where
  assignA :: (Int, Bool) -> Atom -> CAtom
  assignA (x, b) a = case a of
    Is  x' | x == x' -> Const b
    Not x' | x == x' -> Const (not b)
    _                -> Var a
  simpl :: CClause -> Maybe Clause
  simpl [] = Just []
  simpl (Const True  : cal) = Nothing
  simpl (Const False : cal) = simpl cal
  simpl (Var a       : cal) = fmap (a :) (simpl cal)
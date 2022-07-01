module DPLL (solve) where

import Data.Maybe (mapMaybe)
import Data.List (mapAccumL)
import qualified Data.Map as M
import qualified Data.Set as S
import Common

type SProblem = (S.Set Int, [Clause])

solve :: Problem -> Maybe Solution
solve (vars, cs) = sSolve (S.fromList vars, cs)

sSolve :: SProblem -> Maybe Solution
sSolve p =
  let (sol1, (vars, cs)) = simplify p in
  fmap (sol1 ++) (case immSatTest cs of
    Just True  -> Just (zip (S.toList vars) (repeat True))
    Just False -> Nothing
    Nothing ->
      let (v, vs) = S.deleteFindMin vars
          try1 = sSolve (vs, assign (v, True ) cs)
          try2 = sSolve (vs, assign (v, False) cs)
      in fmap ((v, True) :) try1 <|> fmap ((v, False) :) try2)
  where
    Just a  <|> _ = Just a
    Nothing <|> b = b

simplify :: SProblem -> (Solution, SProblem)
simplify p@(vars, cs) =
  let dAtoms = S.fromList (findUnits cs ++ findPLits p) in
  if null dAtoms then
    ([], p)
  else
    let (nextCs, thisSol) = mapAccumL simplify1 cs (S.toList dAtoms)
        assignedVars = S.map varOfAtom dAtoms
        nextVars = vars S.\\ assignedVars
        (nextSol, finalP) = simplify (nextVars, nextCs)
        sol = thisSol ++ nextSol
    in (sol, finalP)
  where
    simplify1 ::  [Clause] -> Atom -> ([Clause], (Int, Bool))
    simplify1 cs (Is  x) = (assign (x, True ) cs, (x, True ))
    simplify1 cs (Not x) = (assign (x, False) cs, (x, False))

findUnits :: [Clause] -> [Atom]
findUnits = mapMaybe f where
  f [a] = Just a
  f _   = Nothing

findPLits :: SProblem -> [Atom]
findPLits (vars, cs) = (mapMaybe id . M.elems) vOncesMap where
  vOncesMap = M.map justOnce vOccurMap
  vOccurMap = foldl count (M.fromList (zip (S.toList vars) (repeat None))) cs
  count = foldl occurOne
  occurOne m a = M.update (Just . addOne a) (varOfAtom a) m
  justOnce :: Occur -> Maybe Atom
  justOnce (Once a) = Just a
  justOnce _        = Nothing

data Occur = None | Once Atom | Many

addOne :: Atom -> Occur -> Occur
addOne a None     = Once a
addOne a (Once _) = Many
addOne _ Many     = Many
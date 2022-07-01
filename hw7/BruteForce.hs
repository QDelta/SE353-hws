module BruteForce (solve) where

import Common

solve :: Problem -> Maybe Solution
solve (vars, cs) = case immSatTest cs of
  Just True  -> Just (zip vars (repeat True))
  Just False -> Nothing
  Nothing ->
    let v : vl = vars
        try1 = solve (vl, assign (v, True ) cs)
        try2 = solve (vl, assign (v, False) cs)
    in fmap ((v, True) :) try1 <|> fmap ((v, False) :) try2
  where
    Just a  <|> _ = Just a
    Nothing <|> b = b
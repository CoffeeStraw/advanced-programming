module TestMSet where

import Data.Char (toLower)
import Data.List (sort)
import MultiSet (MSet, add, empty, getList, subeq, union)

-- Get the CIAO of a String, i.e. the original string but with its chars all lowercase and in alphabetical order
ciao :: String -> String
ciao = sort . map toLower

-- Returns an MSet containing the CIAO of the words in a file
readMSet :: FilePath -> IO (MSet String)
readMSet = fmap (foldr add empty . map ciao . words) . readFile

-- Writes in a file at "path", one per line,
-- each element of the given MSet with its multiplicity in the format "<elem> - <multiplicity>"
writeMSet :: FilePath -> MSet String -> IO ()
writeMSet path = writeFile path . unlines . map (\(v, k) -> v ++ " - " ++ show k) . getList

main :: IO ()
main = do
  m1 <- readMSet "aux_files/anagram.txt"
  m2 <- readMSet "aux_files/anagram-s1.txt"
  m3 <- readMSet "aux_files/anagram-s2.txt"
  m4 <- readMSet "aux_files/margana2.txt"

  -- NOTE: the following code requires O(n^2) time,
  --       but we could have done it in O(nlogn) if we had a method for obtaining
  --       the distinct elements of an MSet, by comparing the two sorted lists of distinct elements
  if (subeq m1 m4 || subeq m4 m1) && not (m1 == m4)
    then putStrLn "Multisets m1 and m4 are not equal, but they have the same elements"
    else putStrLn "ERROR: Multisets m1 and m4 are either equal, or have different elements"

  if m1 == union m2 m3
    then putStrLn "Multisets m1 and m2+m3 are equal"
    else putStrLn "ERROR: Multisets m1 and m2+m3 are not equal"

  writeMSet "anag-out.txt" m1
  writeMSet "gana-out.txt" m4

module MultiSet
  ( MSet (..),
    empty,
    getList,
    insert,
    add,
    occs,
    elems,
    subeq,
    union,
    mapMSet,
  )
where

import Data.Maybe (fromMaybe)

-- Type constructor
data MSet a = MS [(a, Int)] deriving (Show)

-- Empty constructor (equal to empty list)
empty :: MSet a
empty = MS []

-- Helper function: get the list of the MSet
getList :: MSet a -> [(a, Int)]
getList (MS l) = l

-- Helper function: insert an element with the given multiplicity k in the MSet
insert :: (Eq a) => a -> Int -> MSet a -> MSet a
insert v k = MS . insert' . getList
  where
    insert' [] = [(v, k)]
    insert' ((v', x) : xs)
      | v == v' = (v, x + k) : xs
      | otherwise = (v', x) : insert' xs

-- Add an element to the mset
add :: (Eq a) => a -> MSet a -> MSet a
add = flip insert 1

-- Get multiplicity of "v" in the MSet
-- NOTE: I've implemented a different order than the one required in order to eta-reduce
occs :: (Eq a) => a -> MSet a -> Int
occs v = fromMaybe 0 . lookup v . getList

-- Get a list containing all the elements of the MSet
-- NOTE: As I've interpreted it, elements with multiplicity > 1
--       appear multiple times in the returned list
elems :: MSet a -> [a]
elems = concatMap (uncurry $ flip replicate) . getList

-- Return True if each element of "mset1" is also an element of "mset2" with at least the same multiplicity
subeq :: (Eq a) => MSet a -> MSet a -> Bool
subeq mset1 mset2 = all (\(v, k) -> k <= occs v mset2) (getList mset1)

-- Return a new MSet having all the elements of "mset1" and "mset2", each with the sum of the corresponding multiplicities
-- NOTE: The union is done using the "insert" function, effectively adding the elements of the 2nd MSet to the elements of the 1st one.
--       This is done to ensure well-formedness
union :: (Eq a) => MSet a -> MSet a -> MSet a
union mset1 = foldr (uncurry insert) mset1 . getList

-- Class contructor instances
instance (Eq a) => Eq (MSet a) where
  (==) :: (Eq a) => MSet a -> MSet a -> Bool
  mset1 == mset2 = subeq mset1 mset2 && subeq mset2 mset1

instance Foldable MSet where
  -- NOTE: As per the documentation, the "foldr" method of "Foldable" is minimal:
  --       https://hackage.haskell.org/package/base-4.17.0.0/docs/Data-Foldable.html
  foldr :: (a -> b -> b) -> b -> MSet a -> b
  foldr f z = foldr (f . fst) z . getList

-- Map an MSet of type "a" to an MSet of type "b" by applying function "f"
-- NOTE: it is not possible to define an instance of "Functor" for MSet by providing the following method
--       as the implementation of fmap. Functors are defined to work for every "a" and "b",
--       not only for those having constraints like "Eq a" or "Eq b".
--       However, in our case, "Eq b" is needed to ensure well-formedness
mapMSet :: (Eq b) => (a -> b) -> MSet a -> MSet b
mapMSet f = foldr (uncurry (insert . f)) empty . getList

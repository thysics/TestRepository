"""
Unit tests for the pair selection module.
"""

import unittest
import numpy as np
import pandas as pd
from pair_selection import PairSelector
from data_generation import MarketDataGenerator


class TestPairSelector(unittest.TestCase):
    """
    Test cases for the PairSelector class.
    """
    
    def setUp(self):
        """
        Set up test fixtures.
        """
        self.n_stocks = 10
        self.n_days = 500
        self.n_cointegrated_pairs = 3
        self.seed = 42
        
        # Generate synthetic data with known cointegrated pairs
        self.generator = MarketDataGenerator(
            n_stocks=self.n_stocks,
            n_days=self.n_days,
            n_cointegrated_pairs=self.n_cointegrated_pairs,
            seed=self.seed
        )
        self.data = self.generator.generate_data()
        self.known_pairs = self.generator.get_cointegrated_pairs()
        
        # Create pair selector
        self.pair_selector = PairSelector()
        
    def test_find_cointegrated_pairs(self):
        """
        Test the find_cointegrated_pairs method.
        """
        # Find cointegrated pairs
        found_pairs = self.pair_selector.find_cointegrated_pairs(self.data)
        
        # Check that at least some pairs are found
        self.assertGreater(len(found_pairs), 0)
        
        # Check that each pair contains valid stock names
        for stock1, stock2 in found_pairs:
            self.assertIn(stock1, self.data.columns)
            self.assertIn(stock2, self.data.columns)
            self.assertNotEqual(stock1, stock2)
        
        # Check that the pair stats are populated
        self.assertGreater(len(self.pair_selector.pair_stats), 0)
        
        # Check that at least some of the known pairs are found
        # Note: This is a probabilistic test, as the cointegration test may not find all pairs
        found_pair_set = {(stock1, stock2) for stock1, stock2 in found_pairs}
        known_pair_set = {(stock1, stock2) for stock1, stock2 in self.known_pairs}
        
        # Check if there's any overlap between found and known pairs
        overlap = found_pair_set.intersection(known_pair_set)
        self.assertGreater(len(overlap), 0, "None of the known cointegrated pairs were found")
        
    def test_get_pair_stats(self):
        """
        Test the get_pair_stats method.
        """
        # Find cointegrated pairs
        found_pairs = self.pair_selector.find_cointegrated_pairs(self.data)
        
        # Get pair stats
        pair_stats = self.pair_selector.get_pair_stats()
        
        # Check that stats are returned for each found pair
        for stock1, stock2 in found_pairs:
            pair_key = f"{stock1}_{stock2}"
            self.assertIn(pair_key, pair_stats)
            
            # Check that the required stats are present
            self.assertIn("p_value", pair_stats[pair_key])
            self.assertIn("hedge_ratio", pair_stats[pair_key])
            self.assertIn("half_life", pair_stats[pair_key])
            
            # Check that the values are reasonable
            self.assertLess(pair_stats[pair_key]["p_value"], 0.05)  # p-value should be significant
            self.assertGreater(pair_stats[pair_key]["half_life"], 0)  # half-life should be positive
            
    def test_get_hedge_ratio(self):
        """
        Test the get_hedge_ratio method.
        """
        # Find cointegrated pairs
        found_pairs = self.pair_selector.find_cointegrated_pairs(self.data)
        
        if found_pairs:
            # Get hedge ratio for the first pair
            stock1, stock2 = found_pairs[0]
            hedge_ratio = self.pair_selector.get_hedge_ratio(stock1, stock2)
            
            # Check that the hedge ratio is a float
            self.assertIsInstance(hedge_ratio, float)
            
            # Check that the hedge ratio is reasonable
            self.assertGreater(hedge_ratio, 0)
            
            # Check that an error is raised for a non-existent pair
            with self.assertRaises(ValueError):
                self.pair_selector.get_hedge_ratio("NON_EXISTENT_1", "NON_EXISTENT_2")


if __name__ == "__main__":
    unittest.main()
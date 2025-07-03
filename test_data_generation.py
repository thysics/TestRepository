"""
Unit tests for the data generation module.
"""

import unittest
import numpy as np
import pandas as pd
from data_generation import MarketDataGenerator, split_data


class TestMarketDataGenerator(unittest.TestCase):
    """
    Test cases for the MarketDataGenerator class.
    """
    
    def setUp(self):
        """
        Set up test fixtures.
        """
        self.n_stocks = 10
        self.n_days = 500
        self.n_cointegrated_pairs = 3
        self.seed = 42
        
        self.generator = MarketDataGenerator(
            n_stocks=self.n_stocks,
            n_days=self.n_days,
            n_cointegrated_pairs=self.n_cointegrated_pairs,
            seed=self.seed
        )
        
    def test_generate_data(self):
        """
        Test the generate_data method.
        """
        data = self.generator.generate_data()
        
        # Check that the data has the correct shape
        self.assertEqual(data.shape, (self.n_days, self.n_stocks))
        
        # Check that the data has the correct column names
        self.assertEqual(len(data.columns), self.n_stocks)
        for i in range(1, self.n_stocks + 1):
            self.assertIn(f"STOCK_{i}", data.columns)
        
        # Check that the data has a date index
        self.assertIsInstance(data.index, pd.DatetimeIndex)
        self.assertEqual(len(data.index), self.n_days)
        
    def test_get_cointegrated_pairs(self):
        """
        Test the get_cointegrated_pairs method.
        """
        # Generate data
        data = self.generator.generate_data()
        
        # Get cointegrated pairs
        pairs = self.generator.get_cointegrated_pairs()
        
        # Check that the correct number of pairs is returned
        self.assertEqual(len(pairs), self.n_cointegrated_pairs)
        
        # Check that each pair contains valid stock names
        for stock1, stock2 in pairs:
            self.assertIn(stock1, data.columns)
            self.assertIn(stock2, data.columns)
            self.assertNotEqual(stock1, stock2)


class TestSplitData(unittest.TestCase):
    """
    Test cases for the split_data function.
    """
    
    def setUp(self):
        """
        Set up test fixtures.
        """
        self.n_stocks = 5
        self.n_days = 100
        
        # Create a simple DataFrame
        np.random.seed(42)
        data = np.random.randn(self.n_days, self.n_stocks)
        self.df = pd.DataFrame(
            data,
            columns=[f"STOCK_{i}" for i in range(1, self.n_stocks + 1)]
        )
        
    def test_split_data(self):
        """
        Test the split_data function.
        """
        train_ratio = 0.7
        train_df, test_df = split_data(self.df, train_ratio=train_ratio)
        
        # Check that the split is correct
        expected_train_size = int(self.n_days * train_ratio)
        expected_test_size = self.n_days - expected_train_size
        
        self.assertEqual(len(train_df), expected_train_size)
        self.assertEqual(len(test_df), expected_test_size)
        
        # Check that the columns are preserved
        self.assertEqual(list(train_df.columns), list(self.df.columns))
        self.assertEqual(list(test_df.columns), list(self.df.columns))
        
        # Check that the data is correctly split
        pd.testing.assert_frame_equal(train_df, self.df.iloc[:expected_train_size])
        pd.testing.assert_frame_equal(test_df, self.df.iloc[expected_train_size:])


if __name__ == "__main__":
    unittest.main()
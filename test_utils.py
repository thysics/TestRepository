"""
Unit tests for the utils module.
"""

import unittest
import numpy as np
import pandas as pd
import os
from utils import (
    calculate_sharpe_ratio,
    calculate_max_drawdown,
    test_cointegration,
    calculate_zscore,
    plot_pair_trading_results
)


class TestUtils(unittest.TestCase):
    """
    Test cases for the utility functions.
    """
    
    def test_calculate_sharpe_ratio(self):
        """
        Test the calculate_sharpe_ratio function.
        """
        # Test with a simple array of returns
        returns = np.array([0.01, -0.005, 0.02, 0.015, -0.01])
        sharpe = calculate_sharpe_ratio(returns)
        
        # Check that the result is a float
        self.assertIsInstance(sharpe, float)
        
        # Check that the result is reasonable
        self.assertGreater(sharpe, 0)  # Positive returns should give positive Sharpe
        
        # Test with zero returns
        zero_returns = np.zeros(10)
        zero_sharpe = calculate_sharpe_ratio(zero_returns)
        self.assertEqual(zero_sharpe, 0.0)
        
        # Test with empty array
        empty_sharpe = calculate_sharpe_ratio(np.array([]))
        self.assertEqual(empty_sharpe, 0.0)
        
    def test_calculate_max_drawdown(self):
        """
        Test the calculate_max_drawdown function.
        """
        # Test with a simple array of cumulative returns
        cum_returns = np.array([1.0, 1.1, 1.05, 1.15, 1.1, 1.0, 1.05])
        drawdown = calculate_max_drawdown(cum_returns)
        
        # Check that the result is a float
        self.assertIsInstance(drawdown, float)
        
        # Check that the result is reasonable
        self.assertGreaterEqual(drawdown, 0)  # Drawdown should be non-negative
        self.assertLessEqual(drawdown, 1)  # Drawdown should be at most 100%
        
        # Expected drawdown: from 1.15 to 1.0, which is (1.0 - 1.15) / 1.15 = -0.13 -> 0.13
        expected_drawdown = 0.13
        self.assertAlmostEqual(drawdown, expected_drawdown, places=2)
        
        # Test with empty array
        empty_drawdown = calculate_max_drawdown(np.array([]))
        self.assertEqual(empty_drawdown, 0.0)
        
    def test_test_cointegration(self):
        """
        Test the test_cointegration function.
        """
        # Create a cointegrated pair
        np.random.seed(42)
        x = np.random.randn(100).cumsum()
        y = 0.5 * x + np.random.randn(100) * 0.5
        
        # Test cointegration
        is_cointegrated, p_value = test_cointegration(x, y)
        
        # Check that the results are of the correct type
        self.assertIsInstance(is_cointegrated, bool)
        self.assertIsInstance(p_value, float)
        
        # Check that the p-value is between 0 and 1
        self.assertGreaterEqual(p_value, 0)
        self.assertLessEqual(p_value, 1)
        
        # The pair should be cointegrated
        self.assertTrue(is_cointegrated)
        
        # Create a non-cointegrated pair
        z = np.random.randn(100).cumsum()
        
        # Test cointegration
        is_cointegrated, p_value = test_cointegration(x, z)
        
        # The pair should not be cointegrated
        self.assertFalse(is_cointegrated)
        
    def test_calculate_zscore(self):
        """
        Test the calculate_zscore function.
        """
        # Create a simple spread
        np.random.seed(42)
        spread = np.random.randn(100)
        
        # Calculate z-score
        window = 20
        zscore = calculate_zscore(spread, window=window)
        
        # Check that the result has the correct shape
        self.assertEqual(len(zscore), len(spread))
        
        # Check that the first window values are zero
        self.assertTrue(np.all(zscore[:window] == 0))
        
        # Check that the z-score is reasonable
        self.assertTrue(np.isfinite(zscore).all())
        
    def test_plot_pair_trading_results(self):
        """
        Test the plot_pair_trading_results function.
        """
        # Create sample data
        np.random.seed(42)
        n = 100
        prices1 = np.random.randn(n).cumsum() + 50
        prices2 = 0.5 * prices1 + np.random.randn(n) * 2 + 20
        spread = prices2 - 0.5 * prices1
        zscore = calculate_zscore(spread)
        positions = np.zeros(n)
        positions[zscore > 1.5] = -1
        positions[zscore < -1.5] = 1
        cumulative_returns = np.random.randn(n).cumsum() * 0.1
        
        # Plot results
        plot_pair_trading_results(
            prices1, prices2, spread, zscore, positions, cumulative_returns,
            pair_names=('Stock A', 'Stock B')
        )
        
        # Check that the plot file is created
        self.assertTrue(os.path.exists('pair_trading_results.png'))
        
        # Clean up
        os.remove('pair_trading_results.png')


if __name__ == "__main__":
    unittest.main()
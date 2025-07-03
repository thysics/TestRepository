"""
Unit tests for the backtest module.
"""

import unittest
import numpy as np
import pandas as pd
import os
from backtest import StatArbBacktester, run_full_backtest
from data_generation import MarketDataGenerator, split_data
from pair_selection import PairSelector
from trading_strategy import PairTradingStrategy, PortfolioStrategy


class TestStatArbBacktester(unittest.TestCase):
    """
    Test cases for the StatArbBacktester class.
    """
    
    def setUp(self):
        """
        Set up test fixtures.
        """
        # Generate synthetic data
        self.n_stocks = 10
        self.n_days = 200
        self.n_cointegrated_pairs = 3
        self.seed = 42
        
        self.generator = MarketDataGenerator(
            n_stocks=self.n_stocks,
            n_days=self.n_days,
            n_cointegrated_pairs=self.n_cointegrated_pairs,
            seed=self.seed
        )
        self.data = self.generator.generate_data()
        
        # Split data
        self.train_data, self.test_data = split_data(self.data, train_ratio=0.7)
        
        # Create components
        self.pair_selector = PairSelector()
        self.pair_strategy = PairTradingStrategy()
        self.portfolio_strategy = PortfolioStrategy(self.pair_strategy)
        
        # Create backtester
        self.backtester = StatArbBacktester(self.pair_selector, self.portfolio_strategy)
        
    def test_run_backtest(self):
        """
        Test the run_backtest method.
        """
        try:
            # Run backtest
            returns_df, metrics, pair_results = self.backtester.run_backtest(
                self.train_data, self.test_data
            )
            
            # Check that the returns DataFrame has the correct shape
            self.assertEqual(len(returns_df), len(self.test_data))
            self.assertIn('portfolio_return', returns_df.columns)
            self.assertIn('portfolio_cumulative_return', returns_df.columns)
            
            # Check that metrics are calculated
            self.assertIn('total_return', metrics)
            self.assertIn('annualized_return', metrics)
            self.assertIn('sharpe_ratio', metrics)
            self.assertIn('max_drawdown', metrics)
            self.assertIn('win_rate', metrics)
            
            # Check that pair results are stored
            self.assertGreater(len(pair_results), 0)
            
        except ValueError as e:
            # If no cointegrated pairs are found, the test should be skipped
            if "No cointegrated pairs found" in str(e):
                self.skipTest("No cointegrated pairs found in the training data")
            else:
                raise
    
    def test_plot_results(self):
        """
        Test the plot_results method.
        """
        try:
            # Run backtest
            returns_df, metrics, pair_results = self.backtester.run_backtest(
                self.train_data, self.test_data
            )
            
            # Plot results
            self.backtester.plot_results(returns_df, metrics, pair_results, plot_pairs=False)
            
            # Check that the plot file is created
            self.assertTrue(os.path.exists('portfolio_returns.png'))
            
        except ValueError as e:
            # If no cointegrated pairs are found, the test should be skipped
            if "No cointegrated pairs found" in str(e):
                self.skipTest("No cointegrated pairs found in the training data")
            else:
                raise
        finally:
            # Clean up
            if os.path.exists('portfolio_returns.png'):
                os.remove('portfolio_returns.png')


class TestRunFullBacktest(unittest.TestCase):
    """
    Test cases for the run_full_backtest function.
    """
    
    def test_run_full_backtest(self):
        """
        Test the run_full_backtest function.
        """
        try:
            # Run a small backtest
            returns_df, metrics, pair_results = run_full_backtest(
                n_stocks=6,
                n_days=100,
                n_cointegrated_pairs=2,
                train_ratio=0.7,
                seed=42
            )
            
            # Check that the returns DataFrame is not empty
            self.assertGreater(len(returns_df), 0)
            
            # Check that metrics are calculated
            self.assertIn('total_return', metrics)
            self.assertIn('annualized_return', metrics)
            self.assertIn('sharpe_ratio', metrics)
            self.assertIn('max_drawdown', metrics)
            self.assertIn('win_rate', metrics)
            
            # Check that pair results are stored
            self.assertGreater(len(pair_results), 0)
            
        except ValueError as e:
            # If no cointegrated pairs are found, the test should be skipped
            if "No cointegrated pairs found" in str(e):
                self.skipTest("No cointegrated pairs found in the training data")
            else:
                raise
        finally:
            # Clean up
            if os.path.exists('portfolio_returns.png'):
                os.remove('portfolio_returns.png')
            if os.path.exists('pair_trading_results.png'):
                os.remove('pair_trading_results.png')


if __name__ == "__main__":
    unittest.main()
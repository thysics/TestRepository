"""
Unit tests for the trading strategy module.
"""

import unittest
import numpy as np
import pandas as pd
from trading_strategy import PairTradingStrategy, PortfolioStrategy
from data_generation import MarketDataGenerator
from pair_selection import PairSelector


class TestPairTradingStrategy(unittest.TestCase):
    """
    Test cases for the PairTradingStrategy class.
    """
    
    def setUp(self):
        """
        Set up test fixtures.
        """
        # Create a simple pair of stocks
        self.n_days = 200
        np.random.seed(42)
        
        # Create a cointegrated pair
        self.price1 = np.random.randn(self.n_days).cumsum() + 50  # Random walk starting at 50
        self.hedge_ratio = 0.7
        self.spread = np.random.randn(self.n_days) * 0.5  # Random noise
        self.price2 = self.hedge_ratio * self.price1 + self.spread  # Cointegrated with price1
        
        # Create strategy
        self.strategy = PairTradingStrategy(
            entry_threshold=1.5,
            exit_threshold=0.5,
            stop_loss_threshold=3.0,
            lookback_period=20,
            max_position_days=10
        )
        
    def test_generate_signals(self):
        """
        Test the generate_signals method.
        """
        # Generate signals
        spread, zscore, position1, position2 = self.strategy.generate_signals(
            self.price1, self.price2, self.hedge_ratio
        )
        
        # Check that the outputs have the correct shape
        self.assertEqual(len(spread), self.n_days)
        self.assertEqual(len(zscore), self.n_days)
        self.assertEqual(len(position1), self.n_days)
        self.assertEqual(len(position2), self.n_days)
        
        # Check that the spread is calculated correctly
        expected_spread = self.price2 - self.hedge_ratio * self.price1
        np.testing.assert_allclose(spread, expected_spread)
        
        # Check that positions are consistent with the hedge ratio
        for i in range(self.n_days):
            if position1[i] != 0:
                self.assertAlmostEqual(position2[i] / position1[i], -self.hedge_ratio, places=5)
        
        # Check that positions are only taken after the lookback period
        self.assertTrue(np.all(position1[:self.strategy.lookback_period] == 0))
        self.assertTrue(np.all(position2[:self.strategy.lookback_period] == 0))
        
    def test_calculate_returns(self):
        """
        Test the calculate_returns method.
        """
        # Generate signals
        spread, zscore, position1, position2 = self.strategy.generate_signals(
            self.price1, self.price2, self.hedge_ratio
        )
        
        # Calculate returns
        daily_returns, cumulative_returns = self.strategy.calculate_returns(
            self.price1, self.price2, position1, position2
        )
        
        # Check that the outputs have the correct shape
        self.assertEqual(len(daily_returns), self.n_days)
        self.assertEqual(len(cumulative_returns), self.n_days)
        
        # Check that daily returns are zero when no position is held
        for i in range(self.n_days):
            if position1[i-1] == 0 and position2[i-1] == 0 and i > 0:
                self.assertEqual(daily_returns[i], 0)
        
        # Check that cumulative returns are calculated correctly
        expected_cumulative_returns = np.cumprod(1 + daily_returns) - 1
        np.testing.assert_allclose(cumulative_returns, expected_cumulative_returns)


class TestPortfolioStrategy(unittest.TestCase):
    """
    Test cases for the PortfolioStrategy class.
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
        
        # Find cointegrated pairs
        self.pair_selector = PairSelector()
        self.pairs = self.pair_selector.find_cointegrated_pairs(self.data)
        
        # Create strategies
        self.pair_strategy = PairTradingStrategy()
        self.portfolio_strategy = PortfolioStrategy(
            pair_trading_strategy=self.pair_strategy,
            max_pairs=2,
            capital_per_pair=0.5
        )
        
    def test_backtest(self):
        """
        Test the backtest method.
        """
        # Skip if no pairs found
        if not self.pairs:
            self.skipTest("No cointegrated pairs found")
            
        # Get hedge ratios
        hedge_ratios = {f"{stock1}_{stock2}": self.pair_selector.pair_stats[f"{stock1}_{stock2}"]["hedge_ratio"] 
                       for stock1, stock2 in self.pairs}
        
        # Run backtest
        returns_df, metrics, pair_results = self.portfolio_strategy.backtest(
            self.data, self.pairs, hedge_ratios
        )
        
        # Check that the returns DataFrame has the correct shape
        self.assertEqual(len(returns_df), self.n_days)
        self.assertIn('portfolio_return', returns_df.columns)
        self.assertIn('portfolio_cumulative_return', returns_df.columns)
        
        # Check that metrics are calculated
        self.assertIn('total_return', metrics)
        self.assertIn('annualized_return', metrics)
        self.assertIn('sharpe_ratio', metrics)
        self.assertIn('max_drawdown', metrics)
        self.assertIn('win_rate', metrics)
        
        # Check that pair results are stored
        self.assertEqual(len(pair_results), min(len(self.pairs), self.portfolio_strategy.max_pairs))
        
        # Check the first pair result
        if self.pairs:
            stock1, stock2 = self.pairs[0]
            pair_key = f"{stock1}_{stock2}"
            if pair_key in pair_results:
                result = pair_results[pair_key]
                self.assertIn('spread', result)
                self.assertIn('zscore', result)
                self.assertIn('position1', result)
                self.assertIn('position2', result)
                self.assertIn('daily_returns', result)
                self.assertIn('cumulative_returns', result)


if __name__ == "__main__":
    unittest.main()